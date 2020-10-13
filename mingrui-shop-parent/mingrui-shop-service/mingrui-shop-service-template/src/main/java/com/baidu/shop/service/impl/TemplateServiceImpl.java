package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.fegin.BrandFeign;
import com.baidu.shop.fegin.CategoryFeign;
import com.baidu.shop.fegin.GoodsFeign;
import com.baidu.shop.fegin.SpecificationFeign;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.github.pagehelper.PageInfo;
import com.mr.shop.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName TemplateServiceImpl
 * @Description: TemplateServiceImpl
 * @Author jinluying
 * @create: 2020-09-25 19:11
 * @Version V1.0
 **/
@RestController
public class TemplateServiceImpl extends BaseApiService implements TemplateService {

    @Resource
    private GoodsFeign goodsFeign;
    @Resource
    private BrandFeign brandFeign;
    @Resource
    private CategoryFeign categoryFeign;
    @Resource
    private SpecificationFeign specificationFeign;

    //注入静态化模版
    @Autowired
    private TemplateEngine templateEngine;

    //静态文件生成的路径
    @Value(value = "${mrshop.static.html.path}")
    private String staticHTMLPath;

    @Override
    public Result<JSONObject> delHTMLBySpuId(Integer spuId) {

        File file = new File(staticHTMLPath + File.separator + spuId + ".html");

        if(!file.delete()){
            return this.setResultError("文件删除失败");
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> createStaticHTMLTemplate(Integer spuId) {

        Context context = new Context();
        Map<String, Object> map = this.getGoodsInfo(spuId);
        context.setVariables(map);

        //创建文件 param1:文件路径 param2:文件名称
        File file = new File(staticHTMLPath, spuId + ".html");
        //构建文件输出流
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, "UTF-8");
            templateEngine.process("item",context,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally {
            writer.close();
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> initStaticHTMLTemplate() {
        //获取所有spu数据
        Result<List<SpuDTO>> spuInfoResult = goodsFeign.getSpuInfo(new SpuDTO());
        if (spuInfoResult.getCode() == 200) {
            List<SpuDTO> spuDTOList = spuInfoResult.getData();
            spuDTOList.stream().forEach(spuDTO -> {
                this.createStaticHTMLTemplate(spuDTO.getId());
            });
        }

        return this.setResultSuccess();
    }

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
                BrandEntity brandInfo = this.getBrandInfo(spu.getBrandId());
                map.put("brandInfo",brandInfo);

                //spuDetail
                SpuDetailEntity spuDetailInfo = this.getSpuDetailInfo(spuId);
                map.put("spuDetailInfo",spuDetailInfo);

                //分类信息
                List<CategoryEntity> cateList = this.getCateList(spu);
                map.put("cateList",cateList);

                //skus
                List<SkuDTO> skuList = this.getSkuList(spuId);
                map.put("skus",skuList);

                //specParam 特有规格参数
                HashMap<Integer, String> specMap = this.getSpecParamMap(spu);
                map.put("specParamMap",specMap);

                //通用规格参数,规格组
                List<SpecGroupDto> groupParam = this.getGroupParam(spu);
                map.put("groupsInParams",groupParam);
            }
        }
        return map;

    }
    private BrandEntity getBrandInfo(Integer brandId){

        //品牌信息 spu.getBrandId()
        BrandDto brandDto = new BrandDto();
        brandDto.setId(brandId);
        Result<PageInfo<BrandEntity>> brandInfoResult = brandFeign.getBrandInfo(brandDto);
        if(brandInfoResult.getCode() == 200){

            List<BrandEntity> brandList = brandInfoResult.getData().getList();
            if(brandList.size() == 1){
                return brandList.get(0);
            }
        }
        return null;
    }

    private SpuDetailEntity getSpuDetailInfo(Integer spuId){

        Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBySpuId(spuId);
        if(spuDetailResult.getCode() == 200){
            return spuDetailResult.getData();
        }
        return null;
    }

    private List<CategoryEntity> getCateList(SpuDTO spu){
        Result<List<CategoryEntity>> categoryListResult = categoryFeign.getCategoryByIdList(String.join(","
                , Arrays.asList(spu.getCid1() + "", spu.getCid2() + "", spu.getCid3() + "")));
        if(categoryListResult.getCode() == 200){

            return  categoryListResult.getData();
        }
        return null;
    }

    private List<SkuDTO> getSkuList(Integer spuId){
        Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);
        if(skuResult.getCode() == 200){
            return  skuResult.getData();
        }
        return null;
    }

    private HashMap<Integer, String> getSpecParamMap(SpuDTO spu){

        SpecParamDto specParamDto = new SpecParamDto();
        specParamDto.setCid(spu.getCid3());
        specParamDto.setGeneric(false);
        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDto);
        if(specParamResult.getCode() == 200){
            HashMap<Integer, String> specMap = new HashMap<>();
            specParamResult.getData().stream().forEach(spec ->{
                specMap.put(spec.getId(),spec.getName());
            });

            return specMap;
        }
        return null;
    }

    private List<SpecGroupDto> getGroupParam(SpuDTO spu){
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

            return groupsInParams;
        }
        return null;
    }
}
