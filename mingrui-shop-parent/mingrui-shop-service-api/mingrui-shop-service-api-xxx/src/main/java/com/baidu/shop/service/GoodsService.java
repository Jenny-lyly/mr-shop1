package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.entity.SpuEntity;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName GoodsService
 * @Description: GoodsService
 * @Author jinluying
 * @create: 2020-09-07 14:07
 * @Version V1.0
 **/
@Api("商品接口")
public interface GoodsService {

    @ApiOperation("获取spu信息")
    @GetMapping("goods/getSpuInfo")
    public Result<PageInfo<SpuEntity>> getSpuInfo(SpuDTO spuDTO);

    @ApiOperation("新增商品信息")
    @PostMapping("goods/saveInfo")
    public Result<JSONObject> saveGoodsInfo(@RequestBody  SpuDTO spuDTO);

    @ApiOperation(value = "获取spudetail详情信息")
    @GetMapping(value = "goods/getSpuDetailBySpuId")
    Result<SpuDetailEntity> getSpuDetailBySpuId(Integer spuId);

    @ApiOperation(value = "获取sku详情信息")
    @GetMapping(value = "goods/selectBySkuAndStock")
    Result<List<SkuDTO>> selectBySkuAndStock(Integer spuId);

    @ApiOperation("删除商品信息")
    @DeleteMapping("goods/delGoodsInfo")
    Result<JSONObject> delGoodsInfo(Integer spuId);

    @ApiOperation("修改商品信息")
    @PutMapping("goods/saveInfo")
    Result<JSONObject> editGoodsInfo(@RequestBody  SpuDTO spuDTO);

//    @ApiOperation("修改商品信息")
//    @PutMapping("goods/isSaleable")
//    Result<JSONObject> isSaleable(@RequestBody  SpuDTO spuDTO);
}
