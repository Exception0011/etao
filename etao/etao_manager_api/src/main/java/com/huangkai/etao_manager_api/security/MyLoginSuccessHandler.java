package com.huangkai.etao_manager_api.security;

import com.alibaba.fastjson.JSON;
import com.huangkai.etao_common.result.BaseResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Huangkai on 2023/5/19
 */
public class MyLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("text/json;charset=utf-8");
        BaseResult baseResult = new BaseResult(200, "登录成功", null);
        //将字符串转成json
        response.getWriter().write(JSON.toJSONString(baseResult));
    }
}

