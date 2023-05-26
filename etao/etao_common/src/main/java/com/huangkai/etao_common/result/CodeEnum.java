package com.huangkai.etao_common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Admin
 */
@Getter
@AllArgsConstructor
public enum CodeEnum {
    SUCCESS(200, "OK"),
    // 系统异常
    SYSTEM_ERROR(500,"系统异常"),
    // 业务异常
    PARAMETER_ERROR(601,"参数异常"),
    INSERT_PRODUCT_TYPE_ERROR(602,"已经是最后一层，不能再添加"),
    DELETE_PRODUCT_TYPE_ERROR(603,"不是最后一层，不能删除"),
    UPLOAD_FILE_ERROR(604,"文件不存在"),
    REGISTER_CODE_ERROR(605,"注册验证码错误"),
    REGISTER_REPEAT_PHONE_ERROR(606,"手机号已存在"),
    REGISTER_REPEAT_NAME_ERROR(607,"用户名已存在"),
    LOGIN_NAME_PASSWORD_ERROR(608,"用户名或密码异常"),
    LOGIN_CODE_ERROR(609,"验证码错误"),
    VERIFY_TOKEN_ERROR(611,"令牌解析异常"),
    QR_CODE_ERROR(612,"二维码生成错误"),
    CHECK_SIGN_ERROR(613,"支付宝验签异常"),
    NO_STOCK_ERROR(614,"已抢完"),
    ORDER_EXPIRED_ERROR(615,"订单过期");

    private final Integer code;
    private final String message;
}

