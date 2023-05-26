package com.huangkai.etao_file_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class EtaoFileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtaoFileServiceApplication.class, args);
    }

}
