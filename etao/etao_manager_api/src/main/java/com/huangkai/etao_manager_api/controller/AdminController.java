package com.huangkai.etao_manager_api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.Admin;
import com.huangkai.etao_common.result.BaseResult;
import com.huangkai.etao_common.service.AdminService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author Huangkai on 2023/5/18
 */
@RestController
@RequestMapping("admin")
public class AdminController {
    @DubboReference
    private AdminService adminService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    /**添加管理员
     * @param admin: 管理员对象
     * @return void
     * @author Admin
     * @description TODO
     * @date 2022/11/27 16:46
     */
    @PostMapping("/add")
    public BaseResult add(@RequestBody Admin admin){
        String password = admin.getPassword();
        password = passwordEncoder.encode(password);
        admin.setPassword(password);
        adminService.add(admin);
        return BaseResult.ok();
    }


    /**修改管理员
     * @param admin:
     * @return void
     * @author Admin
     * @description TODO
     * @date 2022/11/27 16:48
     */
    @PutMapping("update")
    public BaseResult update(@RequestBody Admin admin){
        String password = admin.getPassword();
        if (StringUtils.hasText(password)){
            password = passwordEncoder.encode(password);
            admin.setPassword(password);
        }
        adminService.update(admin);
        return BaseResult.ok();
    }




    /**根据id删除管理员
     * @param aid:
     * @return void
     * @author Admin
     * @description TODO
     * @date 2022/11/27 16:56
     */
    @DeleteMapping("delete")
    public BaseResult delete(Long aid){
        adminService.delete(aid);
        return BaseResult.ok();
    }


    /**根据id查询管理员
     * @param aid:
     * @return BaseResult<Admin>
     * @author Admin
     * @description TODO
     * @date 2022/11/27 16:56
     */
    @GetMapping("findById")
    public BaseResult<Admin> findById(Long aid){
        Admin admin = adminService.findById(aid);
        return BaseResult.ok(admin);
    }


    /**分页查询
     * @param page:
     * @param size:
     * @return BaseResult<Page < Admin>>
     * @author Admin
     * @description TODO
     * @date 2022/11/27 16:57
     */
    @GetMapping("search")
    @PreAuthorize("hasAnyAuthority('/admin/search')")
    public BaseResult<Page<Admin>> search(int page , int size){
        Page<Admin> adminPage = adminService.findPage(page, size);
        return BaseResult.ok(adminPage);
    }

    /**更新管理员角色
     * @param aid:
     * @param rids:
     * @return BaseResult
     * @author Admin
     * @description TODO
     * @date 2022/11/27 16:57
     */
    @PutMapping("updateRoleToAdmin")
    public BaseResult updateRoleToAdmin(Long aid ,Long[] rids){
        adminService.updateRoleToAdmin(aid,rids);
        return BaseResult.ok();
    }
    /**
     * 获取登录管理员名
     *
     * @return 管理员名
     */
    @GetMapping("/getUsername")
    public BaseResult<String> getUsername() {
        // 1.获取会话对象
        SecurityContext context = SecurityContextHolder.getContext();
        // 2.获取认证对象
        Authentication authentication = context.getAuthentication();
        // 3.获取登录用户信息
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        return BaseResult.ok(username);
    }
}

