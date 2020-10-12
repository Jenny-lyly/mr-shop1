package com.mr.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName TemplateService
 * @Description: TemplateService
 * @Author jinluying
 * @create: 2020-09-25 19:03
 * @Version V1.0
 **/
public interface TemplateService {

    @GetMapping(value = "template/createStaticHTMLTemplate")
    Result<JSONObject> createStaticHTMLTemplate(Integer spuId);

    @GetMapping(value = "template/initStaticHTMLTemplate")
    Result<JSONObject> initStaticHTMLTemplate();
}
