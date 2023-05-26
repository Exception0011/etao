package com.huangkai.etao_user_customer_api.controller;

import com.huangkai.etao_common.domain.ShoppingUser;
import com.huangkai.etao_common.result.BaseResult;
import com.huangkai.etao_common.service.ShoppingUserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.*;

/**
 * @author Huangkai on 2023/5/23
 */
@RestController
@RequestMapping("/user/shoppingUser")
public class ShoppingUserController {
    @DubboReference
    private ShoppingUserService shoppingUserService;
    @GetMapping("sendMeessage")
    public BaseResult senMessage(String phone){

        return BaseResult.ok();

    }

    @GetMapping("/registerCheckCode")
    public BaseResult register(String phone,String checkCode){
        shoppingUserService.registerCheckCode(phone,checkCode);
        return BaseResult.ok();
    }

    @PostMapping("/register")
    public BaseResult register(@RequestBody ShoppingUser shoppingUser){
        shoppingUserService.register(shoppingUser);
        return BaseResult.ok();
    }
    @PostMapping("/loginPassword")
    public BaseResult loginPassword(@RequestBody ShoppingUser shoppingUser){
        return BaseResult.ok(shoppingUserService.loginPassWord(shoppingUser.getUsername(), shoppingUser.getPassword()));
    }
    @GetMapping("/sendLoginCheckCode")
    public BaseResult sendLoginCheckCode(String phone) {
        return BaseResult.ok();
    }


    @PostMapping("/loginCheckCode")
    public BaseResult loginCheckCode(String phone, String checkCode){
        return BaseResult.ok(shoppingUserService.loginCheckCode(phone, checkCode));
    }

    @GetMapping("/getName")
    public BaseResult<String>
    getName(@RequestHeader("token") String token){
        String name = shoppingUserService.getName(token);
        return BaseResult.ok(name);
    }


}

