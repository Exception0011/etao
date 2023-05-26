package com.huangkai.etao_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.Admin;
import com.huangkai.etao_common.domain.Permission;

import java.util.List;

/**
 * @author Huangkai on 2023/5/18
 */
public interface AdminService {
    //新增管理员
    void add(Admin admin);
    //修改管理员
    void update(Admin admin);
    //删除管理员
    void delete(Long id);
    //根据id查询管理员,这里是要查询出所有的角色和权限
    Admin findById(Long id);
    //分页查询管理员
    Page<Admin> findPage(int page, int size);
    //修改管理员角色
    void updateRoleToAdmin(Long aid,Long[] rids);

    Admin findByAdminName(String username);
    List<Permission> findAllPermission(String username);
}
