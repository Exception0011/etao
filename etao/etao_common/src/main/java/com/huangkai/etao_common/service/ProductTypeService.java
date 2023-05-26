package com.huangkai.etao_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.ProductType;

import java.util.List;

/**
 * @author Huangkai on 2023/5/19
 */
public interface ProductTypeService {
    void add(ProductType productType);
    void update(ProductType productType);
    ProductType findById(Long id);
    void delete(Long id);
    Page<ProductType> search(ProductType productType,int page,int size);
    //根据条件查询商品类型
    List<ProductType> findProductType(ProductType productType);
}
