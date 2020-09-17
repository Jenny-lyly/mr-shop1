package com.baidu.repository;

import com.baidu.entity.GoodsEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @ClassName GoodsRepository
 * @Description: GoodsRepository
 * @Author jinluying
 * @create: 2020-09-14 14:32
 * @Version V1.0
 **/
public interface GoodsRepository  extends ElasticsearchRepository<GoodsEntity,Long> {

    List<GoodsEntity> findByTitle(String title);


}
