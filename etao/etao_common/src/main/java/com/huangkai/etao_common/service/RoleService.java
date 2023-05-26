package com.huangkai.etao_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.Role;

import java.util.List;

/**
 * @author Huangkai on 2023/5/18
 */
public interface RoleService {
    void add(Role role);
    void update(Role role);
    void delete(long id);
    Role findById(Long id);
    List<Role> findAll();
    Page<Role> search(int page,int size);
    void addPermissionToRole(Long rid,Long[] pids);
}
