package com.stock.detect.controller;

import com.stock.common.feign.StockDataFeignClient;
import com.stock.common.result.Result;
import com.stock.detect.service.DetectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/detect")
public class DetectController {

    private final DetectService detectService;
    private final StockDataFeignClient stockDataFeignClient;

    public DetectController(DetectService detectService,
                            StockDataFeignClient stockDataFeignClient) {
        this.detectService = detectService;
        this.stockDataFeignClient = stockDataFeignClient;
    }

    /** 手动触发全量检测 */
    @PostMapping("/run")
    public Result<?> runDetect() {
        Map<String, Object> searchResult = stockDataFeignClient.search("");
        Object data = searchResult.get("data");
        if (!(data instanceof List)) return Result.error("无法获取股票列表");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> stocks = (List<Map<String, Object>>) data;
        int count = 0;
        for (Map<String, Object> stock : stocks) {
            String tsCode = (String) stock.get("tsCode");
            String name = (String) stock.get("name");
            Map<String, Object> klineResult = stockDataFeignClient.kline(tsCode, null, null);
            Object klineData = klineResult.get("data");
            if (klineData instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> klines = (List<Map<String, Object>>) klineData;
                detectService.detectOne(tsCode, name, klines);
                count++;
            }
        }
        return Result.success("检测完成，共处理 " + count + " 只股票");
    }

    /** 分页查询预警记录 */
    @GetMapping("/alerts")
    public Result<?> alerts(@RequestParam(value = "type", required = false) String type,
                            @RequestParam(value = "date", required = false) String date,
                            @RequestParam(value = "page", required = false) Integer page,
                            @RequestParam(value = "size", required = false) Integer size) {
        return detectService.getAlerts(type, date, page, size);
    }

    /** 获取所有规则状态 */
    @GetMapping("/rules")
    public Result<?> rules() {
        return detectService.getRules();
    }

    /** 切换规则开关 */
    @PutMapping("/rule/{type}/toggle")
    public Result<?> toggleRule(@PathVariable String type) {
        return detectService.toggleRule(type);
    }
}
