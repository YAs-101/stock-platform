package com.stock.data.controller;

import com.stock.common.result.Result;
import com.stock.data.service.StockDataService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
public class StockDataController {

    private final StockDataService stockDataService;

    public StockDataController(StockDataService stockDataService) {
        this.stockDataService = stockDataService;
    }

    @GetMapping("/search")
    public Result<?> search(@RequestParam("keyword") String keyword) {
        return stockDataService.search(keyword);
    }

    @GetMapping("/detail/{tsCode}")
    public Result<?> detail(@PathVariable String tsCode) {
        return stockDataService.detail(tsCode);
    }

    @GetMapping("/kline")
    public Result<?> kline(@RequestParam("tsCode") String tsCode,
                           @RequestParam(value = "startDate", required = false) String startDate,
                           @RequestParam(value = "endDate", required = false) String endDate) {
        return stockDataService.kline(tsCode, startDate, endDate);
    }

    @GetMapping("/basic")
    public Result<?> basic(@RequestParam("tsCode") String tsCode,
                           @RequestParam(value = "startDate", required = false) String startDate,
                           @RequestParam(value = "endDate", required = false) String endDate) {
        return stockDataService.basic(tsCode, startDate, endDate);
    }

    @GetMapping("/moneyflow")
    public Result<?> moneyFlow(@RequestParam("tsCode") String tsCode,
                               @RequestParam(value = "startDate", required = false) String startDate,
                               @RequestParam(value = "endDate", required = false) String endDate) {
        return stockDataService.moneyFlow(tsCode, startDate, endDate);
    }

    @GetMapping("/hot")
    public Result<?> hot() {
        return stockDataService.hot();
    }
}
