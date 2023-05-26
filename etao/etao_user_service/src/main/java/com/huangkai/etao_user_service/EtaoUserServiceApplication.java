package com.huangkai.etao_user_service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.huangkai.etao_user_service.mapper")
public class EtaoUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtaoUserServiceApplication.class, args);
    }

}
