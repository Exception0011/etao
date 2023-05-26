package com.huangkai.etao_seckill_customer_api.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.Orders;
import com.huangkai.etao_common.domain.SeckillGoods;
import com.huangkai.etao_common.result.BaseResult;
import com.huangkai.etao_common.service.OrdersService;
import com.huangkai.etao_common.service.SeckillService;
import com.huangkai.etao_common.util.JWTUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @author Huangkai on 2023/5/25
 */
@RestController
@RequestMapping("/user/seckillGoods")
public class SeckillGoodsController {
    @DubboReference
    private SeckillService seckillService;

    @DubboReference
    private OrdersService ordersService;
    @GetMapping("/findPage")
    public BaseResult<Page<SeckillGoods>> findPage(int page, int size){
        Page<SeckillGoods> seckillGoodsPage = seckillService.findPageByRedis(page, size);
        return BaseResult.ok(seckillGoodsPage);
    }
    @GetMapping("/findById")
    public BaseResult<SeckillGoods> findById(Long id){
        SeckillGoods seckillGoods = seckillService.findSeckillGoodsByRedis(id);
        return BaseResult.ok(seckillGoods);
    }
    @PostMapping("/add")
    public BaseResult<Orders> add(@RequestBody Orders orders, @RequestHeader String token) {
        Long userId = JWTUtil.getId(token); //获取登录用户
        orders.setUserId(userId);
        Orders order = seckillService.createOrder(orders);
        return BaseResult.ok(order);
    }
    @GetMapping("/findOrder")
    public BaseResult<Orders> findOrder(String id){
        Orders orders = seckillService.findOrder(id);
        return BaseResult.ok(orders);
    }

    /**
     * 支付秒杀订单
     * @param id 订单id
     */
    @GetMapping("/pay")
    public BaseResult pay(String id){
        // 支付秒杀订单
        Orders orders = seckillService.pay(id);
        // 将订单数据存入数据库
        ordersService.add(orders);
        return BaseResult.ok();
    }

}

