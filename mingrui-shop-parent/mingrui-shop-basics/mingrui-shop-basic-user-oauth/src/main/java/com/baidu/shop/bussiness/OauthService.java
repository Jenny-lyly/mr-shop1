package com.baidu.shop.bussiness;

import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.entity.UserEntity;

/**
 * @ClassName UserService
 * @Description: UserService
 * @Author jinluying
 * @create: 2020-10-15 19:24
 * @Version V1.0
 **/
public interface OauthService {
    String login(UserEntity userEntity, JwtConfig jwtConfig);
}
