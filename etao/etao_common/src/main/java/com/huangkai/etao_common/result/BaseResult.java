package com.huangkai.etao_common.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Huangkai on 2023/5/18
 */
@Data
@AllArgsConstructor
public class BaseResult<T> implements Serializable {
    private Integer code;
    private String message;
    private T data;

    public static <T> BaseResult<T> ok(){
        return new BaseResult<>(CodeEnum.SUCCESS.getCode(),CodeEnum.SUCCESS.getMessage(), null);
    }

    public static <T> BaseResult<T> ok(T data){
        return new BaseResult<>(CodeEnum.SUCCESS.getCode(),CodeEnum.SUCCESS.getMessage(), data);
    }
}

