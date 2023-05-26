package com.huangkai.etao_goods_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huangkai.etao_common.domain.Specification;

import java.util.List;

/**
 * @author Huangkai on 2023/5/19
 */
public interface SpecificationMapper extends BaseMapper<Specification> {

    Specification findById(Long id);
    // 根据商品类型查询商品规格
    List<Specification> findByProductTypeId(Long productTypeId);
}

