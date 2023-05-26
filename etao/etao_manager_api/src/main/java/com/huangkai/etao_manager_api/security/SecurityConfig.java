package com.huangkai.etao_manager_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Huangkai on 2023/5/19
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //自定义表单登录
        http.formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/admin/login")
                .successHandler(new MyLoginSuccessHandler())
                .failureHandler(new MyLoginFailureHandler());
        //权限拦截
        http.authorizeHttpRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/admin/login").permitAll()
                .anyRequest().authenticated();
        //退出登录
        http.logout()
                .logoutUrl("/admin/logout")
                .logoutSuccessHandler(new MyLogoutSuccessHandler())
                .clearAuthentication(true)
                .invalidateHttpSession(true);
        //异常处理
        http.exceptionHandling()
                .authenticationEntryPoint(new MyAuthenticationEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler());

        //csrf防护
        http.csrf().disable();
        //开启跨域
        http.cors();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

