package com.huangkai.etao_seckill_customer_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EtaoSeckillCustomerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtaoSeckillCustomerApiApplication.class, args);
    }

}
