package com.huangkai.etao_user_customer_api;


import com.huangkai.etao_common.interceptor.JWTInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Huangkai on 2022/12/4
 */
// 拦截器配置
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptor())
                .addPathPatterns("/**") //拦截的接口
                .excludePathPatterns(
                        "/user/shoppingUser/sendMessage",
                        "/user/shoppingUser/registerCheckCode",
                        "/user/shoppingUser/register",
                        "/user/shoppingUser/loginPassword",
                        "/user/shoppingUser/sendLoginCheckCode",
                        "/user/shoppingUser/loginCheckCode"
                ); //放行的接口
    }
}

