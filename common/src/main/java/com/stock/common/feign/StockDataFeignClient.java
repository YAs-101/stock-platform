package com.stock.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "stock-data")
public interface StockDataFeignClient {

    @GetMapping("/stock/search")
    Map<String, Object> search(@RequestParam("keyword") String keyword);

    @GetMapping("/stock/kline")
    Map<String, Object> kline(@RequestParam("tsCode") String tsCode,
                              @RequestParam(value = "startDate", required = false) String startDate,
                              @RequestParam(value = "endDate", required = false) String endDate);

    @GetMapping("/stock/moneyflow")
    Map<String, Object> moneyFlow(@RequestParam("tsCode") String tsCode,
                                   @RequestParam(value = "startDate", required = false) String startDate,
                                   @RequestParam(value = "endDate", required = false) String endDate);

    @GetMapping("/stock/basic")
    Map<String, Object> basic(@RequestParam("tsCode") String tsCode,
                              @RequestParam(value = "startDate", required = false) String startDate,
                              @RequestParam(value = "endDate", required = false) String endDate);

    @GetMapping("/stock/detail/{tsCode}")
    Map<String, Object> detail(@PathVariable("tsCode") String tsCode);
}
