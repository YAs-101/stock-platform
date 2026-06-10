package com.stock.stat.controller;

import com.stock.common.result.Result;
import com.stock.stat.service.StatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stat")
public class StatController {

    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    @GetMapping("/rank/gainers")
    public Result<?> gainers(@RequestParam(value = "n", defaultValue = "20") int n) {
        return statService.rankGainers(n);
    }

    @GetMapping("/rank/losers")
    public Result<?> losers(@RequestParam(value = "n", defaultValue = "20") int n) {
        return statService.rankLosers(n);
    }

    @GetMapping("/rank/volume")
    public Result<?> volume(@RequestParam(value = "n", defaultValue = "20") int n) {
        return statService.rankVolume(n);
    }

    @GetMapping("/rank/moneyflow")
    public Result<?> moneyFlow(@RequestParam(value = "n", defaultValue = "20") int n) {
        return statService.rankMoneyFlow(n);
    }

    @GetMapping("/industry")
    public Result<?> industry() {
        return statService.industryStat();
    }

    @GetMapping("/rank/pe")
    public Result<?> pe(@RequestParam(value = "n", defaultValue = "20") int n,
                        @RequestParam(value = "order", defaultValue = "asc") String order) {
        return statService.rankPE(n, order);
    }

    @GetMapping("/rank/marketcap")
    public Result<?> marketCap(@RequestParam(value = "n", defaultValue = "20") int n) {
        return statService.rankMarketCap(n);
    }
}
