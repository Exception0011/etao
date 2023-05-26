package com.huangkai.etao_admin_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huangkai.etao_common.domain.Admin;
import com.huangkai.etao_common.domain.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Huangkai on 2023/5/18
 */
public interface AdminMapper extends BaseMapper<Admin> {
    void deleteAdminAllRole(Long id);

    void addRoleToAdmin(@Param("aid") Long aid,@Param("rid") Long rid);

    Admin findById(Long id);

    List<Permission> findAllPermission(String username);
}

