package com.huangkai.etao_order_service.service.impl;

import com.huangkai.etao_common.domain.CartGoods;
import com.huangkai.etao_common.domain.Orders;
import com.huangkai.etao_common.service.OrdersService;
import com.huangkai.etao_order_service.mapper.CartGoodsMapper;
import com.huangkai.etao_order_service.mapper.OrdersMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Huangkai on 2023/5/24
 */
@DubboService
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private CartGoodsMapper cartGoodsMapper;
    @Override
    public Orders add(Orders orders) {
        //设置订单状态未付款
        orders.setStatus(1);
        //设置订单创建时间
        orders.setCreateTime(new Date());
        //计算订单价格，遍历订单所有商品
        List<CartGoods> cartGoods = orders.getCartGoods();
        BigDecimal sum = BigDecimal.ZERO;
        for (CartGoods cartGood : cartGoods) {
            //数量
            BigDecimal num = new BigDecimal(cartGood.getNum());
            //单价
            BigDecimal price = cartGood.getPrice();
            BigDecimal multiply = num.multiply(price);
            sum = sum.add(multiply);
        }
        orders.setPayment(sum);
        ordersMapper.insert(orders);
        // 将订单商品（购物车商品）保存到数据库中
        for (CartGoods cartGood : cartGoods) {
            cartGood.setOrderId(orders.getId());
            cartGoodsMapper.insert(cartGood);
        }
        return orders;

    }

    //只是用来修改状态（支付完）的
    @Override
    public void update(Orders orders) {
        ordersMapper.updateById(orders);
    }

    @Override
    public Orders findById(String id) {
        return ordersMapper.findById(id);
    }

    @Override
    public List<Orders> findUserOrders(Long userId, Integer status) {
        return ordersMapper.findOrdersByUserIdAndStatus(userId,status);
    }
}

