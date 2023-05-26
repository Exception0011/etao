package com.huangkai.etao_order_service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.huangkai.etao_order_service.mapper")
public class EtaoOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtaoOrderServiceApplication.class, args);
    }

}
