package com.huangkai.etao_goods_service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.*;
import com.huangkai.etao_common.service.GoodsService;
import com.huangkai.etao_goods_service.mapper.GoodsImageMapper;
import com.huangkai.etao_goods_service.mapper.GoodsMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Huangkai on 2023/5/21
 */
@DubboService
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsImageMapper goodsImageMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Override
    public void add(Goods goods) {
        //插入商品数据
        goodsMapper.insert(goods);
        //插入图片数据
        Long goodsId = goods.getId();
        List<GoodsImage> images = goods.getImages();
        for (GoodsImage image : images) {
            image.setGoodsId(goodsId);
            goodsImageMapper.insert(image);
        }
        //插入商品——规格项数据
        //1获取规格
        List<Specification> specifications = goods.getSpecifications();
        //2获取规格项
        List<SpecificationOption> options = new ArrayList<>();
        //遍历规格获取规格中所有规格项
        for (Specification specification : specifications) {
            options.addAll(specification.getSpecificationOptions());
        }
        //遍历规格项，插入商品规格数据
        for (SpecificationOption option : options) {
            goodsMapper.addGoodsSpecificationOption(goodsId,option.getId());
        }

        // 将商品数据同步到ES中

        rabbitTemplate.convertAndSend("goods_exchange","sync_goods",findById(goodsId));

    }

    @Override
    public void update(Goods goods) {
        //删除旧图片数据
        Long goodsId = goods.getId(); // 商品id
        QueryWrapper<GoodsImage> queryWrapper = new QueryWrapper();
        queryWrapper.eq("goodsId",goodsId);
        goodsImageMapper.delete(queryWrapper);
        //删除旧规格项数据
        //插入商品数据
        goodsMapper.insert(goods);
        //插入图片数据
        goodsMapper.deleteGoodsSpecificationOption(goodsId);
        // 插入商品数据
        goodsMapper.updateById(goods);
        //插入图片数据
        List<GoodsImage> images = goods.getImages();
        for (GoodsImage image : images) {
            image.setGoodsId(goodsId);
            goodsImageMapper.insert(image);
        }
        //插入商品——规格项数据
        //1获取规格
        List<Specification> specifications = goods.getSpecifications();
        //2获取规格项
        List<SpecificationOption> options = new ArrayList<>();
        //遍历规格获取规格中所有规格项
        for (Specification specification : specifications) {
            options.addAll(specification.getSpecificationOptions());
        }
        //遍历规格项，插入商品规格数据
        for (SpecificationOption option : options) {
            goodsMapper.addGoodsSpecificationOption(goodsId,option.getId());
        }

        rabbitTemplate.convertAndSend("goods_exchange","sync_goods",findDesc(goodsId));
        //将修改数据同步到用户购物车
        CartGoods cartGoods = new CartGoods();
        cartGoods.setGoodId(goods.getId());
        cartGoods.setGoodsName(goods.getGoodsName());
        cartGoods.setHeaderPic(goods.getHeaderPic());
        cartGoods.setPrice(goods.getPrice());
        rabbitTemplate.convertAndSend("goods_exchange","sync_cart",cartGoods);

    }

    @Override
    public Goods findById(Long id) {
        return goodsMapper.findById(id);
    }

    @Override
    public void putAway(Long id, Boolean isMarketable) {
        goodsMapper.putAway(id,isMarketable);
        if (isMarketable){
            // 上架时同步到ES
            rabbitTemplate.convertAndSend("goods_exchange","sync_goods",findDesc(id));
        }else{
            // 下架删除ES数据
            rabbitTemplate.convertAndSend("goods_exchange","del_goods",id);
            // 商品下架删除用户购物车
            CartGoods cartGoods = new CartGoods();
            cartGoods.setGoodId(id);
            rabbitTemplate.convertAndSend("goods_exchange","del_cart",cartGoods);

        }

    }

    @Override
    public Page<Goods> search(Goods goods, int page, int size) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        if (goods != null && StringUtils.hasText(goods.getGoodsName())){
            queryWrapper.like("goodsName",goods.getGoodsName());
        }
        Page<Goods> page1 = goodsMapper.selectPage(new Page(page,size),queryWrapper);

        return page1;
    }

    @Override
    public List<GoodsDesc> findAll() {
        return goodsMapper.findAll();
    }

    @Override
    public GoodsDesc findDesc(Long id) {
        return goodsMapper.findDesc(id);
    }
}

