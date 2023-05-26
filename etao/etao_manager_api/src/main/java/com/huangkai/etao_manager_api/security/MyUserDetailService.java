package com.huangkai.etao_manager_api.security;

import com.huangkai.etao_common.domain.Admin;
import com.huangkai.etao_common.domain.Permission;
import com.huangkai.etao_common.service.AdminService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Huangkai on 2023/5/19
 */
@Service
public class MyUserDetailService implements UserDetailsService {
    @DubboReference
    private AdminService adminService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //认证
        Admin admin = adminService.findByAdminName(username);
        if (admin == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        //授权
        List<Permission> permissions = adminService.findAllPermission(username);
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Permission permission : permissions) {
            grantedAuthorities.add(new SimpleGrantedAuthority(permission.getUrl()));
        }
        //封装成UserDetailService
        UserDetails userDetails = User.withUsername(admin.getUsername())
                .password(admin.getPassword())
                .authorities(grantedAuthorities)
                .build();
        return userDetails;
    }
}

