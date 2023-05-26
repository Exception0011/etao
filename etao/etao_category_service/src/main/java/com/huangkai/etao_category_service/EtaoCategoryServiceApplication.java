package com.huangkai.etao_category_service;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.huangkai.etao_category_service.mapper")
public class EtaoCategoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtaoCategoryServiceApplication.class, args);
    }


}
