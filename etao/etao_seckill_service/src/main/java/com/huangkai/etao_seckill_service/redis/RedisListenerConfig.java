package com.huangkai.etao_seckill_service.redis;

/**
 * @author Huangkai on 2023/5/26
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * 配置redis监听器
 */
@Configuration
public class RedisListenerConfig {
    // 配置redis监听器，监听redis过期事件
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}


