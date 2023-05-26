package com.huangkai.etao_common.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.SeckillGoods;

public interface SeckillGoodsService {
    /**
     * 添加秒杀商品
     *
     * @param seckillGoods 秒杀商品实体
     */
    void add(SeckillGoods seckillGoods);


    /**
     * 修改秒杀商品
     *
     * @param seckillGoods 秒杀商品实体
     */
    void update(SeckillGoods seckillGoods);


    /**
     * 分页查询秒杀商品
     *
     * @param page 页数
     * @param size 每页条数
     * @return 查询结果
     */
    Page<SeckillGoods> findPage(int page, int size);
}
