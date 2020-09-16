package com.baidu.feign;

import com.baidu.shop.service.GoodsService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName GoodsFeign
 * @Description: GoodsFeign
 * @Author jinluying
 * @create: 2020-09-16 19:09
 * @Version V1.0
 **/
@FeignClient(value = "xxx-service")
public interface GoodsFeign extends GoodsService {
}
