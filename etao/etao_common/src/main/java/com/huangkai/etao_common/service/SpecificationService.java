package com.huangkai.etao_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.Specification;
import com.huangkai.etao_common.domain.SpecificationOption;
import com.huangkai.etao_common.domain.SpecificationOptions;

import java.util.List;

/**
 * @author Huangkai on 2023/5/19
 */
public interface SpecificationService {
    void add(Specification specification);
    void update(Specification specification);
    // 删除商品规格
    void delete(Long[] ids);
    // 根据id查询商品规格
    Specification findById(Long id);
    // 分页查询商品规格
    Page<Specification> search(int page, int size);
    // 查询某种商品类型下的所有规格
    List<Specification> findByProductTypeId(Long id);
    // 新增商品规格项
    void addOption(SpecificationOptions specificationOptions);
    // 删除商品规格项
    void deleteOption(Long[] ids);

}
