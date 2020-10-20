package com.baidu.shop.feign;

import com.baidu.shop.service.SpecificationService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName SpecificationFeign
 * @Description: SpecificationFeign
 * @Author jinluying
 * @create: 2020-09-17 14:46
 * @Version V1.0
 **/
@FeignClient(contextId = "SpecificationService",value = "xxx-service")
public interface SpecificationFeign extends SpecificationService {
}
