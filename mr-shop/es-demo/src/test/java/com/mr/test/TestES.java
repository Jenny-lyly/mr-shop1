package com.mr.test;

import com.baidu.RunTestEsApplication;
import com.baidu.entity.GoodsEntity;
import com.baidu.repository.GoodsRepository;
import com.baidu.util.ESHighLightUtil;
import org.elasticsearch.client.core.TermVectorsResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Test;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName Test
 * @Description: Test
 * @Author jinluying
 * @create: 2020-09-14 14:09
 * @Version V1.0
 **/
//让测试在Spring容器环境下执行
@RunWith(SpringRunner.class)
//声明启动类,当测试方法运行的时候会帮我们自动启动容器
@SpringBootTest(classes = { RunTestEsApplication.class})
public class TestES {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private GoodsRepository goodsRepository;

    @Test
    public void createGoodsIndex(){
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(IndexCoordinates.of("mrgoods"));

        indexOperations.create();//创建索引
        System.out.println(indexOperations.exists()?"索引创建成功":"索引创建失败");
    }

    @Test
    public void createMappings(){
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsEntity.class);
        System.out.println("索引创建成功");
    }

    /*
        删除索引
    */
    @Test
    public void deleteGoodsIndex(){
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsEntity.class);

        indexOperations.delete();
        System.out.println("索引删除成功");
    }

    @Test
    public void saveData(){
        GoodsEntity entity = new GoodsEntity();
        entity.setId(5L);
        entity.setTitle("华为nova7");
        entity.setBrand("华为1");
        entity.setCategory("手机1");
        entity.setImages("huawei.jpg");
        //新增
//        goodsRepository.save(entity);
        entity.setPrice(3000D);
        goodsRepository.save(entity);
        System.out.println("新增成功");
    }

    /*
批量新增文档
 */
    @Test
    public void saveAllData(){

        GoodsEntity entity = new GoodsEntity();
        entity.setId(2L);
        entity.setBrand("苹果");
        entity.setCategory("手机");
        entity.setImages("pingguo.jpg");
        entity.setPrice(5000D);
        entity.setTitle("iphone11手机");

        GoodsEntity entity2 = new GoodsEntity();
        entity2.setId(3L);
        entity2.setBrand("三星");
        entity2.setCategory("手机");
        entity2.setImages("sanxing.jpg");
        entity2.setPrice(3000D);
        entity2.setTitle("w2019手机");

        GoodsEntity entity3 = new GoodsEntity();
        entity3.setId(4L);
        entity3.setBrand("小米");
        entity3.setCategory("手机");
        entity3.setImages("xiaomi.jpg");
        entity3.setPrice(4000D);
        entity3.setTitle("小米10手机");

        goodsRepository.saveAll(Arrays.asList(entity,entity2,entity3));

        System.out.println("批量新增成功");
    }

    /*
    查询所有
    */
    @Test
    public void searchAll(){
        //查询总条数
        long count = goodsRepository.count();
        System.out.println(count);
        //查询所有数据
        Iterable<GoodsEntity> all = goodsRepository.findAll();
        all.forEach(goods -> {
            System.out.println(goods);
        });
    }

    /*
    删除文档
     */
    @Test
    public void delData(){

        GoodsEntity entity = new GoodsEntity();
        entity.setId(1L);

        goodsRepository.delete(entity);

        System.out.println("删除成功");
    }

    @Test
    public void selectData(){
//        List<GoodsEntity> list = goodsRepository.findByTitle("w2019");
//        System.out.println(list);
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//        queryBuilder.withQuery(QueryBuilders.matchQuery("title","小米"));

        queryBuilder.withQuery(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("title","w2019"))
                        .must(QueryBuilders.rangeQuery("price").gte(1000).lte(3000))
        );

//        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);
        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);
        System.out.println(search.getSearchHits());

    }

    @Test
    public void searchHighLight(){

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        HighlightBuilder highlightBuilder = ESHighLightUtil.getHighlightBuilder("title");


        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        queryBuilder.withPageable(PageRequest.of(0,2));
        queryBuilder.withHighlightBuilder(highlightBuilder);

        queryBuilder.withQuery(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("title","w2019"))
                        .must(QueryBuilders.rangeQuery("price").gte(1000).lte(30000))
        );

        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);
        List<SearchHit<GoodsEntity>> hitList = search.getSearchHits();

        List<SearchHit<GoodsEntity>> highLightHit = ESHighLightUtil.getHighLightHit(hitList);
        System.out.println(highLightHit);
    }

    @Test
    public void serachAgg(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        queryBuilder.addAggregation(AggregationBuilders.terms("brand_agg").field("brand"));

        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);

        Aggregations aggregations = search.getAggregations();
        Terms terms = aggregations.get("brand_agg");

        List<? extends Terms.Bucket> buckets = terms.getBuckets();

        buckets.forEach(bucket ->{
            System.out.println(bucket.getKeyAsString()+":"+bucket.getDocCount());
        });
        System.out.println(search);
    }

    @Test
    public void searchAggMethod(){

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        queryBuilder.addAggregation(
                AggregationBuilders.terms("brandName")
                        .field("brand")
                        .subAggregation(AggregationBuilders.max("max_price").field("price")));

        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);

        Aggregations aggregations = search.getAggregations();

        Terms terms = aggregations.get("brandName");

        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        buckets.forEach(bucket ->{

            System.out.println(bucket.getKeyAsString()+":"+bucket.getDocCount());

            //获取聚合
            Aggregations aggregations1 = bucket.getAggregations();
            //得到map
            Map<String, Aggregation> map = aggregations1.asMap();

            Max max_price = (Max) map.get("max_price");

            System.out.println(max_price.getValue());
        });
    }
}
