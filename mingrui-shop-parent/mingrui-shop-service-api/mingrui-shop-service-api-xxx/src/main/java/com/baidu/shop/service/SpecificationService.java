package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDto;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.validate.group.MrOperation;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName SpecificationService
 * @Description: SpecificationService
 * @Author jinluying
 * @create: 2020-09-03 11:40
 * @Version V1.0
 **/
@Api(tags = "规格接口")
public interface SpecificationService {

    @GetMapping("specgroup/getSpecGroupInfo")
    @ApiOperation(value = "通过条件查询规格组")
    public Result<List<SpecGroupEntity>> getSpecInfo(SpecGroupDto specGroupDto);

    @PostMapping("specgroup/save")
    @ApiOperation(value = "新增规格组")
    public Result<JsonObject> save(@Validated({MrOperation.Add.class}) @RequestBody SpecGroupDto specGroupDto);

    @PutMapping("specgroup/save")
    @ApiOperation(value = "修改规格组")
    public Result<JsonObject> edit(@Validated({MrOperation.Update.class}) @RequestBody SpecGroupDto specGroupDto);

    @DeleteMapping("specgroup/delete")
    @ApiOperation(value = "修改规格组")
    public Result<JsonObject> delete(Integer id);
}
