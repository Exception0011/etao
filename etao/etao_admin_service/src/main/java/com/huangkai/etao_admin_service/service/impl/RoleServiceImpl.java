package com.huangkai.etao_admin_service.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_admin_service.mapper.RoleMapper;
import com.huangkai.etao_common.domain.Role;
import com.huangkai.etao_common.service.RoleService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Huangkai on 2023/5/18
 */
@DubboService
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleMapper roleMapper;
    @Override
    public void add(Role role) {
        roleMapper.insert(role);
    }

    @Override
    public void update(Role role) {
        roleMapper.updateById(role);
    }

    @Override
    public void delete(long id) {

        roleMapper.deleteById(id);
        // 删除角色的所有权限

        roleMapper.deleteRoleAllPermission(id);
        // 删除用户_角色中间表的相关数据
        roleMapper.deleteRoleAllAdmin(id);
    }

    @Override
    public Role findById(Long id) {
        return roleMapper.findById(id);
    }

    @Override
    public List<Role> findAll() {
        return roleMapper.selectList(null);
    }

    @Override
    public Page<Role> search(int page, int size) {
        return roleMapper.selectPage(new Page<>(page,size),null);
    }

    @Override
    public void addPermissionToRole(Long rid, Long[] pids) {
        // 删除角色的所有权限
        roleMapper.deleteRoleAllPermission(rid);
        for (Long pid : pids) {
            roleMapper.addPermissionToRole(rid,pid);
        }
    }
}

