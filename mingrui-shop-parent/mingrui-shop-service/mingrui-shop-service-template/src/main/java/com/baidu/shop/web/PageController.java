package com.baidu.shop.web;

import com.baidu.shop.service.PageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @ClassName PageController
 * @Description: PageController
 * @Author jinluying
 * @create: 2020-09-23 19:15
 * @Version V1.0
 **/
@Controller
@RequestMapping("item")
public class PageController {
    @Resource
    private PageService pageService;

    @GetMapping("{spuId}.html")
    public String test(@PathVariable  Integer spuId, ModelMap modelMap){
        Map<String,Object> map = pageService.getGoodsInfo(spuId);
        modelMap.putAll(map);

        return "item";
    }
}
