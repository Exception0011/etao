package com.huangkai.etao_seckill_service.service.ipml;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.CartGoods;
import com.huangkai.etao_common.domain.Orders;
import com.huangkai.etao_common.domain.SeckillGoods;
import com.huangkai.etao_common.exception.BusException;
import com.huangkai.etao_common.result.CodeEnum;
import com.huangkai.etao_common.service.SeckillService;
import com.huangkai.etao_seckill_service.mapper.SeckillGoodsMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Huangkai on 2023/5/25
 */
@DubboService
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Page<SeckillGoods> findPageByRedis(int page, int size) {
        //查询所有秒杀商品列表
        List<SeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();
        //获取当前页商品列表
        int start = (page-1)*size;
        int end = start + page > seckillGoods.size() ? seckillGoods.size() : start+page;
        //获取当前页结果集
        List<SeckillGoods> seckillGoods1 = seckillGoods.subList(start, end);
        //构造页面对象
        Page<SeckillGoods> page1 = new Page<>();
        page1.setCurrent(page).setSize(size).setTotal(seckillGoods.size()).setRecords(seckillGoods1);
        return page1;
    }

    @Override
    public SeckillGoods findSeckillGoodsByRedis(Long goodsId) {
        return (SeckillGoods)redisTemplate.boundHashOps("seckillGoods").get(goodsId);
    }

    @Override
    public Orders createOrder(Orders orders) {
        //生成订单对象
        orders.setId(IdWorker.getIdStr());
        orders.setStatus(1);
        orders.setCreateTime(new Date());
        orders.setExpire(new Date(new Date().getTime()+1000*60*5));
        //计算商品价格
        CartGoods cartGoods = orders.getCartGoods().get(0);
        Integer num = cartGoods.getNum();
        BigDecimal price = cartGoods.getPrice();
        BigDecimal sum = price.multiply(BigDecimal.valueOf(num));
        orders.setPayment(sum);
        //减少秒杀商品库存
        SeckillGoods seckillGoods = findSeckillGoodsByRedis(cartGoods.getGoodId());//查询秒杀商品
        // 查询库存，库存不足抛出异常
        Integer stockCount = seckillGoods.getStockCount();
        if (stockCount < cartGoods.getNum()){
            throw new BusException(CodeEnum.NO_STOCK_ERROR);
        }
        // 减少库存
        seckillGoods.setStockCount(seckillGoods.getStockCount() - cartGoods.getNum());
        redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getGoodsId(),seckillGoods);
        // 3.保存订单数据
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置一分钟过期
        redisTemplate.opsForValue().set(orders.getId(),orders,1, TimeUnit.MINUTES);
        /**
         * 给订单创建副本，副本的过期时间长于原订单
         * redis过期后触发过期事件时，redis数据已经过期，此时只能拿到key，拿不到value。
         * 而过期事件需要回退商品库存，必须拿到value即订单详情，才能拿到商品数据，进行回退操作
         * 我们保存一个订单副本，过期时间长于原订单，此时就可以通过副本拿到原订单数据
         */
        redisTemplate.opsForValue().set(orders.getId()+"_copy",orders,2,TimeUnit.MINUTES);
        return orders;
    }

    @Override
    public Orders findOrder(String id) {
        return (Orders) redisTemplate.opsForValue().get(id);
    }

    @Override
    public Orders pay(String orderId) {
        // 1.查询订单，设置相应数据
        Orders orders = (Orders) redisTemplate.opsForValue().get(orderId);
        if (orders == null){
            throw new BusException(CodeEnum.ORDER_EXPIRED_ERROR);
        }
        orders.setStatus(2);
        orders.setPaymentTime(new Date());
        orders.setPaymentType(2); // 支付宝支付


        // 2.从redis删除订单
        redisTemplate.delete(orderId);
        redisTemplate.delete(orderId+"_copy");


        // 3.返回订单数据
        return orders;
    }


    @Scheduled(cron = "0/5 * * * * *")
    public void refreshRedis(){

        // 将redis中秒杀商品的库存数据同步到mysql
        List<SeckillGoods> seckillGoodsListOld = redisTemplate.boundHashOps("seckillGoods").values();
        for (SeckillGoods seckillGoods : seckillGoodsListOld) {
            // 在数据库中查询秒杀商品
            SeckillGoods sqlSeckillGoods = seckillGoodsMapper.selectById(seckillGoods.getId());
            // 修改秒杀商品的库存
            sqlSeckillGoods.setStockCount(seckillGoods.getStockCount());
            seckillGoodsMapper.updateById(sqlSeckillGoods);
        }

        System.out.println("同步秒杀数据到redis");
        //查询正在秒杀的商品
        QueryWrapper<SeckillGoods> queryWrapper = new QueryWrapper<>();
        Date date = new Date();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        queryWrapper.le("startTime",now).ge("endTime",now).gt("stockCount",0);
        List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectList(queryWrapper);
        //删除之前的
        redisTemplate.delete("seckillGoods");
        //保存现在的
        for (SeckillGoods seckillGood : seckillGoods) {
            redisTemplate.boundHashOps("seckillGoods").put(seckillGood.getGoodsId(),seckillGood);
        }

    }
}

