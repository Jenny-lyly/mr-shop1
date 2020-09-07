package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuEntity;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

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
}
