package com.stock.detect;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.stock.detect.mapper")
@EnableFeignClients(basePackages = "com.stock.common.feign")
public class DetectApplication {
    public static void main(String[] args) {
        SpringApplication.run(DetectApplication.class, args);
    }
}
