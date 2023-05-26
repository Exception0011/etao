package com.huangkai.etao_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.Category;

import java.util.List;

/**
 * @author Huangkai on 2023/5/22
 */
public interface CategoryService {
    void add(Category category);
    void update(Category category);
    void updateStatus(Long id,Integer status);
    void delete(Long[] ids);
    Category findById(Long id);
    Page<Category> search(int page,int size);
    //查询所有启用广告
    List<Category> findAll();
}
