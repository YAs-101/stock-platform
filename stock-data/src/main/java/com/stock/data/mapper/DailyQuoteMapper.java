package com.stock.data.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stock.data.entity.DailyQuote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DailyQuoteMapper extends BaseMapper<DailyQuote> {

    default List<DailyQuote> selectByCodeAndDateRange(String tsCode, String startDate, String endDate) {
        QueryWrapper<DailyQuote> wrapper = new QueryWrapper<>();
        wrapper.eq("ts_code", tsCode);
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge("trade_date", startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le("trade_date", endDate);
        }
        wrapper.orderByAsc("trade_date");
        return selectList(wrapper);
    }

    default DailyQuote selectLatestByCode(String tsCode) {
        QueryWrapper<DailyQuote> wrapper = new QueryWrapper<>();
        wrapper.eq("ts_code", tsCode)
               .orderByDesc("trade_date")
               .last("LIMIT 1");
        return selectOne(wrapper);
    }
}
