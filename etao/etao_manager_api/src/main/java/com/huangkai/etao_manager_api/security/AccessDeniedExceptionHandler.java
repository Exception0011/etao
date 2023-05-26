package com.huangkai.etao_manager_api.security;

import com.huangkai.etao_common.exception.BusException;
import com.huangkai.etao_common.result.BaseResult;
import com.huangkai.etao_common.result.CodeEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;

/**
 * @author Huangkai on 2023/5/18
 */
//这是对控制器的全局配置
@RestControllerAdvice
public class AccessDeniedExceptionHandler {

    //处理权限不足异常，捕获到异常再次抛出，交给MyAccessDeniedHandler
    @ExceptionHandler(AccessDeniedException.class)
    public void defaultExceptionHandler(AccessDeniedException e) throws AccessDeniedException {
        throw e;
    }

   
}

