package com.baidu.shop.service;

import java.util.Map;

/**
 * @ClassName PageService
 * @Description: PageService
 * @Author jinluying
 * @create: 2020-09-23 19:41
 * @Version V1.0
 **/
public interface PageService {
    Map<String, Object> getGoodsInfo(Integer spuId);
}
