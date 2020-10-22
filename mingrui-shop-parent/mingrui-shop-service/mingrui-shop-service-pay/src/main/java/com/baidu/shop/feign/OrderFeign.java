package com.baidu.shop.feign;

import com.alipay.api.domain.OrderExtInfo;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.OrderInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName OrderFeign
 * @Description: OrderFeign
 * @Author jinluying
 * @create: 2020-10-22 19:03
 * @Version V1.0
 **/
@FeignClient(contextId = "OrderService", value = "order-server")
public interface OrderFeign {

    @GetMapping(value = "order/getOrderInfoByOrderId")
    Result<OrderInfo> getOrderInfoByOrderId(@RequestParam Long orderId);
}
