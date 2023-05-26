package com.huangkai.etao_common.service;

import com.huangkai.etao_common.domain.GoodsDesc;
import com.huangkai.etao_common.domain.GoodsSearchParam;
import com.huangkai.etao_common.domain.GoodsSearchResult;

import java.util.List;

/**
 * @author Huangkai on 2023/5/22
 */
public interface SearchService {
    /**
     * 自动补全关键字
     * @param keyword: 被补齐的词
     * @return List<String> 补齐的关键字集合
     * @author huangkai
     * @description TODO
     * @date 2023/5/22 16:25
     */
    List<String> autoSuggest(String keyword);


    /**
     * 搜索商品
     * @param goodsSearchParam:  搜索条件
     * @return GoodsSearchResult 搜索结果
     * @author huangkai
     * @description TODO
     * @date 2023/5/22 16:26
     */
    GoodsSearchResult search(GoodsSearchParam goodsSearchParam);

    /**
     * 向es中同步数据
     * @param goodsDesc: 商品详情
     * @return void
     * @author huangkai
     * @description TODO
     * @date 2023/5/22 16:28
     */
    void syncGoodsToES(GoodsDesc goodsDesc);


    /**
     * 删除ES中的商品数据
     * @param id:  商品id
     * @return void
     * @author huangkai
     * @description TODO
     * @date 2023/5/22 16:28
     */
    void delete(Long id);
}
