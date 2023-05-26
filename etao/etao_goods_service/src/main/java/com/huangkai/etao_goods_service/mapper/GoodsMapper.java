package com.huangkai.etao_goods_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huangkai.etao_common.domain.Goods;
import com.huangkai.etao_common.domain.GoodsDesc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Huangkai on 2023/5/21
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    void addGoodsSpecificationOption(@Param("gid")Long gid, @Param("optionId")Long optionId);

    void deleteGoodsSpecificationOption(Long gid);
    Goods findById(Long id);
    void putAway(@Param("id")Long id,@Param("isMarketable")Boolean isMarketable);

    //这里是开始向es里面操作了
    //查询所有商品详情
    List<GoodsDesc> findAll();

    //在前端页面中根据id查询商品，上面那个数据是不够的，这次返回GoodsDESC
    // 根据id查询商品详情
    GoodsDesc findDesc(Long id);
}

