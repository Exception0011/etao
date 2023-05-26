package com.huangkai.etao_admin_service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_admin_service.mapper.AdminMapper;
import com.huangkai.etao_common.domain.Admin;
import com.huangkai.etao_common.domain.Permission;
import com.huangkai.etao_common.service.AdminService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Huangkai on 2023/5/18
 */
@DubboService
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Override
    public void add(Admin admin) {
        adminMapper.insert(admin);
    }

    @Override
    public void update(Admin admin) {
        //如果传来空则还是原来的密码
        if (!StringUtils.hasText(admin.getPassword())){
            //查出原来的密码
            String password = adminMapper.selectById(admin.getAid()).getPassword();
            admin.setPassword(password);
        }
        adminMapper.updateById(admin);
    }

    @Override
    public void delete(Long id) {
        //删除用户的所有角色
        adminMapper.deleteAdminAllRole(id);
        adminMapper.deleteById(id);
    }

    @Override
    public Admin findById(Long id) {
        Admin admin = adminMapper.findById(id);
        return admin;
    }

    @Override
    public Page<Admin> findPage(int page, int size) {
        Page<Admin> page1 = new Page<>(page, size);
        Page<Admin> adminPage = adminMapper.selectPage(page1, null);
        return adminPage;
    }

    @Override
    public void updateRoleToAdmin(Long aid, Long[] rids) {
        //删除用户的所有角色
        adminMapper.deleteAdminAllRole(aid);
        //重新添加管理员角色
        for (Long rid:rids) {
            adminMapper.addRoleToAdmin(aid,rid);
        }
    }

    @Override
    public Admin findByAdminName(String username) {
        QueryWrapper<Admin> wrapper = new QueryWrapper();
        wrapper.eq("username", username);
        Admin admin = adminMapper.selectOne(wrapper);
        return admin;
    }

    @Override
    public List<Permission> findAllPermission(String username) {
        return adminMapper.findAllPermission(username);
    }
}

