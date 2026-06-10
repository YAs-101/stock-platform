package com.stock.stat.service;

import com.stock.common.feign.StockDataFeignClient;
import com.stock.common.result.Result;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatService {

    private final StockDataFeignClient stockDataFeignClient;

    public StatService(StockDataFeignClient stockDataFeignClient) {
        this.stockDataFeignClient = stockDataFeignClient;
    }

    /** 从 Feign 响应中提取 data 列表 */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getDataList(Map<String, Object> response) {
        Object data = response.get("data");
        return (data instanceof List) ? (List<Map<String, Object>>) data : Collections.emptyList();
    }

    /** 涨幅排行 */
    public Result<?> rankGainers(int n) {
        Map<String, Object> result = stockDataFeignClient.search("");
        List<Map<String, Object>> stocks = getDataList(result);
        List<Map<String, Object>> rank = new ArrayList<>();

        for (Map<String, Object> stock : stocks) {
            String tsCode = (String) stock.get("tsCode");
            String name = (String) stock.get("name");
            Map<String, Object> klineRes = stockDataFeignClient.kline(tsCode, null, null);
            List<Map<String, Object>> klines = getDataList(klineRes);
            if (klines.isEmpty()) continue;

            Map<String, Object> latest = klines.get(klines.size() - 1);
            Object pctChg = latest.get("pctChg");

            Map<String, Object> item = new HashMap<>();
            item.put("tsCode", tsCode);
            item.put("name", name);
            item.put("pctChg", pctChg);
            item.put("close", latest.get("close"));
            rank.add(item);
        }

        rank.sort((a, b) -> {
            BigDecimal bd = toBigDecimal(b.get("pctChg"));
            BigDecimal ad = toBigDecimal(a.get("pctChg"));
            return bd.compareTo(ad);
        });

        return Result.success(rank.subList(0, Math.min(n, rank.size())));
    }

    /** 跌幅排行 */
    public Result<?> rankLosers(int n) {
        Map<String, Object> result = stockDataFeignClient.search("");
        List<Map<String, Object>> stocks = getDataList(result);
        List<Map<String, Object>> rank = new ArrayList<>();

        for (Map<String, Object> stock : stocks) {
            String tsCode = (String) stock.get("tsCode");
            String name = (String) stock.get("name");
            Map<String, Object> klineRes = stockDataFeignClient.kline(tsCode, null, null);
            List<Map<String, Object>> klines = getDataList(klineRes);
            if (klines.isEmpty()) continue;

            Map<String, Object> latest = klines.get(klines.size() - 1);
            Object pctChg = latest.get("pctChg");

            Map<String, Object> item = new HashMap<>();
            item.put("tsCode", tsCode);
            item.put("name", name);
            item.put("pctChg", pctChg);
            item.put("close", latest.get("close"));
            rank.add(item);
        }

        rank.sort((a, b) -> {
            BigDecimal bd = toBigDecimal(b.get("pctChg"));
            BigDecimal ad = toBigDecimal(a.get("pctChg"));
            return ad.compareTo(bd);
        });

        return Result.success(rank.subList(0, Math.min(n, rank.size())));
    }

    /** 成交量排行 */
    public Result<?> rankVolume(int n) {
        Map<String, Object> result = stockDataFeignClient.search("");
        List<Map<String, Object>> stocks = getDataList(result);
        List<Map<String, Object>> rank = new ArrayList<>();

        for (Map<String, Object> stock : stocks) {
            String tsCode = (String) stock.get("tsCode");
            String name = (String) stock.get("name");
            Map<String, Object> klineRes = stockDataFeignClient.kline(tsCode, null, null);
            List<Map<String, Object>> klines = getDataList(klineRes);
            if (klines.isEmpty()) continue;

            Map<String, Object> latest = klines.get(klines.size() - 1);
            Object vol = latest.get("vol");

            Map<String, Object> item = new HashMap<>();
            item.put("tsCode", tsCode);
            item.put("name", name);
            item.put("vol", vol);
            item.put("pctChg", latest.get("pctChg"));
            rank.add(item);
        }

        rank.sort((a, b) -> {
            Long bv = toLong(b.get("vol"));
            Long av = toLong(a.get("vol"));
            return bv.compareTo(av);
        });

        return Result.success(rank.subList(0, Math.min(n, rank.size())));
    }

    /** 资金净流入排行 */
    public Result<?> rankMoneyFlow(int n) {
        Map<String, Object> result = stockDataFeignClient.search("");
        List<Map<String, Object>> stocks = getDataList(result);
        List<Map<String, Object>> rank = new ArrayList<>();

        for (Map<String, Object> stock : stocks) {
            String tsCode = (String) stock.get("tsCode");
            String name = (String) stock.get("name");
            Map<String, Object> mfRes = stockDataFeignClient.moneyFlow(tsCode, null, null);
            List<Map<String, Object>> mfs = getDataList(mfRes);
            if (mfs.isEmpty()) continue;

            Map<String, Object> latest = mfs.get(mfs.size() - 1);
            Object netMf = latest.get("netMf");

            Map<String, Object> item = new HashMap<>();
            item.put("tsCode", tsCode);
            item.put("name", name);
            item.put("netMf", netMf);
            rank.add(item);
        }

        rank.sort((a, b) -> {
            BigDecimal bd = toBigDecimal(b.get("netMf"));
            BigDecimal ad = toBigDecimal(a.get("netMf"));
            return bd.compareTo(ad);
        });

        return Result.success(rank.subList(0, Math.min(n, rank.size())));
    }

    /** 行业涨跌统计 */
    public Result<?> industryStat() {
        Map<String, Object> result = stockDataFeignClient.search("");
        List<Map<String, Object>> stocks = getDataList(result);

        Map<String, List<BigDecimal>> industryMap = new HashMap<>();

        for (Map<String, Object> stock : stocks) {
            String tsCode = (String) stock.get("tsCode");
            String industry = (String) stock.get("industry");
            if (industry == null || industry.isEmpty()) continue;

            Map<String, Object> klineRes = stockDataFeignClient.kline(tsCode, null, null);
            List<Map<String, Object>> klines = getDataList(klineRes);
            if (klines.isEmpty()) continue;

            Map<String, Object> latest = klines.get(klines.size() - 1);
            BigDecimal pctChg = toBigDecimal(latest.get("pctChg"));
            if (pctChg == null) continue;

            industryMap.computeIfAbsent(industry, k -> new ArrayList<>()).add(pctChg);
        }

        List<Map<String, Object>> stats = new ArrayList<>();
        for (Map.Entry<String, List<BigDecimal>> entry : industryMap.entrySet()) {
            List<BigDecimal> values = entry.getValue();
            BigDecimal sum = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avg = sum.divide(new BigDecimal(values.size()), 4, BigDecimal.ROUND_HALF_UP);
            long upCount = values.stream().filter(v -> v.compareTo(BigDecimal.ZERO) > 0).count();

            Map<String, Object> item = new HashMap<>();
            item.put("industry", entry.getKey());
            item.put("count", values.size());
            item.put("avgPctChg", avg);
            item.put("upRatio", values.size() > 0 ? (double) upCount / values.size() : 0);
            stats.add(item);
        }

        stats.sort((a, b) -> {
            BigDecimal bd = (BigDecimal) b.get("avgPctChg");
            BigDecimal ad = (BigDecimal) a.get("avgPctChg");
            return bd.compareTo(ad);
        });

        return Result.success(stats);
    }

    /** 市盈率排行 */
    public Result<?> rankPE(int n, String order) {
        Map<String, Object> result = stockDataFeignClient.search("");
        List<Map<String, Object>> stocks = getDataList(result);
        List<Map<String, Object>> rank = new ArrayList<>();

        for (Map<String, Object> stock : stocks) {
            String tsCode = (String) stock.get("tsCode");
            String name = (String) stock.get("name");
            Map<String, Object> basicRes = stockDataFeignClient.basic(tsCode, null, null);
            List<Map<String, Object>> basics = getDataList(basicRes);
            if (basics.isEmpty()) continue;

            Map<String, Object> latest = basics.get(basics.size() - 1);
            Object peTtm = latest.get("peTtm");
            BigDecimal pe = toBigDecimal(peTtm);
            if (pe == null || pe.compareTo(BigDecimal.ZERO) <= 0) continue;

            Map<String, Object> item = new HashMap<>();
            item.put("tsCode", tsCode);
            item.put("name", name);
            item.put("peTtm", pe);
            item.put("pb", latest.get("pb"));
            rank.add(item);
        }

        boolean asc = "asc".equalsIgnoreCase(order);
        rank.sort((a, b) -> {
            BigDecimal bd = (BigDecimal) b.get("peTtm");
            BigDecimal ad = (BigDecimal) a.get("peTtm");
            return asc ? ad.compareTo(bd) : bd.compareTo(ad);
        });

        return Result.success(rank.subList(0, Math.min(n, rank.size())));
    }

    /** 总市值排行 */
    public Result<?> rankMarketCap(int n) {
        Map<String, Object> result = stockDataFeignClient.search("");
        List<Map<String, Object>> stocks = getDataList(result);
        List<Map<String, Object>> rank = new ArrayList<>();

        for (Map<String, Object> stock : stocks) {
            String tsCode = (String) stock.get("tsCode");
            String name = (String) stock.get("name");
            Map<String, Object> basicRes = stockDataFeignClient.basic(tsCode, null, null);
            List<Map<String, Object>> basics = getDataList(basicRes);
            if (basics.isEmpty()) continue;

            Map<String, Object> latest = basics.get(basics.size() - 1);
            Object totalMv = latest.get("totalMv");

            Map<String, Object> item = new HashMap<>();
            item.put("tsCode", tsCode);
            item.put("name", name);
            item.put("totalMv", totalMv);
            rank.add(item);
        }

        rank.sort((a, b) -> {
            BigDecimal bd = toBigDecimal(b.get("totalMv"));
            BigDecimal ad = toBigDecimal(a.get("totalMv"));
            return bd.compareTo(ad);
        });

        return Result.success(rank.subList(0, Math.min(n, rank.size())));
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val instanceof BigDecimal) return (BigDecimal) val;
        if (val instanceof Number) return new BigDecimal(val.toString());
        if (val instanceof String) {
            try { return new BigDecimal((String) val); } catch (Exception e) { return null; }
        }
        return null;
    }

    private Long toLong(Object val) {
        if (val instanceof Long) return (Long) val;
        if (val instanceof Number) return ((Number) val).longValue();
        return 0L;
    }
}
