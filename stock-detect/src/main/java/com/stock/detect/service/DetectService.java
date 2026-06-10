package com.stock.detect.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stock.common.feign.StockDataFeignClient;
import com.stock.common.result.Result;
import com.stock.detect.entity.DetectAlert;
import com.stock.detect.mapper.DetectAlertMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class DetectService {

    private final DetectAlertMapper alertMapper;
    private final StockDataFeignClient stockDataFeignClient;

    /** 已启用规则集合 */
    private final Set<String> enabledRules = new HashSet<>(Set.of(
            "LIMIT_UP", "LIMIT_DOWN", "VOL_BREAK", "CONSEC_UP", "CONSEC_DOWN"));

    public DetectService(DetectAlertMapper alertMapper,
                         StockDataFeignClient stockDataFeignClient) {
        this.alertMapper = alertMapper;
        this.stockDataFeignClient = stockDataFeignClient;
    }

    /** 获取所有规则状态 */
    public Result<?> getRules() {
        List<Map<String, Object>> rules = new ArrayList<>();
        String[] types = {"LIMIT_UP", "LIMIT_DOWN", "VOL_BREAK", "CONSEC_UP", "CONSEC_DOWN"};
        String[] names = {"涨停检测", "跌停检测", "放量突破", "连续上涨", "持续下跌"};
        for (int i = 0; i < types.length; i++) {
            Map<String, Object> rule = new HashMap<>();
            rule.put("type", types[i]);
            rule.put("name", names[i]);
            rule.put("enabled", enabledRules.contains(types[i]));
            rules.add(rule);
        }
        return Result.success(rules);
    }

    /** 切换规则开关 */
    public Result<?> toggleRule(String type) {
        if (enabledRules.contains(type)) {
            enabledRules.remove(type);
            return Result.success("规则 " + type + " 已禁用");
        } else {
            enabledRules.add(type);
            return Result.success("规则 " + type + " 已启用");
        }
    }

    /** 对单只股票执行全量检测 */
    public void detectOne(String tsCode, String name, List<Map<String, Object>> klineData) {
        if (klineData == null || klineData.size() < 3) return;

        int lastIdx = klineData.size() - 1;
        Map<String, Object> today = klineData.get(lastIdx);

        String tradeDate = (String) today.get("tradeDate");
        BigDecimal pctChg = getBigDecimal(today, "pctChg");
        BigDecimal vol = getBigDecimal(today, "vol");

        if (pctChg == null) return;

        // 涨停检测
        if (enabledRules.contains("LIMIT_UP") && pctChg.compareTo(new BigDecimal("9.8")) >= 0) {
            saveAlert(tsCode, name, tradeDate, "LIMIT_UP",
                    String.format("涨停！涨幅 %.2f%%", pctChg));
        }

        // 跌停检测
        if (enabledRules.contains("LIMIT_DOWN") && pctChg.compareTo(new BigDecimal("-9.8")) <= 0) {
            saveAlert(tsCode, name, tradeDate, "LIMIT_DOWN",
                    String.format("跌停！跌幅 %.2f%%", pctChg));
        }

        // 放量突破：5日均量
        if (enabledRules.contains("VOL_BREAK") && vol != null) {
            long sumVol = 0;
            int count = 0;
            for (int i = lastIdx - 5; i < lastIdx && i >= 0; i--) {
                Map<String, Object> d = klineData.get(i);
                BigDecimal v = getBigDecimal(d, "vol");
                if (v != null) {
                    sumVol += v.longValue();
                    count++;
                }
            }
            if (count > 0) {
                long avgVol = sumVol / count;
                if (vol.longValue() > avgVol * 2) {
                    saveAlert(tsCode, name, tradeDate, "VOL_BREAK",
                            String.format("放量突破！量比 %.1f 倍", vol.doubleValue() / avgVol));
                }
            }
        }

        // 连续涨跌检测
        if (enabledRules.contains("CONSEC_UP") || enabledRules.contains("CONSEC_DOWN")) {
            int consecUp = 0, consecDown = 0;
            BigDecimal cumChg = BigDecimal.ZERO;
            for (int i = lastIdx; i >= 0; i--) {
                BigDecimal chg = getBigDecimal(klineData.get(i), "pctChg");
                if (chg == null) break;
                if (chg.compareTo(BigDecimal.ZERO) > 0) {
                    if (consecDown > 0) break;
                    consecUp++;
                    cumChg = cumChg.add(chg);
                } else if (chg.compareTo(BigDecimal.ZERO) < 0) {
                    if (consecUp > 0) break;
                    consecDown++;
                    cumChg = cumChg.add(chg);
                } else {
                    break;
                }
            }

            if (enabledRules.contains("CONSEC_UP") && consecUp >= 3 && cumChg.compareTo(new BigDecimal("10")) > 0) {
                saveAlert(tsCode, name, tradeDate, "CONSEC_UP",
                        String.format("连涨%d天，累计涨幅 %.2f%%", consecUp, cumChg));
            }
            if (enabledRules.contains("CONSEC_DOWN") && consecDown >= 3 && cumChg.compareTo(new BigDecimal("-10")) < 0) {
                saveAlert(tsCode, name, tradeDate, "CONSEC_DOWN",
                        String.format("连跌%d天，累计跌幅 %.2f%%", consecDown, cumChg.abs()));
            }
        }
    }

    public Result<?> getAlerts(String type, String date, Integer page, Integer size) {
        QueryWrapper<DetectAlert> wrapper = new QueryWrapper<>();
        if (type != null && !type.isEmpty()) wrapper.eq("alert_type", type);
        if (date != null && !date.isEmpty()) wrapper.eq("trade_date", date);
        wrapper.orderByDesc("created_at");

        long total = alertMapper.selectCount(wrapper);
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 20 : size;

        wrapper.last("LIMIT " + ((p - 1) * s) + "," + s);
        List<DetectAlert> list = alertMapper.selectList(wrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("page", p);
        result.put("size", s);
        result.put("records", list);
        return Result.success(result);
    }

    private void saveAlert(String tsCode, String name, String tradeDate, String type, String msg) {
        // 去重：同一天同一股票同一类型不重复
        QueryWrapper<DetectAlert> wrapper = new QueryWrapper<>();
        wrapper.eq("ts_code", tsCode).eq("trade_date", tradeDate).eq("alert_type", type);
        if (alertMapper.selectCount(wrapper) > 0) return;

        DetectAlert alert = new DetectAlert();
        alert.setTsCode(tsCode);
        alert.setName(name);
        alert.setTradeDate(tradeDate);
        alert.setAlertType(type);
        alert.setAlertMsg(msg);
        alertMapper.insert(alert);
    }

    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof BigDecimal) return (BigDecimal) val;
        if (val instanceof Number) return new BigDecimal(val.toString());
        return null;
    }
}
