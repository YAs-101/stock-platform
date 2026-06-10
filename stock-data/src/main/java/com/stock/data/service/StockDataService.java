package com.stock.data.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stock.common.result.Result;
import com.stock.data.entity.*;
import com.stock.data.mapper.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StockDataService {

    private final StockBasicMapper stockBasicMapper;
    private final DailyQuoteMapper dailyQuoteMapper;
    private final DailyBasicMapper dailyBasicMapper;
    private final MoneyFlowMapper moneyFlowMapper;

    public StockDataService(StockBasicMapper stockBasicMapper,
                            DailyQuoteMapper dailyQuoteMapper,
                            DailyBasicMapper dailyBasicMapper,
                            MoneyFlowMapper moneyFlowMapper) {
        this.stockBasicMapper = stockBasicMapper;
        this.dailyQuoteMapper = dailyQuoteMapper;
        this.dailyBasicMapper = dailyBasicMapper;
        this.moneyFlowMapper = moneyFlowMapper;
    }

    /** 按名称/代码模糊搜索股票 */
    public Result<?> search(String keyword) {
        QueryWrapper<StockBasic> wrapper = new QueryWrapper<>();
        wrapper.eq("list_status", "L")
               .inSql("ts_code", "SELECT DISTINCT ts_code FROM daily_quote")
               .and(w -> w.like("name", keyword).or().like("ts_code", keyword).or().like("symbol", keyword))
               .last("LIMIT 20");
        List<StockBasic> list = stockBasicMapper.selectList(wrapper);
        return Result.success(list);
    }

    /** 股票基本信息 */
    public Result<?> detail(String tsCode) {
        StockBasic stock = stockBasicMapper.selectById(tsCode);
        if (stock == null) return Result.error("股票不存在");
        return Result.success(stock);
    }

    /** 日K线数据 */
    public Result<?> kline(String tsCode, String startDate, String endDate) {
        List<DailyQuote> list = dailyQuoteMapper.selectByCodeAndDateRange(tsCode, startDate, endDate);
        return Result.success(list);
    }

    /** 基本面指标 */
    public Result<?> basic(String tsCode, String startDate, String endDate) {
        QueryWrapper<DailyBasic> wrapper = new QueryWrapper<>();
        wrapper.eq("ts_code", tsCode);
        if (startDate != null && !startDate.isEmpty()) wrapper.ge("trade_date", startDate);
        if (endDate != null && !endDate.isEmpty()) wrapper.le("trade_date", endDate);
        wrapper.orderByAsc("trade_date");
        return Result.success(dailyBasicMapper.selectList(wrapper));
    }

    /** 资金流向 */
    public Result<?> moneyFlow(String tsCode, String startDate, String endDate) {
        QueryWrapper<MoneyFlow> wrapper = new QueryWrapper<>();
        wrapper.eq("ts_code", tsCode);
        if (startDate != null && !startDate.isEmpty()) wrapper.ge("trade_date", startDate);
        if (endDate != null && !endDate.isEmpty()) wrapper.le("trade_date", endDate);
        wrapper.orderByAsc("trade_date");
        return Result.success(moneyFlowMapper.selectList(wrapper));
    }

    /** 热门股票（按最新日成交量 Top 20） */
    public Result<?> hot() {
        QueryWrapper<DailyQuote> latestWrapper = new QueryWrapper<>();
        latestWrapper.select("MAX(trade_date) as trade_date");
        Map<String, Object> dateMap = dailyQuoteMapper.selectMaps(latestWrapper).get(0);
        String latestDate = (String) dateMap.get("trade_date");

        QueryWrapper<DailyQuote> wrapper = new QueryWrapper<>();
        wrapper.eq("trade_date", latestDate)
               .orderByDesc("vol")
               .last("LIMIT 20");
        return Result.success(dailyQuoteMapper.selectList(wrapper));
    }
}
