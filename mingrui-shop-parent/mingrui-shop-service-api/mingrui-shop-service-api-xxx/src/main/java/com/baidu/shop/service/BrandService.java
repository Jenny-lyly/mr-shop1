package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDto;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.validate.group.MrOperation;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName BrandService
 * @Description: BrandService
 * @Author jinluying
 * @create: 2020-08-31 14:45
 * @Version V1.0
 **/
@Api("品牌分类接口")
public interface BrandService {

    @ApiOperation("查询品牌信息")
    @GetMapping("brand/getBrandInfo")
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDto brandDTO);

    @ApiOperation("新增品牌信息")
    @PostMapping("brand/save")
    public Result<JsonObject> saveBrandInfo(@Validated({MrOperation.Add.class}) @RequestBody BrandDto brandDTO);

    @ApiOperation("修改品牌信息")
    @PutMapping("brand/save")
    public Result<JsonObject> updateBrandInfo(@Validated({MrOperation.Update.class}) @RequestBody BrandDto brandDTO);

    @ApiOperation("删除品牌信息")
    @DeleteMapping("brand/deleteBrand")
    public Result<JsonObject> deleteBrandInfo(Integer id);

    @ApiOperation("通过分类id查询品牌信息")
    @GetMapping("brand/getBrandByCate")
    public Result<List<BrandEntity>> getBrandByCate(Integer cid);
}
