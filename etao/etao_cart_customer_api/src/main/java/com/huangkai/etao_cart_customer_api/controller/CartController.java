package com.huangkai.etao_cart_customer_api.controller;

import com.huangkai.etao_common.domain.CartGoods;
import com.huangkai.etao_common.result.BaseResult;
import com.huangkai.etao_common.service.CartService;
import com.huangkai.etao_common.util.JWTUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Huangkai on 2023/5/23
 */
@RestController
@RequestMapping("/user/cart")
public class CartController {
    @DubboReference
    private CartService cartService;

    @GetMapping("/findCartList")
    public BaseResult<List<CartGoods>> findCartList(@RequestHeader String token){
        Long userId = JWTUtil.getId(token);
        List<CartGoods> cartList = cartService.findCartList(userId);
        return BaseResult.ok(cartList);
    }

    @PostMapping("/addCart")
    public BaseResult addCart(@RequestBody CartGoods cartGoods,@RequestHeader String token){
        Long userId = JWTUtil.getId(token); // 获取用户id
        cartService.addCart(userId,cartGoods);
        return BaseResult.ok();
    }

    @GetMapping("/handleCart")
    public BaseResult addCart(@RequestHeader String token,Long goodId,Integer num){
        Long userId = JWTUtil.getId(token); // 获取用户id
        cartService.handleCart(userId,goodId,num);
        return BaseResult.ok();
    }

    @DeleteMapping("/deleteCart")
    public BaseResult addCart(@RequestHeader String token,Long goodId){
        Long userId = JWTUtil.getId(token); // 获取用户id
        cartService.deleteCartOption(userId,goodId);
        return BaseResult.ok();
    }

}

