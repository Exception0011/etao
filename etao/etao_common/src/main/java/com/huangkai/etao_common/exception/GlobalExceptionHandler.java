package com.huangkai.etao_common.exception;

import com.huangkai.etao_common.result.BaseResult;
import com.huangkai.etao_common.result.CodeEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Huangkai on 2023/5/18
 */
//这是对控制器的全局配置
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 业务异常
     * @param req:
     * @param res:
     * @param e:
     * @return BaseResult
     * @author huangkai
     * @description TODO
     * @date 2023/5/18 15:10
     */
    @ExceptionHandler(BusException.class)
    public BaseResult defaultExceptionHandler(HttpServletRequest req, HttpServletResponse res,BusException e){
        BaseResult baseResult = new BaseResult<>(e.getCode(), e.getMessage(), null);
        return baseResult;
    }

    @ExceptionHandler(Exception.class)
    public BaseResult defaultExceptionHandler(HttpServletRequest req, HttpServletResponse res,Exception e){
        e.printStackTrace();
        BaseResult baseResult = new BaseResult<>(CodeEnum.SYSTEM_ERROR.getCode(), CodeEnum.SYSTEM_ERROR.getMessage(), null);
        return baseResult;
    }


}

