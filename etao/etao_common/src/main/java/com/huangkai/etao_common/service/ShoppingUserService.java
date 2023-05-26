package com.huangkai.etao_common.service;

import com.huangkai.etao_common.domain.ShoppingUser;


/**
 * @author Huangkai on 2023/5/23
 */
public interface ShoppingUserService {
    void saveRegisterCheckCode(String phone,String checkCode);
    void registerCheckCode(String phone,String checkCode);
    void register(ShoppingUser shoppingUser);
    String loginPassWord(String username,String password);
    void saveLoginCheckCode(String phone,String checkCode);
    String loginCheckCode(String phone,String checkCode);
    String getName(String token);
    ShoppingUser getLoginUser(String token);
}
