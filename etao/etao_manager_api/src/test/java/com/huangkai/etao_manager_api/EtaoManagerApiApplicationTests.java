package com.huangkai.etao_manager_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class EtaoManagerApiApplicationTests {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Test
    void contextLoads() {
        String pa = "hjk";
        String encode = passwordEncoder.encode(pa);
        System.out.println(encode);
    }

}
