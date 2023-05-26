package com.huangkai.etao_manager_api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.Role;
import com.huangkai.etao_common.result.BaseResult;
import com.huangkai.etao_common.service.RoleService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Huangkai on 2023/5/19
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    @DubboReference
    private RoleService roleService;

    /**添加角色
     * @param role:
     * @return BaseResult
     * @author Admin
     * @description TODO
     * @date 2022/11/28 7:48
     */
    @PostMapping("/add")
    public BaseResult add(@RequestBody Role role) {
        roleService.add(role);
        return BaseResult.ok();
    }

    /**更新角色
     * @param role:
     * @return BaseResult
     * @author Admin
     * @description TODO
     * @date 2022/11/28 7:48
     */
    @PutMapping("/update")
    public BaseResult update(@RequestBody Role role) {
        roleService.update(role);
        return BaseResult.ok();
    }

    /**通过id删除角色
     * @param rid:
     * @return BaseResult
     * @author Admin
     * @description TODO
     * @date 2022/11/28 7:49
     */
    @DeleteMapping("/delete")
    public BaseResult delete(Long rid) {
        roleService.delete(rid);
        return BaseResult.ok();
    }

    /**通过id查询权限
     * @param rid:
     * @return BaseResult<Role>
     * @author Admin
     * @description TODO
     * @date 2022/11/28 7:49
     */
    @GetMapping("/findById")
    public BaseResult<Role> findById(Long rid) {
        Role role = roleService.findById(rid);
        return BaseResult.ok(role);
    }

    /**分页查询
     * @param page:
     * @param size:
     * @return BaseResult<Page < Role>>
     * @author Admin
     * @description TODO
     * @date 2022/11/28 7:49
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('/role/search')")
    public BaseResult<Page<Role>> search(int page, int size) {
        Page<Role> page1 = roleService.search(page, size);
        return BaseResult.ok(page1);
    }
    /**
     * @param :  查询所有角色
     * @return BaseResult<List < Role>>
     * @author Admin
     * @description TODO
     * @date 2022/11/28 7:50
     */
    @GetMapping("/findAll")
    public BaseResult<List<Role>> findAll() {
        List<Role> all = roleService.findAll();
        return BaseResult.ok(all);
    }
    /**修改角色的权限
     * @param rid:
     * @param pids:
     * @return BaseResult
     * @author Admin
     * @description TODO
     * @date 2022/11/28 7:51
     */
    @PutMapping("/updatePermissionToRole")
    public BaseResult updatePermissionToRole(Long rid, Long[] pids) {
        roleService.addPermissionToRole(rid,pids);
        return BaseResult.ok();
    }
}


