
package com.huangkai.etao_common.domain;





/**
 * 在ES中存储的商品实体类
 * 在本项目中，使用的框架是spring data es框架来操作实体类
 *
 */

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Document(indexName = "goods",createIndex = false)//实体类对应es的索引名叫做goods，不自动创建索引
@Data
public class GoodsES implements Serializable {
    //es中的列
    @Field
    private Long id; // 商品id
    @Field
    private String goodsName; // 商品名称
    @Field
    private String caption; // 副标题
    @Field
    private BigDecimal price; // 价格
    @Field
    private String headerPic; // 头图
//上面几项完全是从商品实体类（Goods）中取出来的
    //下面就是不一样的

    @Field
    private String brand; // 品牌名称
    @CompletionField
    private List<String> tags; // 关键字，每个商品提取出关键字
    @Field
    private List<String> productType; // 类目名，这里是三级类目直接封装成的集合
    @Field
    private Map<String,List<String>> specification; // 规格,键为规格项（例如内存）,值为规格值（例64G,256G等）
}

