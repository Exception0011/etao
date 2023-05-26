package com.huangkai.etao_goods_service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.Specification;
import com.huangkai.etao_common.domain.SpecificationOption;
import com.huangkai.etao_common.domain.SpecificationOptions;
import com.huangkai.etao_common.service.SpecificationService;
import com.huangkai.etao_goods_service.mapper.SpecificationMapper;
import com.huangkai.etao_goods_service.mapper.SpecificationOptionMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @author Huangkai on 2023/5/19
 */
@DubboService
public class SpecificationServiceImpl implements SpecificationService {
    @Autowired
    private SpecificationMapper specificationMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;
    @Override
    public void add(Specification specification) {
        specificationMapper.insert(specification);
    }

    @Override
    public void update(Specification specification) {
        specificationMapper.updateById(specification);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            // 删除商品规格项
            QueryWrapper<SpecificationOption> queryWrapper = new QueryWrapper();
            queryWrapper.eq("specId",id);
            specificationOptionMapper.delete(queryWrapper);
            specificationMapper.deleteById(id);
        }
    }

    @Override
    public Specification findById(Long id) {
        return specificationMapper.findById(id);
    }

    @Override
    public Page<Specification> search(int page, int size) {
        return specificationMapper.selectPage(new Page<>(page,size),null);
    }

    @Override
    public List<Specification> findByProductTypeId(Long id) {
        return specificationMapper.findByProductTypeId(id);
    }

    @Override
    public void addOption(SpecificationOptions specificationOptions) {
        String[] optionNames = specificationOptions.getOptionName();
        Long specId = specificationOptions.getSpecId();
        for (String optionName : optionNames) {
            SpecificationOption specificationOption = new SpecificationOption();
            specificationOption.setSpecId(specId);
            specificationOption.setOptionName(optionName);
            specificationOptionMapper.insert(specificationOption);
        }
    }

    @Override
    public void deleteOption(Long[] ids) {
        specificationOptionMapper.deleteBatchIds(Arrays.asList(ids));
    }
}

