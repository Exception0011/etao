package com.huangkai.etao_cart_service.service.impl;

import com.huangkai.etao_common.domain.CartGoods;
import com.huangkai.etao_common.service.CartService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Huangkai on 2023/5/23
 */
@DubboService
public class CartServiceImpl implements CartService {
    @Autowired
    private RedisTemplate redisTemplate;

    @RabbitListener(queues = "sync_cart_queue")
    public void listenSyncQueue(CartGoods cartGoods){
        refreshCartGoods(cartGoods);
    }

    // 监听删除购物车商品队列
    @RabbitListener(queues = "del_cart_queue")
    public void listenDelQueue(CartGoods cartGoods){
        deleteCartGoods(cartGoods);
    }

    @Override
    public void addCart(Long userId, CartGoods cartGoods) {
        List<CartGoods> cartList = findCartList(userId);
        //查询购物车是否有该商品，如果有添加数量
        for (CartGoods goods : cartList) {
            if (cartGoods.getGoodId().equals(goods.getGoodId())){
                int newNum = goods.getNum() +cartGoods.getNum();
                cartGoods.setNum(newNum);
                redisTemplate.boundHashOps("cartList").put(userId,cartList);
                return;
            }
        }
        cartList.add(cartGoods);
        redisTemplate.boundHashOps("cartList").put(userId,cartList);
    }

    /**
     * 修改商品购物车数量
     * @param userId:
     * @param goodId:
     * @return void
     * @author huangkai
     * @description TODO
     * @date 2023/5/23 22:57
     */
    @Override
    public void handleCart(Long userId, Long goodId,Integer num) {
        List<CartGoods> cartList = findCartList(userId);
        for (CartGoods cartGoods : cartList) {
            if (cartGoods.getGoodId().equals(goodId)){
                cartGoods.setNum(num);
                break;
            }
        }
        redisTemplate.boundHashOps("cartList").put(userId,cartList);
    }

    @Override
    public void deleteCartOption(Long userId, Long goodId) {
        // 获取用户购物车列表
        List<CartGoods> cartList =
                findCartList(userId);
        // 将商品移出列表
        for (CartGoods cartGoods : cartList) {
            if
            (goodId.equals(cartGoods.getGoodId())) {
                cartList.remove(cartGoods);
                break;
            }
        }
        // 将新的购物车列表保存到redis中
        redisTemplate.boundHashOps("cartList").put(userId, cartList);
    }

    @Override
    public List<CartGoods> findCartList(Long userId) {
        Object cartList = redisTemplate.boundHashOps("cartList").get(userId);
        if (cartList == null){
            return new ArrayList<CartGoods>();
        }else {
            return (List<CartGoods>) cartList;
        }
    }

    @Override
    public void refreshCartGoods(CartGoods cartGoods) {
        // 获取所有用户购物车商品
        BoundHashOperations cartList = redisTemplate.boundHashOps("cartList");
        Map<Long,List<CartGoods>> allCartGoods = cartList.entries();
        Set<Map.Entry<Long, List<CartGoods>>> entries = allCartGoods.entrySet();
        // 遍历所有用户的购物车
        for (Map.Entry<Long, List<CartGoods>> entry : entries) {
            List<CartGoods> goodsList = entry.getValue();
            // 遍历一个用户购物车的所有商品
            for (CartGoods goods : goodsList) {
                // 如果该商品是被更新的商品，修改商品数据
                if (cartGoods.getGoodId().equals(goods.getGoodId())){
                    goods.setGoodsName(cartGoods.getGoodsName());
                    goods.setHeaderPic(cartGoods.getHeaderPic());
                    goods.setPrice(cartGoods.getPrice());
                }
            }
        }


        // 将改变后所有用户购物车重新放入redis
        redisTemplate.delete("cartList");
        redisTemplate.boundHashOps("cartList").putAll(allCartGoods);
    }

    @Override
    public void deleteCartGoods(CartGoods cartGoods) {
        BoundHashOperations cartList = redisTemplate.boundHashOps("cartList");
        // 所有用户的购物车
        Map<String,List<CartGoods>> allCartGoods = cartList.entries();
        Set<Map.Entry<String, List<CartGoods>>> entries = allCartGoods.entrySet();
        // 遍历所有用户的购物车
        for (Map.Entry<String, List<CartGoods>> entry : entries) {
            List<CartGoods> goodsList = entry.getValue();
            // 遍历一个用户购物车的所有商品
            for (CartGoods goods : goodsList) {
                // 如果该商品是被删除的商品
                if (cartGoods.getGoodId().equals(goods.getGoodId())){
                    goodsList.remove(goods);
                    break;
                }
            }
        }
        // 将改变后的map重新放入redis
        redisTemplate.delete("cartList");
        redisTemplate.boundHashOps("cartList").putAll(allCartGoods);
    }
}

