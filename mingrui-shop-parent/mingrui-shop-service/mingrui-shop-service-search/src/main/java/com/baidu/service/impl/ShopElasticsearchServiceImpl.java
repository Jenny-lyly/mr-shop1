package com.baidu.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.feign.GoodsFeign;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.service.ShopElasticsearchService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.JSONUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @Override
    public Result<JSONObject> esGoodsInfo() {

        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setRows(5);
        spuDTO.setPage(1);
        Result<List<SpuEntity>> spuInfo = goodsFeign.getSpuInfo(spuDTO);

        if(spuInfo.getCode() == HTTPStatus.OK){

            List<SpuEntity> spuList = spuInfo.getData();
            spuList.stream().forEach(spu ->{

                //通过spuId 查询 skuList
                Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spu.getId());
                if(skuResult.getCode() == HTTPStatus.OK){

                    List<SkuDTO> skuList = skuResult.getData();
                    String skuJsonStr = JSONUtil.toJsonString(skuList);
//                    System.out.println(skuJsonStr);
                    System.out.println(skuList);
                }
            });
        }
        return this.setResultSuccess();
    }
}
