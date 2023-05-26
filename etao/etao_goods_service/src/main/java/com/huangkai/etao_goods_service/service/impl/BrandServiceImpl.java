package com.huangkai.etao_goods_service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.Brand;
import com.huangkai.etao_common.service.BrandService;
import com.huangkai.etao_goods_service.mapper.BrandMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Huangkai on 2023/5/18
 */
@DubboService
@Transactional
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;
    @Override
    public Brand findById(Long id) {
        Brand brand = brandMapper.selectById(id);
        return brand;
    }

    @Override
    public List<Brand> findAll() {
        return brandMapper.selectList(null);
    }

    @Override
    public void add(Brand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public void update(Brand brand) {
        brandMapper.updateById(brand);
    }

    @Override
    public void delete(Long id) {
        brandMapper.deleteById(id);
    }

    @Override
    public Page<Brand> search(Brand brand, int page, int size) {
        QueryWrapper<Brand> queryWrapper = new QueryWrapper<>();
        // 判断品牌名不为空
        if (brand != null){
            queryWrapper.like(StringUtils.hasText(brand.getName()),"name",brand.getName());
        }
        Page<Brand> page1 = brandMapper.selectPage(new Page(page, size), queryWrapper);

        return brandMapper.selectPage(page1,queryWrapper);
    }
}

