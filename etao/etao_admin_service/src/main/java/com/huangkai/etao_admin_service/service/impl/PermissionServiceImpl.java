package com.huangkai.etao_admin_service.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_admin_service.mapper.PermissionMapper;
import com.huangkai.etao_common.domain.Permission;
import com.huangkai.etao_common.service.PermissionService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Huangkai on 2023/5/19
 */
@DubboService
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;
    @Override
    public void add(Permission permission) {
        permissionMapper.insert(permission);
    }

    @Override
    public void update(Permission permission) {
        permissionMapper.updateById(permission);
    }

    @Override
    public void delete(Long id) {
        permissionMapper.deleteById(id);
        // 删除角色_权限表中的相关数据
        permissionMapper.deletePermissionAllRole(id);
    }

    @Override
    public Permission findById(Long id) {
        return permissionMapper.selectById(id);
    }

    @Override
    public Page<Permission> search(int page, int size) {
        return permissionMapper.selectPage(new Page<>(page,size),null);
    }

    @Override
    public List<Permission> findAll() {
        return permissionMapper.selectList(null);
    }
}

