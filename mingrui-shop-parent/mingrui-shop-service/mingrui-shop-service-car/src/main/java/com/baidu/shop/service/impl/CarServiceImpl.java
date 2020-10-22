package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSON;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.MrShopConstant;
import com.baidu.shop.dto.Car;
import com.baidu.shop.dto.SpecParamDto;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.CarService;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CarServiceImpl
 * @Description: CarServiceImpl
 * @Author jinluying
 * @create: 2020-10-19 20:00
 * @Version V1.0
 **/
@RestController
@Slf4j
public class CarServiceImpl extends BaseApiService implements CarService {

    @Autowired
    private RedisRepository redisRepository;
    @Resource
    private GoodsFeign goodsFeign;
    @Resource
    private JwtConfig jwtConfig;
//    @Resource
//    private SpecificationFeign specificationFeign;


    @Override
    public Result<List<Car>> getConcurrentUserCar(String token) {

        List<Car> carList = new ArrayList<>();
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Map<String, String> map = redisRepository.getHash(MrShopConstant.USER_GOODS_CAR_PRE + userInfo.getId());
            map.forEach((key,value) ->{
                carList.add(JSONUtil.toBean(value,Car.class));
            });
            return this.setResultSuccess(carList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultError("程序错误");
    }

    @Override
    public Result<JSONObject> mergeCar(String clientCarList, String token) {

        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(clientCarList);
        List<Car> carList = com.alibaba.fastjson.JSONObject.parseArray(jsonObject.get("clientCarList").toString(), Car.class);
        carList.stream().forEach(car ->{
            this.addCar(car,token);
        });
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> updateNum(Long skuId, Integer type, String token) {
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Car redisCar = redisRepository.getHash(MrShopConstant.USER_GOODS_CAR_PRE + userInfo.getId(),
                    skuId + "", Car.class);
            if(ObjectUtil.isNotNull(redisCar)){
                if(type == MrShopConstant.USER_CAR_NUM_ADD){
                    redisCar.setNum(redisCar.getNum()+1);
                }else if(type == MrShopConstant.USER_CAR_NUM_DEL){
                    redisCar.setNum(redisCar.getNum()-1);
                }
                redisRepository.setHash(MrShopConstant.USER_GOODS_CAR_PRE+userInfo.getId(),
                        redisCar.getSkuId()+"", JSONUtil.toJsonString(redisCar));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> addCar(Car car,String token) {

        Result<SkuEntity> skuResult = goodsFeign.getSkuBySkuId(car.getSkuId());
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Car redisCar = redisRepository.getHash(MrShopConstant.USER_GOODS_CAR_PRE + userInfo.getId(), car.getSkuId() + "", Car.class);

            log.debug("通过 key :{},skuid:{} 获取到的数据是:{} ",MrShopConstant.USER_GOODS_CAR_PRE+userInfo.getId(),car.getSkuId(),redisCar);

            Car saveCar = null;

            if (ObjectUtil.isNotNull(redisCar)) {//原来的用户购物车中有当前要添加到购物车中的商品

                redisCar.setNum(car.getNum() + redisCar.getNum());
                saveCar = redisCar;
                log.debug("当前用户购物车中有将要新增的商品，重新设置num : {}" , redisCar.getNum());
             }else{//当前用户购物车中没有将要新增的商品信息
                if(skuResult.getCode() == 200){


                    SkuEntity skuEntity = skuResult.getData();
                    car.setTitle(skuEntity.getTitle());
                    car.setPrice(Long.valueOf(skuEntity.getPrice()));

                    car.setImage(StringUtil.isNotEmpty(skuEntity.getImages()) ?
                            skuEntity.getImages().split(",")[0] :"");
                    car.setUserId(userInfo.getId());
                    //key:id
                    //value: 规格参数值
                    //遍历map
                    //feign调用通过paramId查询info的接口
                    //重新组装map
                    //将map转为json字符串
//                    Map<String, Object> ownSpecMap = JSONUtil.toMap(skuEntity.getOwnSpec());
//                    ownSpecMap.forEach((id,spec) ->{
//                        SpecParamDto specParamDto = new SpecParamDto();
//                        specParamDto.setId(Integer.parseInt(id));
//                        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDto);
//                        List<SpecParamEntity> specList = specParamResult.getData();
//                        if(specList.size() == 1){
//                            id = specList.get(0).getName();
//                        }
//                    });
//                    car.setOwnSpec(JSONUtil.toJsonString(ownSpecMap));
                    car.setOwnSpec(skuEntity.getOwnSpec());
                    saveCar = car;

                    log.debug("新增商品到购物车redis,KEY:{} ,skuid:{},car:{}"
                            ,MrShopConstant.USER_GOODS_CAR_PRE+userInfo.getId(),car.getSkuId(), JSONUtil.toJsonString(car));
                }
            }
            redisRepository.setHash(MrShopConstant.USER_GOODS_CAR_PRE+userInfo.getId(),
                    car.getSkuId()+"", JSONUtil.toJsonString(saveCar));
            log.debug("新增到redis数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("=========");
        return this.setResultSuccess();
    }
}
