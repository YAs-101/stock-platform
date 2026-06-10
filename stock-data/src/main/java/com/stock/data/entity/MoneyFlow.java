package com.stock.data.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("money_flow")
public class MoneyFlow {
    private String tsCode;
    private String tradeDate;
    private BigDecimal buyLg;
    private BigDecimal sellLg;
    private BigDecimal buyElg;
    private BigDecimal sellElg;
    private BigDecimal netMf;
}
