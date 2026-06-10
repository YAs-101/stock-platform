package com.stock.stat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.stock.common.feign")
public class StatApplication {
    public static void main(String[] args) {
        SpringApplication.run(StatApplication.class, args);
    }
}
