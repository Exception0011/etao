package com.huangkai.etao_order_customer_api.controller;

import com.alibaba.fastjson.JSON;
import com.huangkai.etao_common.domain.Orders;
import com.huangkai.etao_common.domain.Payment;
import com.huangkai.etao_common.result.BaseResult;
import com.huangkai.etao_common.service.OrdersService;
import com.huangkai.etao_common.service.ZfbPayService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Huangkai on 2023/5/24
 */
@RestController
@RequestMapping("/user/payment")
public class PaymentController {
    @DubboReference
    private ZfbPayService zfbPayService;

    @DubboReference
    private OrdersService ordersService;


    @PostMapping("/pcPay")
    public BaseResult<String> pcPay(String orderId) {
        Orders orders = ordersService.findById(orderId);
        String codeUrl = zfbPayService.pcPay(orders);
        return BaseResult.ok(codeUrl);
    }
    /**
     * 该方法是用户扫码支付后支付宝调用的。
     * @return
     */
    @PostMapping("/success/notify")
    public BaseResult successNotify(HttpServletRequest request) {
        // 1.验签
        Map<String, Object> paramMap = new HashMap();
        paramMap.put("requestParameterMap", request.getParameterMap());
        zfbPayService.checkSign(paramMap);
        String trade_status = request.getParameter("trade_status");// 订单状态
        String out_trade_no = request.getParameter("out_trade_no");// 订单编号
        // 如果支付成功
        if (trade_status.equals("TRADE_SUCCESS")) {
            // 2.修改订单状态
            Orders orders = ordersService.findById(out_trade_no);
            orders.setStatus(2); // 订单状态为已付款
            orders.setPaymentTime(new Date());
            orders.setPaymentType(2); // 支付宝支付
            ordersService.update(orders);
            // 3.添加交易记录
            Payment payment = new Payment();
            payment.setOrderId(out_trade_no);// 订单编号
            payment.setTransactionId(out_trade_no);// 交易号
            payment.setTradeType("支付宝支付");// 交易类型
            payment.setTradeState(trade_status); //交易状态
            payment.setPayerTotal(orders.getPayment()); // 付款数
            payment.setContent(JSON.toJSONString(request.getParameterMap())); // 支付详情
            payment.setCreateTime(new Date());// 支付时间
            zfbPayService.addPayment(payment);
        }
        return BaseResult.ok();
    }
}

