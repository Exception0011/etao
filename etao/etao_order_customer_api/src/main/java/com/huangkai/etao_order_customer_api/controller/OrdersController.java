package com.huangkai.etao_order_customer_api.controller;

import com.huangkai.etao_common.domain.CartGoods;
import com.huangkai.etao_common.domain.Orders;
import com.huangkai.etao_common.result.BaseResult;
import com.huangkai.etao_common.service.CartService;
import com.huangkai.etao_common.service.OrdersService;
import com.huangkai.etao_common.util.JWTUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Huangkai on 2023/5/24
 */
@RestController
@RequestMapping("/user/orders")
public class OrdersController {

    @DubboReference
    private OrdersService ordersService;
    @DubboReference
    private CartService cartService;

    public BaseResult<Orders> add(@RequestHeader String token, @RequestBody Orders orders){
        Long userId = JWTUtil.getId(token);
        orders.setUserId(userId);
        Orders orders1 = ordersService.add(orders);
        //将redis的购物车中的商品删除
        List<CartGoods> cartGoods = orders.getCartGoods();
        for (CartGoods cartGood : cartGoods) {
            cartService.deleteCartOption(userId,cartGood.getGoodId());
        }
        return BaseResult.ok(orders1);
    }
    @GetMapping("/findById")
    public BaseResult<Orders> findById(String id){
        Orders orders = ordersService.findById(id);
        return BaseResult.ok(orders);
    }

    @GetMapping("/findUserOrders")
    public BaseResult<List<Orders>> findUserOrders(Integer status,@RequestHeader String token){
        Long userId = JWTUtil.getId(token); //获取用户id
        List<Orders> orders = ordersService.findUserOrders(userId,status);
        return BaseResult.ok(orders);
    }

}

