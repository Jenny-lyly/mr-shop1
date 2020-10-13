package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.validate.group.MrOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @ClassName UserService
 * @Description: UserService
 * @Author jinluying
 * @create: 2020-10-13 14:28
 * @Version V1.0
 **/
@Api("用户接口")
public interface UserService {

    @ApiOperation(value = "用户注册")
    @PostMapping("user/register")
    Result<JSONObject> register(@Validated({MrOperation.Add.class}) @RequestBody UserDTO userDTO);

    @ApiOperation(value = "用户注册")
    @GetMapping("user/check/{value}/{type}")
    Result<List<UserEntity>> checkUsernameOrPhonenumber(@PathVariable(value = "value") String value,
                                                        @PathVariable(value = "type") Integer type);

    @ApiOperation(value = "给手机号发送验证码")
    @PostMapping("user/sendValidCode")
    Result<JSONObject> sendValidCode(@RequestBody  UserDTO userDTO);
}
