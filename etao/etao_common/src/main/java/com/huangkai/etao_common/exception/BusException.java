package com.huangkai.etao_common.exception;

import com.huangkai.etao_common.result.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 自定义异常
 *
 * @author Huangkai on 2023/5/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusException extends RuntimeException implements Serializable {
    private Integer code;
    private String message;

    public BusException(CodeEnum codeEnum){
        this.code = codeEnum.getCode();
        this.message = codeEnum.getMessage();
    }
}

