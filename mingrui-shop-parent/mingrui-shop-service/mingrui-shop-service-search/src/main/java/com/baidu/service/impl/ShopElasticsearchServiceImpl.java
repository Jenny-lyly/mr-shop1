package com.baidu.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.feign.BrandFeign;
import com.baidu.feign.CategoryFeign;
import com.baidu.feign.GoodsFeign;
import com.baidu.feign.SpecificationFeign;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDto;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.response.GoodsResponse;
import com.baidu.shop.service.ShopElasticsearchService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.ESHighLightUtil;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.StringUtil;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ShopElasticsearchServiceImpl
 * @Description: ShopElasticsearchServiceImpl
 * @Author jinluying
 * @create: 2020-09-16 19:11
 * @Version V1.0
 **/
@RestController
@Slf4j
public class ShopElasticsearchServiceImpl extends BaseApiService implements ShopElasticsearchService {

    @Resource
    private GoodsFeign goodsFeign;
    @Resource
    private CategoryFeign categoryFeign;
    @Resource
    private BrandFeign brandFeign;
    @Resource
    private SpecificationFeign specificationFeign;
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 删除es中的数据
     * @return
     */
    @Override
    public Result<JSONObject> clearGoodsEsData() {
        IndexOperations indexOps = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if(indexOps.exists())indexOps.delete();
        return this.setResultSuccess();
    }

    /**
     * 初始话es库
     * @return
     */
    @Override
    public Result<JsonObject> initGoodsEsData() {
        IndexOperations indexOps = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if(!indexOps.exists()){
            indexOps.create();
            log.info("索引创建成功");
            indexOps.createMapping();
            log.info("映射创建成功");
        }

        List<GoodsDoc> goodsInfo = this.getEsGoodsInfo(new SpuDTO());
        elasticsearchRestTemplate.save(goodsInfo);
        return this.setResultSuccess();
    }

    /**
     * 搜索
     * @param search
     * @param page
     * @return
     */
    @Override
    public GoodsResponse search(String search,Integer page,String filter) {

        if (StringUtil.isEmpty(search))  throw new RuntimeException("搜索条件不能为空");
        //查询
        SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(this.getSearchQueryBuilder(search,page,filter).build(), GoodsDoc.class);
        //设置高亮字段
        List<SearchHit<GoodsDoc>> highLightHit = ESHighLightUtil.getHighLightHit(searchHits.getSearchHits());
        List<GoodsDoc> goodsDocs = highLightHit.stream().map(hit -> hit.getContent()).collect(Collectors.toList());
        //分页
        long total = searchHits.getTotalHits();
        long totalPage = Double.valueOf(Math.ceil(Long.valueOf(total).doubleValue() / 10)).longValue();
        //聚合
        Aggregations aggregations = searchHits.getAggregations();

        List<BrandEntity> brandList = this.getBrandList(aggregations);
        Map<Integer, List<CategoryEntity>> map = this.getCategoryList(aggregations);

        Integer hotCid = 0;
        List<CategoryEntity> categoryList = null;
        for(Map.Entry<Integer, List<CategoryEntity>> entry :map.entrySet()){
            hotCid = entry.getKey();
            categoryList = entry.getValue();
        }

        Map<String, List<String>> specParamMap = this.getSpecParam(hotCid, search);

        return new GoodsResponse(total, totalPage,brandList, categoryList, goodsDocs,specParamMap);
    }

    @Override
    public Result<JSONObject> saveData(Integer spuId) {
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        List<GoodsDoc> goodsDoc = this.getEsGoodsInfo(spuDTO);
        elasticsearchRestTemplate.save(goodsDoc.get(0));
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> delData(Integer spuId) {

        GoodsDoc goodsDoc = new GoodsDoc();
        goodsDoc.setId(spuId.longValue());
        elasticsearchRestTemplate.delete(goodsDoc);
        return this.setResultSuccess();
    }

    /**
     * 获得规格参数
     * @param hotCid
     * @param search
     * @return
     */
    private Map<String, List<String>> getSpecParam(Integer hotCid,String search){

        SpecParamDto specParamDto = new SpecParamDto();
        specParamDto.setCid(hotCid);
        specParamDto.setSearching(true);//只搜索有查询属性的规格参数
        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDto);

        if(specParamResult.getCode() == 200){

            List<SpecParamEntity> specParamList  = specParamResult.getData();

            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));
            //分页必须得查询一条数据
            queryBuilder.withPageable(PageRequest.of(0,1));
            //聚合
            specParamList.stream().forEach(specParam ->{
                queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs."+specParam.getName()+".keyword"));
            });

            SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsDoc.class);
            Aggregations aggregations = searchHits.getAggregations();

            return this.getSpecParamMap(specParamList,aggregations);
        }
        return null;
    }

    private Map<String, List<String>> getSpecParamMap(List<SpecParamEntity> specParamList,Aggregations aggregations){

        //key:paramName value-->aggr
        Map<String, List<String>> map = new HashMap<>();
        specParamList.stream().forEach(specParam ->{

            Terms terms = aggregations.get(specParam.getName());
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            //key中存储了我们需要的规格参数的值,类型为string
            List<String> valueList = buckets.stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());

            map.put(specParam.getName(),valueList);
        });

        return map;
    }

    /**
     * 构建查询
     * @param search
     * @param page
     * @return
     */
    private NativeSearchQueryBuilder getSearchQueryBuilder(String search,Integer page,String filter){

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));

        if(StringUtil.isNotEmpty(filter) && filter.length() > 2){

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Map<String, String> filterMap = JSONUtil.toMapValueString(filter);

            filterMap.forEach((key,value) ->{

                MatchQueryBuilder matchQueryBuilder = null;
                if (key.equals("cid3") || key.equals("brandId")) {
                     matchQueryBuilder = QueryBuilders.matchQuery(key, value);
                }else{
                    matchQueryBuilder = QueryBuilders.matchQuery("specs."+key+".keyword",value);
                }
                boolQueryBuilder.must(matchQueryBuilder);
            });
            queryBuilder.withFilter(boolQueryBuilder);
        }
        //分页
        queryBuilder.withPageable(PageRequest.of(page-1,10));
        //高亮
        queryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilder("title"));

        //聚合品牌,分类
        queryBuilder.addAggregation(AggregationBuilders.terms("cid_agg").field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms("brand_agg").field("brandId"));
        return queryBuilder;
    };

    /**
     * 获得品牌
     * @param aggregations
     * @return
     */
    private List<BrandEntity> getBrandList(Aggregations aggregations){

        Terms brand_agg = aggregations.get("brand_agg");

        List<String> brandIdList = brand_agg.getBuckets().stream().map(brandBucket -> {
            Number keyAsNumber = brandBucket.getKeyAsNumber();
            return keyAsNumber.intValue() + "";
        }).collect(Collectors.toList());

        String brandIdStr = String.join(",", brandIdList);
        Result<List<BrandEntity>> brandResult = brandFeign.getBrandByIdList(brandIdStr);
        return brandResult.getData();
    }

    /**
     * 分类
     * @param aggregations
     * @return
     */
    private Map<Integer, List<CategoryEntity>> getCategoryList(Aggregations aggregations){

        Terms cid_agg = aggregations.get("cid_agg");
        List<? extends Terms.Bucket> cidBuckets = cid_agg.getBuckets();

        List<Integer> hotCidArr = Arrays.asList(0);
        List<Long> maxCount = Arrays.asList(0L);

        Map<Integer, List<CategoryEntity>> map = new HashMap<>();
        //返回string类型的id集合去查询数据
        List<String> cidList = cidBuckets.stream().map(cidBucket -> {

            Number keyAsNumber = cidBucket.getKeyAsNumber();

            if(cidBucket.getDocCount() > maxCount.get(0)){

                maxCount.set(0,cidBucket.getDocCount());
                hotCidArr.set(0,keyAsNumber.intValue());
            }
            return keyAsNumber.intValue() + "";
        }).collect(Collectors.toList());

        //通过分类id集合去查询数据
        //将List集合转换成,分隔的string字符串
        // String.join(",", cidList); 通过,分隔list集合 --> 返回,拼接的string字符串
        String cidStr = String.join(",", cidList);
        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByIdList(cidStr);

        map.put(hotCidArr.get(0),categoryResult.getData());

         return map;
    };

    private List<GoodsDoc> getEsGoodsInfo(SpuDTO spuDTO) {

        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(spuDTO);
        //查询出多个spu
        List<GoodsDoc> goodsDocs = new ArrayList<>();
        if(spuInfo.getCode() == HTTPStatus.OK){

            List<SpuDTO> spuList = spuInfo.getData();

            spuList.stream().forEach(spu ->{

                GoodsDoc goodsDoc = new GoodsDoc();
                //  spu信息填充
                goodsDoc.setId(spu.getId().longValue());
                goodsDoc.setTitle(spu.getTitle());
                goodsDoc.setSubTitle(spu.getSubTitle());
                goodsDoc.setBrandName(spu.getBrandName());
                goodsDoc.setCategoryName(spu.getCategoryName());
                goodsDoc.setCid1(spu.getCid1().longValue());
                goodsDoc.setCid2(spu.getCid2().longValue());
                goodsDoc.setCid3(spu.getCid3().longValue());
                goodsDoc.setCreateTime(spu.getCreateTime());
                goodsDoc.setBrandId(spu.getBrandId().longValue());

                //sku数据填充
                Map<List<Long>, List<Map<String, Object>>> skusAndPriceMap = this.getSkusAndPriceList(spu.getId());
                skusAndPriceMap.forEach((key,value) ->{
                    goodsDoc.setPrice(key);
                    goodsDoc.setSkus(JSONUtil.toJsonString(value));
                });


                //规格数据填充
                Map<String, Object> specMap = this.getSpecMap(spu);
                goodsDoc.setSpecs(specMap);
                goodsDocs.add(goodsDoc);
            });
        }
        return goodsDocs;
    }

    private Map<List<Long>, List<Map<String, Object>>> getSkusAndPriceList(Integer spuId){
        Map<List<Long>, List<Map<String, Object>>> hashMap = new HashMap<>();
        //通过spuId 查询 skuList
        Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);

        List<Long> priceList = new ArrayList<>();
        List<Map<String, Object>> skuMap = null;

        if(skuResult.getCode() == HTTPStatus.OK){

            List<SkuDTO> skuList = skuResult.getData();
             skuMap = skuList.stream().map(sku -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", sku.getId());
                map.put("title", sku.getTitle());
                map.put("images", sku.getImages());
                map.put("price", sku.getPrice());
                priceList.add(sku.getPrice().longValue());
                return map;
            }).collect(Collectors.toList());
        }
        hashMap.put(priceList,skuMap);
        return hashMap;
    }

    private Map<String, Object> getSpecMap(SpuDTO spuDTO){
        //通过cid3查询规格参数
        SpecParamDto specParamDto = new SpecParamDto();
        specParamDto.setCid(spuDTO.getCid3());

        //只有规格参数的id和规格参数的名字
        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDto);

        Map<String, Object> specMap = new HashMap<>();
        if(specParamResult.getCode() == HTTPStatus.OK){
            List<SpecParamEntity> specParamList = specParamResult.getData();

            //通过spuid去查询spuDetail,detail里面有通用和特殊规格参数的值
            Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBySpuId(spuDTO.getId());
            if(spuDetailResult.getCode() == HTTPStatus.OK){

                SpuDetailEntity spuDetailInfo = spuDetailResult.getData();
                //获得通用规格参数的值
                String genericSpec = spuDetailInfo.getGenericSpec();
                Map<String, String> genericSpecMap = JSONUtil.toMapValueString(genericSpec);

                //获得通用规格参数的值
                String specialSpec = spuDetailInfo.getSpecialSpec();
                Map<String, List<String>> specialSpecMap = JSONUtil.toMapValueStrList(specialSpec);

                specParamList.stream().forEach(param ->{
                    if(param.getGeneric()){
                        if(param.getNumeric() && param.getSearching()){
                            specMap.put(param.getName(),this.chooseSegment(genericSpecMap.get(param.getId()+""),param.getSegments(),param.getUnit()));
                        }else{
                            specMap.put(param.getName(),genericSpecMap.get(param.getId()+""));
                        }
                    }else{
                        specMap.put(param.getName(),specialSpecMap.get(param.getId()+""));
                    }
                });
            }
        }
        return specMap;
    }

    /**
     *  把具体的值转换成区间-->不做范围查询
     * @param value
     * @param segments
     * @param unit
     * @return
     */
    private String chooseSegment(String value, String segments, String unit) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + unit + "以上";
                }else if(begin == 0){
                    result = segs[1] + unit + "以下";
                }else{
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }


}
