package com.huangkai.etao_cart_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class EtaoCartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtaoCartServiceApplication.class, args);
    }

}
