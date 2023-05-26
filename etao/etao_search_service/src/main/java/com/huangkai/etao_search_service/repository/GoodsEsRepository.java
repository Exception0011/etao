package com.huangkai.etao_search_service.repository;

import com.huangkai.etao_common.domain.GoodsES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Huangkai on 2023/5/22
 */
public interface GoodsEsRepository extends ElasticsearchRepository<GoodsES,Long> {
}
