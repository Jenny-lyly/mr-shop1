package com.baidu.shop.fegin;

import com.baidu.shop.service.CategoryService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName CategoryFeign
 * @Description: CategoryFeign
 * @Author jinluying
 * @create: 2020-09-21 14:25
 * @Version V1.0
 **/
@FeignClient(contextId = "CategoryService",value = "xxx-service")
public interface CategoryFeign extends CategoryService {
}
