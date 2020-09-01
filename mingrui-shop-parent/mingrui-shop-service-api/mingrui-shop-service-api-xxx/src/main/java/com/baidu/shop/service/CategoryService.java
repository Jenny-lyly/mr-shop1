package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.validate.group.MrOperation;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.scripts.JS;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商品分类接口")
public interface CategoryService {

    @ApiOperation(value="通过商品查询分类 接口")
    @GetMapping(value="category/list")
    Result<List<CategoryEntity>> getCategoryByPid(Integer pid);

    @ApiOperation(value = "通过id删除分类")
    @DeleteMapping("category/del")
    public Result<JsonObject> delCategory(Integer id);

    @PutMapping("category/edit")
    @ApiOperation(value = "修改分类")
    //声明哪个组下面的参数参加校验-->当前是校验Update组
    public Result<JSONObject> editCategory(@Validated({MrOperation.Update.class}) @RequestBody CategoryEntity entity);

    @ApiOperation(value = "新增分类")
    @PostMapping(value = "category/add")
    //声明哪个组下面的参数参加校验-->当前是校验Add组
    public Result<JSONObject> addCategory(@Validated({MrOperation.Add.class}) @RequestBody CategoryEntity entity);
}
