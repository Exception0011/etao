package com.huangkai.etao_manager_api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.Goods;
import com.huangkai.etao_common.result.BaseResult;
import com.huangkai.etao_common.service.GoodsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @author Huangkai on 2023/5/21
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @DubboReference
    private GoodsService goodsService;
    /**
     * 新增商品
     *
     * @param goods 商品实体
     * @return 执行结果
     */
    @PostMapping("/add")
    public BaseResult add(@RequestBody Goods goods) {
        goodsService.add(goods);
        return BaseResult.ok();
    }
    /**
     * 修改商品
     *
     * @param goods 商品实体
     * @return 执行结果
     */
    @PutMapping("/update")
    public BaseResult update(@RequestBody Goods goods) {
        goodsService.update(goods);
        return BaseResult.ok();
    }
    /**
     * 上架/下架商品
     *
     * @param id 商品id
     * @param isMarketable 是否上架
     * @return 执行结果
     */
    @PutMapping("/putAway")
    public BaseResult putAway(Long id,Boolean isMarketable) {
        goodsService.putAway(id,isMarketable);
        return BaseResult.ok();
    }
    /**
     * 根据id查询商品详情
     *
     * @param id 商品id
     * @return 商品详情
     */
    @GetMapping("/findById")
    public BaseResult<Goods> findById(Long id) {
        Goods goods = goodsService.findById(id);
        return BaseResult.ok(goods);
    }
    /**
     * 分页查询
     * @param goods 商品条件对象
     * @param page 页码
     * @param size 每页条数
     * @return 查询结果
     */
    @GetMapping("/search")
    public BaseResult<Page<Goods>> search(Goods goods, int page, int size) {
        Page<Goods> page1 = goodsService.search(goods, page, size);
        return BaseResult.ok(page1);
    }
}

