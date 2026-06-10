package com.stock.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("daily_quote")
public class DailyQuote {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String tsCode;
    private String tradeDate;
    @TableField("`open`")
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    @TableField("`close`")
    private BigDecimal close;
    private BigDecimal preClose;
    @TableField("`change`")
    private BigDecimal change;
    private BigDecimal pctChg;
    private Long vol;
    private BigDecimal amount;
}
