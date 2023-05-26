package com.huangkai.etao_manager_api.security;

import com.alibaba.fastjson.JSON;
import com.huangkai.etao_common.result.BaseResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Huangkai on 2023/5/19
 */
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("text/json;charset=utf-8");
        BaseResult baseResult = new BaseResult(403,"权限不足",null);
        response.getWriter().write(JSON.toJSONString(baseResult));
    }
}

