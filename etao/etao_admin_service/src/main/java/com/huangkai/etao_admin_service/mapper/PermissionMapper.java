package com.huangkai.etao_admin_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huangkai.etao_common.domain.Permission;

/**
 * @author Huangkai on 2023/5/19
 */
public interface PermissionMapper extends BaseMapper<Permission> {
    void deletePermissionAllRole(Long id);

}
