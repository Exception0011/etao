package com.huangkai.etao_user_service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huangkai.etao_common.domain.ShoppingUser;
import com.huangkai.etao_common.exception.BusException;
import com.huangkai.etao_common.result.CodeEnum;
import com.huangkai.etao_common.service.ShoppingUserService;
import com.huangkai.etao_common.util.JWTUtil;
import com.huangkai.etao_common.util.Md5Util;
import com.huangkai.etao_user_service.mapper.ShoppingUserMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Huangkai on 2023/5/23
 */
@DubboService
public class ShoppingUserServiceImpl implements ShoppingUserService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ShoppingUserMapper shoppinguserMapper;
    @Override
    public void saveRegisterCheckCode(String phone, String checkCode) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("registerCode:"+phone,checkCode,300, TimeUnit.SECONDS);
    }

    @Override
    public void registerCheckCode(String phone, String checkCode) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object checkCodeRedis = valueOperations.get("registerCode:" +phone);
        if (!checkCode.equals(checkCodeRedis)){
            throw new BusException(CodeEnum.REGISTER_CODE_ERROR);

        }
    }

    @Override
    public void register(ShoppingUser shoppingUser) {
        //验证手机号是否存在
        String phone = shoppingUser.getPhone();
        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",phone);
        List<ShoppingUser> shoppingUsers = shoppinguserMapper.selectList(queryWrapper);
        if (shoppingUsers!=null&&shoppingUsers.size()>0){
            throw new BusException(CodeEnum.REGISTER_REPEAT_PHONE_ERROR);
        }
        //验证用户名是否存在
        String username = shoppingUser.getUsername();
        QueryWrapper<ShoppingUser> queryWrapper1 = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        List<ShoppingUser> shoppingUsers1 = shoppinguserMapper.selectList(queryWrapper1);
        if (shoppingUsers1!=null&&shoppingUsers1.size()>0){
            throw new BusException(CodeEnum.REGISTER_REPEAT_NAME_ERROR);
        }
        //新增用户
        shoppingUser.setStatus("Y");
        shoppingUser.setPassword(Md5Util.encode(shoppingUser.getPassword()));
        shoppinguserMapper.insert(shoppingUser);
    }

    @Override
    public String loginPassWord(String username, String password) {
        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        ShoppingUser shoppingUser = shoppinguserMapper.selectOne(queryWrapper);
        if (shoppingUser == null) {
            throw new BusException(CodeEnum.LOGIN_NAME_PASSWORD_ERROR);
        }
        // 验证密码
        boolean verify = Md5Util.verify(password, shoppingUser.getPassword());
        if (!verify) {
            throw new BusException(CodeEnum.LOGIN_NAME_PASSWORD_ERROR);
        }
        // 3.生成JWT令牌，返回令牌
        String sign = JWTUtil.sign(shoppingUser);
        return sign;
    }

    @Override
    public void saveLoginCheckCode(String phone, String checkCode) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // redis键为手机号，值为验证码，过期时间5分钟
        valueOperations.set("loginCode:" + phone, checkCode, 300, TimeUnit.SECONDS);
    }

    @Override
    public String loginCheckCode(String phone, String checkCode) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object checkCodeRedis = valueOperations.get("loginCode:" + phone);
        if (!checkCode.equals(checkCodeRedis)) {
            throw new BusException(CodeEnum.LOGIN_CODE_ERROR);
        }
        // 登录成功，查询用户
        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
        queryWrapper.eq("phone", phone);
        ShoppingUser shoppingUser = shoppinguserMapper.selectOne(queryWrapper);
        // 生成JWT令牌，返回令牌
        String sign = JWTUtil.sign(shoppingUser);
        return sign;

    }

    @Override
    public String getName(String token) {
        String name = JWTUtil.verify(token);
        return name;
    }

    @Override
    public ShoppingUser getLoginUser(String token) {
        String username = JWTUtil.verify(token);
        QueryWrapper<ShoppingUser> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        ShoppingUser shoppingUser = shoppinguserMapper.selectOne(queryWrapper);
        return shoppingUser;
    }
}

