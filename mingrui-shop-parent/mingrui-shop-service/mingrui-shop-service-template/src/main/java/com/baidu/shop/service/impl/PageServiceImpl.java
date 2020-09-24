package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.fegin.BrandFeign;
import com.baidu.shop.fegin.CategoryFeign;
import com.baidu.shop.fegin.GoodsFeign;
import com.baidu.shop.fegin.SpecificationFeign;
import com.baidu.shop.service.PageService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName PageServiceImpl
 * @Description: PageServiceImpl
 * @Author jinluying
 * @create: 2020-09-23 19:42
 * @Version V1.0
 **/
@Service
public class PageServiceImpl implements PageService {

    @Resource
    private GoodsFeign goodsFeign;
    @Resource
    private BrandFeign brandFeign;
    @Resource
    private CategoryFeign categoryFeign;
    @Resource
    private SpecificationFeign specificationFeign;

    @Override
    public Map<String, Object> getGoodsInfo(Integer spuId) {
        Map<String, Object> map = new HashMap<>();
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        Result<List<SpuDTO>> spuInfoResult = goodsFeign.getSpuInfo(spuDTO);

        if(spuInfoResult.getCode() == 200){

            if(spuInfoResult.getData().size() == 1){
                //spu信息
                SpuDTO spu = spuInfoResult.getData().get(0);
                map.put("spuInfo",spu);
                
                //品牌信息
                BrandDto brandDto = new BrandDto();
                brandDto.setId(spu.getBrandId());
                Result<PageInfo<BrandEntity>> brandInfoResult = brandFeign.getBrandInfo(brandDto);
                if(brandInfoResult.getCode() == 200){

                    List<BrandEntity> brandList = brandInfoResult.getData().getList();
                    if(brandList.size() == 1){
                        map.put("brandInfo",brandList.get(0));
                    }
                }

                //spuDetail
                Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBySpuId(spuId);
                if(spuDetailResult.getCode() == 200){
                    SpuDetailEntity spuDetailInfo = spuDetailResult.getData();
                    map.put("spuDetailInfo",spuDetailInfo);
                }

                //分类信息
                Result<List<CategoryEntity>> categoryListResult = categoryFeign.getCategoryByIdList(String.join(","
                        , Arrays.asList(spu.getCid1() + "", spu.getCid2() + "", spu.getCid3() + "")));
                if(categoryListResult.getCode() == 200){
                    map.put("cateList",categoryListResult.getData());
                }

                //skus
                Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);
                if(skuResult.getCode() == 200){
                    List<SkuDTO> skuList = skuResult.getData();
                    map.put("skus",skuList);
                }

                //specParam 特有规格参数
                SpecParamDto specParamDto = new SpecParamDto();
                specParamDto.setCid(spu.getCid3());
                specParamDto.setGeneric(false);
                Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDto);
                if(specParamResult.getCode() == 200){
                    HashMap<Integer, String> specMap = new HashMap<>();
                    specParamResult.getData().stream().forEach(spec ->{
                        specMap.put(spec.getId(),spec.getName());
                    });
                    map.put("specParamMap",specMap);
                }

                //通用规格参数,规格组
                SpecGroupDto specGroupDto = new SpecGroupDto();
                specGroupDto.setCid(spu.getCid3());
                Result<List<SpecGroupEntity>> specGroupResult = specificationFeign.getSpecGroupInfo(specGroupDto);
                if(specGroupResult.getCode() == 200){
                    //规格组
                    List<SpecGroupEntity> specGroupInfo = specGroupResult.getData();

                    List<SpecGroupDto> groupsInParams = specGroupInfo.stream().map(specGroup -> {

                        //就规格组转换为规格组dto
                        SpecGroupDto specGroupDto1 = BaiduBeanUtil.copyProperties(specGroup, SpecGroupDto.class);
                        //通过规格组id查询 --> 通用规格参数
                        SpecParamDto specParam = new SpecParamDto();
                        specParam.setGroupId(specGroup.getId());
                        specParam.setGeneric(true);
                        Result<List<SpecParamEntity>> paramResult = specificationFeign.getSpecParamInfo(specParam);
                        if (paramResult.getCode() == 200) {
                            specGroupDto1.setSpecParams(paramResult.getData());
                        }

                        return specGroupDto1;
                    }).collect(Collectors.toList());

                    map.put("groupsInParams",groupsInParams);
                }
            }
        }
        return map;

    }
}
