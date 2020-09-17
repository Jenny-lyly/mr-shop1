package com.baidu.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.feign.GoodsFeign;
import com.baidu.feign.SpecificationFeign;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDto;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.service.ShopElasticsearchService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.JSONUtil;
import com.github.pagehelper.PageInfo;
import com.netflix.ribbon.proxy.annotation.Http;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName ShopElasticsearchServiceImpl
 * @Description: ShopElasticsearchServiceImpl
 * @Author jinluying
 * @create: 2020-09-16 19:11
 * @Version V1.0
 **/
@RestController
public class ShopElasticsearchServiceImpl extends BaseApiService implements ShopElasticsearchService {

    @Autowired
    private GoodsFeign goodsFeign;
    @Autowired
    private SpecificationFeign specificationFeign;
    @Override
    public Result<JSONObject> esGoodsInfo() {

        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setRows(5);
        spuDTO.setPage(1);
        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(spuDTO);

        if(spuInfo.getCode() == HTTPStatus.OK){
            //查询出多个spu
            List<GoodsDoc> goodsDocs = new ArrayList<>();

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

                //sku数据填充
                Map<List<Long>, Map<String, Object>> skusAndPriceMap = this.getSkusAndPriceList(spu.getId());
                skusAndPriceMap.forEach((key,value) ->{
                    goodsDoc.setPrice(key);
                    goodsDoc.setSkus(JSONUtil.toJsonString(value));
                });

                //规格数据填充
                Map<String, Object> specMap = this.getSpecMap(spu);
                goodsDoc.setSpecs(specMap);
                goodsDocs.add(goodsDoc);
            });

            goodsDocs.forEach(good ->{
                System.out.println(good);
            });
        }
        return this.setResultSuccess();
    }

    private Map<List<Long>, Map<String, Object>> getSkusAndPriceList(Integer spuId){
        Map<List<Long>, Map<String, Object>> hashMap = new HashMap<>();
        //通过spuId 查询 skuList
        Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);

        List<Long> priceList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        if(skuResult.getCode() == HTTPStatus.OK){

            List<SkuDTO> skuList = skuResult.getData();
            skuList.stream().forEach(sku -> {
                map.put("id", sku.getId());
                map.put("title", sku.getTitle());
                map.put("images", sku.getImages());
                map.put("price", sku.getPrice());
                priceList.add(sku.getPrice().longValue());
            });
        }
        hashMap.put(priceList,map);
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
