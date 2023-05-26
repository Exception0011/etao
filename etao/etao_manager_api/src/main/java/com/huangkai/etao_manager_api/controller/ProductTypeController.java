package com.huangkai.etao_manager_api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.ProductType;
import com.huangkai.etao_common.result.BaseResult;
import com.huangkai.etao_common.service.ProductTypeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 *
 * @author Huangkai on 2023/5/21
 */
@RestController
@RequestMapping("/productType")
public class ProductTypeController {
    @DubboReference
    private ProductTypeService productTypeService;

    /**
     * 新增
     * @param productType:
     * @return BaseResult
     * @author huangkai
     * @description TODO
     * @date 2023/5/21 15:37
     */
    @PostMapping("add")
    public BaseResult add(@RequestBody ProductType productType){
        productTypeService.add(productType);
        return BaseResult.ok();
    }
    /**
     * 修改
     * @param productType:
     * @return BaseResult
     * @author huangkai
     * @description TODO
     * @date 2023/5/21 15:37
     */
    @PutMapping("update")
    public BaseResult update(@RequestBody ProductType productType){
        productTypeService.update(productType);
        return BaseResult.ok();
    }
    /**
     * 删除
     * @param id:
     * @return BaseResult
     * @author huangkai
     * @description TODO
     * @date 2023/5/21 15:37
     */
    @DeleteMapping("delete")
    public BaseResult delete(Long id){
        productTypeService.delete(id);
        return BaseResult.ok();
    }

    /**
     * 通过id查询
     * @param id:
     * @return BaseResult<ProductType>
     * @author huangkai
     * @description TODO
     * @date 2023/5/21 15:37
     */
    @GetMapping("findById")
    public BaseResult<ProductType> findById(Long id){
        ProductType productType = productTypeService.findById(id);
        return BaseResult.ok(productType);
    }

    /**
     * 分页查询
     * @param productType:
     * @param page:
     * @param size:
     * @return BaseResult<Page < ProductType>>
     * @author huangkai
     * @description TODO
     * @date 2023/5/21 15:37
     */
    @GetMapping("search")
    public BaseResult<Page<ProductType>> search(ProductType productType, int page, int size){
        return BaseResult.ok(productTypeService.search(productType,page,size));
    }
    /**
     * 根据条件查询商品类型
     * @param productType:
     * @return BaseResult<List < ProductType>>
     * @author huangkai
     * @description TODO
     * @date 2023/5/21 15:37
     */
    @GetMapping("findProductType")
    public BaseResult<List<ProductType>> findProductType(ProductType productType){
        return BaseResult.ok(productTypeService.findProductType(productType));
    }

    /**
     * 通过父id查询
     * @param parentId:
     * @return BaseResult<List < ProductType>>
     * @author huangkai
     * @description TODO
     * @date 2023/5/21 15:37
     */
    @GetMapping("findByParentId")
    public BaseResult<List<ProductType>> findByParentId(Long parentId){
        ProductType productType = new ProductType();
        productType.setParentId(parentId);
        return BaseResult.ok(productTypeService.findProductType(productType));
    }
}

