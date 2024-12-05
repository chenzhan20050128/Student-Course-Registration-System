package com.scrs;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@MapperScan("com.scrs.mapper")
public class ScrsApplication {
    public static void main(String[] args) {

        SpringApplication.run(ScrsApplication.class, args);
        log.info("ScrsApplication started successfully!!!");
    }

}
