package com.huangkai.etao_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.Permission;

import java.util.List;

/**
 * @author Huangkai on 2023/5/19
 */
public interface PermissionService {
    void add(Permission permission);
    void update(Permission permission);
    void delete(Long id);
    Permission findById(Long id);
    Page<Permission> search(int page ,int size);
    List<Permission> findAll();
}

