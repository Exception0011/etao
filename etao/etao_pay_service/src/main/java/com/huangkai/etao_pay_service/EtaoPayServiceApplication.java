package com.huangkai.etao_pay_service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.huangkai.etao_pay_service.mapper")
public class EtaoPayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtaoPayServiceApplication.class, args);
    }

}
