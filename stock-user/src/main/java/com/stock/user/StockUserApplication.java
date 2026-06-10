package com.stock.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.stock.user.mapper")
public class StockUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockUserApplication.class, args);
    }
}
