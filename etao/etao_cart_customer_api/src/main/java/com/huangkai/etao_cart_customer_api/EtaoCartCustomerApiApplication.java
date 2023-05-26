package com.huangkai.etao_cart_customer_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EtaoCartCustomerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtaoCartCustomerApiApplication.class, args);
    }

}
