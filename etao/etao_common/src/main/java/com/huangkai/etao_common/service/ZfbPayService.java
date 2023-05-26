package com.huangkai.etao_common.service;

import com.huangkai.etao_common.domain.Orders;
import com.huangkai.etao_common.domain.Payment;

import java.util.Map;

/**
 * @author Huangkai on 2023/5/24
 */
public interface ZfbPayService {
    /**
     * 生成二维码
     * @param  订单对象
     * @return 二维码字符串
     */
    String pcPay(Orders orders);

    /**
     * 验签
     * @param paramMap 支付相关参数
     */
    void checkSign(Map<String,Object> paramMap);

    /**
     * 生成交易记录
     * @param payment 交易记录对象
     */
    void addPayment(Payment payment);
}

