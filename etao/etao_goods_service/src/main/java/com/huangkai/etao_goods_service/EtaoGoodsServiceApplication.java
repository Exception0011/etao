package com.huangkai.etao_goods_service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.huangkai.etao_goods_service.mapper")
public class EtaoGoodsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtaoGoodsServiceApplication.class, args);
    }

}
