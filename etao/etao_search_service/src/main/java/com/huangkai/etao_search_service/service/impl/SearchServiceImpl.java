package com.huangkai.etao_search_service.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huangkai.etao_common.domain.*;
import com.huangkai.etao_common.service.SearchService;
import com.huangkai.etao_search_service.repository.GoodsEsRepository;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboService;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Huangkai on 2023/5/22
 */
@DubboService
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private GoodsEsRepository goodsESRepository;

    @Autowired
    private ElasticsearchRestTemplate template;

    @Autowired
    private RestHighLevelClient restHighLevelClient;



    // 监听同步商品队列
    @RabbitListener(queues = "sync_goods_queue")
    public void listenSyncQueue(GoodsDesc goodsDesc){
        syncGoodsToES(goodsDesc);
    }

    // 监听删除商品队列
    @RabbitListener(queues = "del_goods_queue")
    public void listenDelQueue(Long id){
        delete(id);
    }

    /**分词方法，写法固定
     * @param text: 被分的文本
     * @paramanalyze:  分词器
     * @return List<String>
     * @author Admin
     * @description TODO
     * @date 2022/12/2 15:52
     */
    @SneakyThrows // 抛出已检查异常
    public List<String> analyze(String text, String analyzer){
        // 分词请求
        AnalyzeRequest request = AnalyzeRequest.withIndexAnalyzer("goods",analyzer, text);
        // 分词响应
        AnalyzeResponse response = restHighLevelClient.indices().analyze(request, RequestOptions.DEFAULT);
        // 分词结果集合
        List<String> words = new ArrayList<>();
        // 处理响应
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            String term = token.getTerm(); // 分出的词
            words.add(term);
        }
        return words;
    }

    //自动补全
    @Override
    public List<String> autoSuggest(String keyword) {
        // 1.创建补全条件
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        SuggestionBuilder suggestionBuilder = SuggestBuilders
                .completionSuggestion("tags")
                .prefix(keyword)
                .skipDuplicates(true)
                .size(10);
        suggestBuilder.addSuggestion("prefix_suggestion", suggestionBuilder);
        // 2.发送请求
        SearchResponse response = template.suggest(suggestBuilder, IndexCoordinates.of("goods"));
        // 3.处理结果
        List<String> result = response
                .getSuggest()
                .getSuggestion("prefix_suggestion")
                .getEntries()
                .get(0)
                .getOptions()
                .stream()
                .map(Suggest.Suggestion.Entry.Option::getText)
                .map(Text::toString)
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public GoodsSearchResult search(GoodsSearchParam goodsSearchParam) {
        //构造Es搜索条件,有很多条件，所以单独写一个方法
        NativeSearchQuery query = buildQuery(goodsSearchParam);
        //搜索,分装成GoodsEs对象
        SearchHits<GoodsES> search = template.search(query, GoodsES.class);
        //将查询结果分装为Page对象，MyBatis是可以自动分装为Page对象的,SpringDataEs不行
        //将SearchHits装list再转Page
        List<GoodsES> content = new ArrayList<>();
        for (SearchHit<GoodsES> goodsESSearchHits:search) {
            GoodsES goodsES = goodsESSearchHits.getContent();
            content.add(goodsES);
        }
        Page<GoodsES> page = new Page<>();
        //当前页
        page.setCurrent(goodsSearchParam.getPage())
                //每页条数
                .setSize(goodsSearchParam.getSize())
                //总条数
                .setTotal(search.getTotalHits())
                //结果集
                .setRecords(content);
        //分装结果对象（查询结果，查询参数，查询面板）
        GoodsSearchResult goodsSearchResult = new GoodsSearchResult();
        goodsSearchResult.setGoodsPage(page);
        goodsSearchResult.setGoodsSearchParam(goodsSearchParam);

        //这里开始分装查询面板
        buildSearchPanel(goodsSearchParam,goodsSearchResult);
        return goodsSearchResult;
    }
    /**构造搜索条件
     * @param goodsSearchParam:  查询条件对象
     * @return NativeSearchQuery  搜索条件对象
     * @author Admin
     * @description TODO
     * @date 2022/12/3 13:38
     */
    public NativeSearchQuery buildQuery(GoodsSearchParam goodsSearchParam){
        //构造复杂查询条件对象
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        //如果查询条件有关键词，关键词可以匹配商品名，父标题，品牌字段，否则查询所有商品
        if (StringUtils.hasText(goodsSearchParam.getKeyword())){
            //查询所有
            MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
            builder.must(matchAllQueryBuilder);
        }else {
            String keywords = goodsSearchParam.getKeyword();
            //多字段搜索,先分词后匹配
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keywords, "goodsName", "caption", "brand");
            builder.must(multiMatchQueryBuilder);
        }
        //如果查询条件有品牌
        String brand = goodsSearchParam.getBrand();
        if (StringUtils.hasText(brand)){
            //则精准匹配品牌，不分词直接匹配
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("brand", brand);
            builder.must(termQueryBuilder);
        }
        //如果查询条件有价格则匹配价格
        Double highPrice = goodsSearchParam.getHighPrice();
        Double lowPrice = goodsSearchParam.getLowPrice();
        if (highPrice!=null&&highPrice!=0){
            RangeQueryBuilder lte = QueryBuilders.rangeQuery("price").lte(highPrice);
            builder.must(lte);
        }
        if (lowPrice!=null&&lowPrice!=0){
            RangeQueryBuilder gte = QueryBuilders.rangeQuery("price").gte(lowPrice);
            builder.must(gte);
        }
        //如果查询条件有规格项，则匹配规格项
        Map<String, String> specificationOption = goodsSearchParam.getSpecificationOption();
        if (specificationOption!=null&&specificationOption.size()>0){
            Set<Map.Entry<String, String>> entries = specificationOption.entrySet();
            for (Map.Entry<String,String> entry: entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.hasText(key)){
                    //要加上.keyword，可以理解为固定写法
                    TermQueryBuilder termQuery = QueryBuilders.termQuery("specification." + key + ".keyword", value);
                    builder.must(termQuery);
                }
            }
        }
        //分页条件,mybatis分页从1开始，Es从0开始
        PageRequest pageable = PageRequest.of(goodsSearchParam.getPage() - 1, goodsSearchParam.getSize());
        //查询构造器
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(builder).withPageable(pageable);
        //如果查询条件有排序，则添加排序条件
        String sortFiled = goodsSearchParam.getSortFiled();
        String sort = goodsSearchParam.getSort();
        SortBuilder sortBuilder = null;
        if (StringUtils.hasText(sort)&&StringUtils.hasText(sortFiled)){
            //
            if (sortFiled.equals("NEW")){
                //新品的正序是id的倒序
                sortBuilder = SortBuilders.fieldSort("id");
                if (sort.equals("ASC")){
                    sortBuilder.order(SortOrder.DESC);
                }
                if (sort.equals("DESC")){
                    sortBuilder.order(SortOrder.ASC);
                }
            }
            if (sortFiled.equals("PRICE")){
                sortBuilder = SortBuilders.fieldSort("price");
                if (sort.equals("ASC")){
                    sortBuilder.order(SortOrder.ASC);
                }
                if (sort.equals("DESC")){
                    sortBuilder.order(SortOrder.DESC);
                }
            }
            nativeSearchQueryBuilder.withSorts(sortBuilder);
        }
        //返回查询条件对象
        NativeSearchQuery query = nativeSearchQueryBuilder.build();
        return query;
    }

    /**封装查询面板，即根据查询条件，找到查询结果关联度前20的商品进行封装
     * @param goodsSearchParam:
     * @param goodsSearchResult:
     * @return void
     * @author Admin
     * @description TODO
     * @date 2022/12/3 14:31
     */
    public void buildSearchPanel(GoodsSearchParam goodsSearchParam,GoodsSearchResult goodsSearchResult){
        //构造搜索条件,相当于分页查询
        goodsSearchParam.setPage(1);
        goodsSearchParam.setSize(20);
        goodsSearchParam.setSort(null);//不用排序，不排序Es会按照关联度为我们排序
        goodsSearchParam.setSortFiled(null);
        NativeSearchQuery query = buildQuery(goodsSearchParam);
        //搜索
        SearchHits<GoodsES> search = template.search(query, GoodsES.class);
        //将结果封装为list对象
        List<GoodsES> content = new ArrayList<>();
        for (SearchHit<GoodsES> goodsESSearchHit:search) {
            GoodsES goodsES = goodsESSearchHit.getContent();
            content.add(goodsES);
        }
        //遍历list集合，封装查询面板(商品相关的品牌列表，商品相关的类型列表，商品相关的规格列表)
        Set<String> brand = new HashSet<>();
        Set<String> productType = new HashSet<>();
        Map<String,Set<String>> specifications = new HashMap<>();
        for (GoodsES goodsES:content) {
            //获取品牌
            brand.add(goodsES.getBrand());
            //获取类型
            List<String> goodsESProductType = goodsES.getProductType();
            goodsESProductType.addAll(productType);
            //获取规格
            Map<String, List<String>> specification = goodsES.getSpecification();
            Set<Map.Entry<String, List<String>>> entries = specification.entrySet();
            for (Map.Entry<String, List<String>> entry :entries ) {
                //规格名
                String key = entry.getKey();
                //规格值
                List<String> value = entry.getValue();
                //如果没有遍历出该规格，新增键值对，如果已经遍历出该规格，则向规格中添加规格项
                if (!specifications.containsKey(key)){
                    specifications.put(key,new HashSet(value));
                }else {
                    specification.get(key).addAll(value);
                }

            }

        }
        goodsSearchResult.setBrands(brand);
        goodsSearchResult.setProductType(productType);
        goodsSearchResult.setSpecifications(specifications);
    }
    //向es同步数据只运行一次
    @Override
    public void syncGoodsToES(GoodsDesc goodsDesc) {
        //将商品详情对象转为godsEs对象
        GoodsES goodsES = new GoodsES();
        goodsES.setId(goodsDesc.getId());
        goodsES.setGoodsName(goodsDesc.getGoodsName());
        goodsES.setCaption(goodsDesc.getCaption());
        goodsES.setPrice(goodsDesc.getPrice());
        goodsES.setHeaderPic(goodsDesc.getHeaderPic());
        goodsES.setBrand(goodsDesc.getBrand().getName());
        // 类型集合
        List<String> productType = new ArrayList();
        productType.add(goodsDesc.getProductType1().getName());
        productType.add(goodsDesc.getProductType2().getName());
        productType.add(goodsDesc.getProductType3().getName());
        goodsES.setProductType(productType);
        // 规格集合
        Map<String,List<String>> map = new HashMap();
        List<Specification> specifications = goodsDesc.getSpecifications();
        // 遍历规格
        for (Specification specification : specifications) {
            // 规格项集合
            List<SpecificationOption> options = specification.getSpecificationOptions();
            // 规格项名集合
            List<String> optionStrList = new ArrayList();
            for (SpecificationOption option : options) {
                optionStrList.add(option.getOptionName());
            }
            map.put(specification.getSpecName(),optionStrList);
        }
        goodsES.setSpecification(map);
        // 关键字
        List<String> tags = new ArrayList();
        tags.add(goodsDesc.getBrand().getName()); //品牌名是关键字
        tags.addAll(analyze(goodsDesc.getGoodsName(),"ik_smart"));//商品名分词后为关键词
        tags.addAll(analyze(goodsDesc.getCaption(),"ik_smart"));//副标题分词后为关键词
        goodsES.setTags(tags);
        goodsESRepository.save(goodsES);
    }
    //删除es，不主动调用
    @Override
    public void delete(Long id) {
        goodsESRepository.deleteById(id);
    }
}
