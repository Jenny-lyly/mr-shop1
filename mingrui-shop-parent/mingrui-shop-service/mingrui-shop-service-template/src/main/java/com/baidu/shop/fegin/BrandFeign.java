package com.baidu.shop.fegin;

import com.baidu.shop.service.BrandService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName BrandFeign
 * @Description: BrandFeign
 * @Author jinluying
 * @create: 2020-09-21 14:25
 * @Version V1.0
 **/
@FeignClient(value = "xxx-service",contextId = "BrandService")
public interface BrandFeign extends BrandService {
}
