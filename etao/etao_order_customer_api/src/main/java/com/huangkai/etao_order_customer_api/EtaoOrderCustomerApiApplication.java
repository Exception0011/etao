package com.huangkai.etao_order_customer_api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})

public class EtaoOrderCustomerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtaoOrderCustomerApiApplication.class, args);
    }

}
