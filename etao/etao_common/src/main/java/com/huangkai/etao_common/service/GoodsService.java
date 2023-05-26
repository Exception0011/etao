package com.huangkai.etao_common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.Goods;
import com.huangkai.etao_common.domain.GoodsDesc;

import java.util.List;

/**
 * @author Huangkai on 2023/5/21
 */
public interface GoodsService {
    void add(Goods goods);
    void update(Goods goods);
    Goods findById(Long id);
    //上下架商品
    void putAway(Long id,Boolean isMarketable);
    //分页查询
    Page<Goods> search(Goods goods,int page,int size);

    //这里开始是向es中同步
    //查询所有商品
    List<GoodsDesc> findAll();



    // 查询商品详情
    GoodsDesc findDesc(Long id);
}
