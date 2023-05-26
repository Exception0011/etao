package com.huangkai.etao_manager_api.security;

import com.alibaba.fastjson.JSON;
import com.huangkai.etao_common.result.BaseResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Huangkai on 2023/5/19
 */
public class MyLoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("text/json;charset=utf-8");
        BaseResult baseResult = new BaseResult(402, "用户名或密码错误", null);
        response.getWriter().write(JSON.toJSONString(baseResult));
    }
}

