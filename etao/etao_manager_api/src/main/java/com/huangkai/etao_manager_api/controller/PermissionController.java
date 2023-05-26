package com.huangkai.etao_manager_api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.Permission;
import com.huangkai.etao_common.result.BaseResult;
import com.huangkai.etao_common.service.PermissionService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Huangkai on 2023/5/19
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {
    @DubboReference
    private PermissionService permissionService;

    /**添加权限
     * @param permission:  从请求体中获取权限参数
     * @return BaseResult
     * @author Admin
     * @description TODO
     * @date 2022/11/28 7:41
     */
    @PostMapping("/add")
    public BaseResult add(@RequestBody Permission permission){
        permissionService.add(permission);
        return BaseResult.ok();
    }

    /**更新权限
     * @param permission:
     * @return BaseResult
     * @author Admin
     * @description TODO
     * @date 2022/11/28 7:45
     */
    @PutMapping("/update")
    public BaseResult update(@RequestBody Permission permission){
        permissionService.update(permission);
        return BaseResult.ok();
    }

    /**删除权限
     * @param pid:
     * @return BaseResult
     * @author Admin
     * @description TODO
     * @date 2022/11/28 7:45
     */
    @DeleteMapping("/delete")
    public BaseResult delete(Long pid){
        permissionService.delete(pid);
        return BaseResult.ok();
    }
    /**通过id查询权限
     * @param pid:
     * @return BaseResult<Permission>
     * @author Admin
     * @description TODO
     * @date 2022/11/28 7:45
     */
    @GetMapping("/findById")
    public BaseResult<Permission> findById(Long pid){
        Permission permission = permissionService.findById(pid);
        return BaseResult.ok(permission);
    }

    /**分页查询
     * @param page:
     * @param size:
     * @return BaseResult<Page < Permission>>
     * @author Admin
     * @description TODO
     * @date 2022/11/28 7:47
     */
    @GetMapping("/search")
    public BaseResult<Page<Permission>> search(int page, int size){
        Page<Permission> permissionPage = permissionService.search(page, size);
        return BaseResult.ok(permissionPage);
    }

    /**查询所有权限
     * @param :
     * @return BaseResult<List < Permission>>
     * @author Admin
     * @description TODO
     * @date 2022/11/28 7:48
     */
    @GetMapping("/findAll")
    public BaseResult<List<Permission>> findAll(){
        List<Permission> all = permissionService.findAll();
        return BaseResult.ok(all);
    }
}



