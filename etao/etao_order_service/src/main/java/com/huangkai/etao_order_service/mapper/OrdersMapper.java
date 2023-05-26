package com.huangkai.etao_order_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huangkai.etao_common.domain.Orders;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Huangkai on 2023/5/24
 */
public interface OrdersMapper extends BaseMapper<Orders> {
    //查询订单详情
    Orders findById(String id);
    // 查询用户订单
    List<Orders> findOrdersByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

}
