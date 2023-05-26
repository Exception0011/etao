package com.huangkai.etao_order_service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huangkai.etao_common.domain.Address;
import com.huangkai.etao_common.domain.Area;
import com.huangkai.etao_common.domain.City;
import com.huangkai.etao_common.domain.Province;
import com.huangkai.etao_common.service.AddressService;
import com.huangkai.etao_order_service.mapper.AddressMapper;
import com.huangkai.etao_order_service.mapper.AreaMapper;
import com.huangkai.etao_order_service.mapper.CityMapper;
import com.huangkai.etao_order_service.mapper.ProvinceMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * @author Huangkai on 2023/5/24
 */
@DubboService
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private ProvinceMapper provinceMapper;
    @Autowired
    private CityMapper cityMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Override
    public List<Province> findAllProvince() {
        return provinceMapper.selectList(null);
    }

    @Override
    public List<City> findCityByProvince(Long provinceId) {
        QueryWrapper<City> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("provinceId",provinceId);
        return cityMapper.selectList(queryWrapper);
    }

    @Override
    public List<Area> findAreaByCity(Long cityId) {
        QueryWrapper<Area> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cityId",cityId);
        return areaMapper.selectList(queryWrapper);

    }

    @Override
    public void add(Address address) {
        addressMapper.insert(address);
    }

    @Override
    public void update(Address address) {
        addressMapper.updateById(address);
    }

    @Override
    public Address findById(Long id) {
        return addressMapper.selectById(id);
    }

    @Override
    public void delete(Long id) {
        addressMapper.deleteById(id);
    }

    @Override
    public List<Address> findByUserId(Long userId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper();
        queryWrapper.eq("userId",userId);
        List<Address> addresses = addressMapper.selectList(queryWrapper);
        return addresses;

    }
}

