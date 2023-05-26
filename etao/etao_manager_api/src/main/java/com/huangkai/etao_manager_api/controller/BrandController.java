package com.huangkai.etao_manager_api.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.Brand;
import com.huangkai.etao_common.result.BaseResult;
import com.huangkai.etao_common.service.BrandService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Huangkai on 2023/5/18
 */
/**
 * 品牌
 */
@RestController
@RequestMapping("/brand")
public class BrandController {
    // 远程注入
    @DubboReference
    private BrandService brandService;


    /**
     * 根据id查询品牌
     *
     * @param id 品牌id
     * @return 查询结果
     */
    @GetMapping("/findById")
    public BaseResult<Brand> findById(Long id) {
        Brand brand = brandService.findById(id);
        return BaseResult.ok(brand);
    }


    /**
     * 查询所有品牌
     *
     * @return 所有品牌
     */
    @GetMapping("/all")
    public BaseResult<List<Brand>> findAll() {
        List<Brand> brands = brandService.findAll();
        return BaseResult.ok(brands);
    }


    /**
     * 新增品牌
     *当参数没有加上@RequestBody时，程序是获取不到前端传来的数据的
     * @param brand 品牌对象
     * @return 执行结果
     */
    @PostMapping("/add")
    public BaseResult add(@RequestBody Brand brand) {
        brandService.add(brand);
        return BaseResult.ok();
    }


    /**
     * 修改品牌
     *
     * @param brand 品牌对象
     * @return 执行结果
     */
    @PutMapping("/update")
    public BaseResult update(@RequestBody Brand brand) {
        brandService.update(brand);
        return BaseResult.ok();
    }


    /**
     * 删除品牌
     *
     * @param id 品牌id
     * @return 执行结果
     */
    @DeleteMapping("/delete")
    public BaseResult delete(Long id) {
        brandService.delete(id);
        return BaseResult.ok();
    }


    /**
     * 分页查询品牌
     *
     * @param brand 查询条件对象
     * @param page  页码
     * @param size  每页条数
     * @return 查询结果
     */
    @GetMapping("/search")
    public BaseResult<Page<Brand>> search(Brand brand, int page, int size) {
        Page<Brand> page1 = brandService.search(brand, page, size);
        return BaseResult.ok(page1);
    }
}



