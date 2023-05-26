package com.huangkai.etao_common.service;

import com.huangkai.etao_common.domain.Orders;

import java.util.List;

/**
 * @author Huangkai on 2023/5/24
 */
public interface OrdersService {
    Orders add(Orders orders);
    void update(Orders orders);
    Orders findById(String id);
    //查询用户订单
    List<Orders> findUserOrders(Long UserId,Integer status);
}
