package com.huangkai.etao_common.service;

import com.huangkai.etao_common.domain.Address;
import com.huangkai.etao_common.domain.Area;
import com.huangkai.etao_common.domain.City;
import com.huangkai.etao_common.domain.Province;

import java.util.List;

/**
 * @author Huangkai on 2023/5/24
 */
public interface AddressService {
    List<Province> findAllProvince();
    List<City> findCityByProvince(Long provinceId);
    List<Area> findAreaByCity(Long cityId);
    void add(Address address);
    void update(Address address);
    Address findById(Long id);
    void delete(Long id);
    //根据登录用户查询地址
    List<Address> findByUserId(Long userId);
}
