package com.stock.data.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("daily_basic")
public class DailyBasic {
    private String tsCode;
    private String tradeDate;
    @TableField("`close`")
    private BigDecimal close;
    private BigDecimal peTtm;
    private BigDecimal pb;
    private BigDecimal totalMv;
    private BigDecimal circMv;
}
