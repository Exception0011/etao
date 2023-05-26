package com.huangkai.etao_common.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.Brand;

import java.util.List;

/**
* @author Admin
* @description 针对表【bz_brand】的数据库操作Service
* @createDate 2023-05-18 13:21:10
*/
public interface BrandService {

    /**
     *工具id查询广告
     * @param :
     * @return Brand
     * @author huangkai
     * @description TODO
     * @date 2023/5/18 13:30
     */
    Brand findById(Long id);
    List<Brand> findAll();
    void add(Brand brand);
    void update(Brand brand);
    void delete(Long id);
    Page<Brand> search(Brand brand,int page,int size);
}
