package com.huangkai.etao_order_customer_api.controller;

import com.huangkai.etao_common.domain.Address;
import com.huangkai.etao_common.domain.Area;
import com.huangkai.etao_common.domain.City;
import com.huangkai.etao_common.domain.Province;
import com.huangkai.etao_common.result.BaseResult;
import com.huangkai.etao_common.service.AddressService;
import com.huangkai.etao_common.util.JWTUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Huangkai on 2023/5/24
 */
@RestController
@RequestMapping("/user/address")
public class AddressController {
    @DubboReference
    private AddressService addressService;

    @GetMapping("findAllProvince")
    public BaseResult<List<Province>> findAllProvince(){
        return BaseResult.ok(addressService.findAllProvince());
    }
    @GetMapping("findCityByProvince")
    public BaseResult<List<City>> findCityByProvince(Long provinceId){
        return BaseResult.ok(addressService.findCityByProvince(provinceId));
    }

    @GetMapping("findAreaByCity")
    public BaseResult<List<Area>> findAreaByCity(Long cityId){
        return BaseResult.ok(addressService.findAreaByCity(cityId));
    }

    @PostMapping("add")
    public BaseResult add(@RequestHeader String token,@RequestBody Address address){
        Long userId = JWTUtil.getId(token);
        address.setUserId(userId);
        addressService.add(address);
        return BaseResult.ok();
    }

    @PutMapping("/update")
    public BaseResult update(@RequestHeader String token,@RequestBody Address address) {
        Long userId = JWTUtil.getId(token);
        // 获取用户id
        address.setUserId(userId);
        addressService.update(address);
        return BaseResult.ok();
    }
    /**
     * 根据id获取地址
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public BaseResult<Address> findById(Long id) {
        Address address = addressService.findById(id);
        return BaseResult.ok(address);
    }
    /**
     * 删除地址
     * @param id 地址id
     * @return
     */
    @DeleteMapping("/delete")
    public BaseResult delete(Long id) {
        addressService.delete(id);
        return BaseResult.ok();
    }

    /**
     * 根据登录用户查询地址
     * @return
     */
    @GetMapping("/findByUser")
    public BaseResult<List<Address>>
    findByUser(@RequestHeader String token) {
        Long userId = JWTUtil.getId(token);
        // 获取用户id
        List<Address> addresses = addressService.findByUserId(userId);
        return BaseResult.ok(addresses);
    }
}

