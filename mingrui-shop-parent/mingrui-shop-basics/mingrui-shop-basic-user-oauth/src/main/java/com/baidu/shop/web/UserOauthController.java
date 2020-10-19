package com.baidu.shop.web;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.bussiness.OauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName UserOauthController
 * @Description: UserOauthController
 * @Author jinluying
 * @create: 2020-10-15 19:19
 * @Version V1.0
 **/
@RestController
@Api(tags = "用户认证接口")
public class UserOauthController extends BaseApiService {

    @Resource
    private OauthService oauthService;
    @Resource
    private JwtConfig jwtConfig;

    @PostMapping("oauth/login")
    @ApiOperation(value = "用户登录")
    public Result<JSONObject> login(@RequestBody UserEntity userEntity, HttpServletRequest request, HttpServletResponse response){

        String token = oauthService.login(userEntity,jwtConfig);

        //判断token是否为null
        //true:用户名或密码错误
        //将token放到cookie中
        if (ObjectUtil.isNull(token)) {
            return this.setResultError(HTTPStatus.USER_VALIDATE_ERROR,"用户名或密码错误");
        }
        CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),token,jwtConfig.getCookieMaxAge(),true);
        return this.setResultSuccess();
    }

    @GetMapping("oauth/verify")
    public Result<UserInfo> verify(@CookieValue(value = "MRSHOP_TOKEN") String token , HttpServletRequest request, HttpServletResponse response){

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
    //可以解析token的证明用户是正确登录状态,重新生成 token
            String newToken = JwtUtils.generateToken(new UserInfo(userInfo.getId(),userInfo.getUsername())
                    ,jwtConfig.getPrivateKey(),jwtConfig.getExpire());
            //将newToken写道cookie中
            CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),token,jwtConfig.getCookieMaxAge(),true);

            return this.setResultSuccess(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return this.setResultError(HTTPStatus.VERIFY_ERROR,"用户失效");
        }
    }
}
