package com.stock.data;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.stock.data.mapper")
public class StockDataApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockDataApplication.class, args);
    }
}
