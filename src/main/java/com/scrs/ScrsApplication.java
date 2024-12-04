package com.scrs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.scrs.mapper")
public class ScrsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScrsApplication.class, args);
    }

}
