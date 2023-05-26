package com.huangkai.etao_common.service;

import com.huangkai.etao_common.domain.CartGoods;

import java.util.List;

/**
 * @author Huangkai on 2023/5/23
 */
public interface CartService {
    void addCart(Long userId, CartGoods cartGoods);
    //修改购物车商品数量
    void handleCart(Long userId,Long goodId,Integer num);
    //删除购物车商品
    void deleteCartOption(Long userId,Long goodId);
    //获取用户购物车
    List<CartGoods> findCartList(Long userId);
    //更新redis中的商品数据，在管理员执行更新商品之后执行
    void refreshCartGoods(CartGoods cartGoods);
    //删除redis中的商品数据，在管理员下架之后执行
    void deleteCartGoods(CartGoods cartGoods);

}
