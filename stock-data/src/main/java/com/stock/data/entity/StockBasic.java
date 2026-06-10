package com.stock.data.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("stock_basic")
public class StockBasic {
    private String tsCode;
    private String symbol;
    private String name;
    private String area;
    private String industry;
    private String listDate;
    private String market;
    private String listStatus;
}
