
package com.huangkai.etao_common.domain;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 商品搜索结果
 */


@Data
public class GoodsSearchResult implements Serializable {
    private Page<GoodsES> goodsPage; // 页面商品信息
    private GoodsSearchParam goodsSearchParam; // 搜索条件回显，比如说在搜索框中输入电视，在搜索框中还会回显电视


    //就是面板里面的品牌列表
    private Set<String> brands; // 和商品有关的品牌列表
    private Set<String> productType; // 和商品有关的类别列表
    // 和商品有关的规格列表，键：规格名，值：规格集合
    private Map<String, Set<String>> specifications;
}

