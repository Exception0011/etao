package com.huangkai.etao_search_customer_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EtaoSearchCustomerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtaoSearchCustomerApiApplication.class, args);
    }

}
