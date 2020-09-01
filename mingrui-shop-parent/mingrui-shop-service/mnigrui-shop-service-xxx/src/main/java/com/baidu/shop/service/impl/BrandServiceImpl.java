package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDto;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName BrandServiceImpl
 * @Description: BrandServiceImpl
 * @Author jinluying
 * @create: 2020-08-31 14:51
 * @Version V1.0
 **/
@RestController
@Slf4j
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Resource
    private BrandMapper mapper;
    
    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDto brandDto) {

        //分页
        PageHelper.startPage(brandDto.getPage(),brandDto.getRows());


        Example example = new Example(BrandEntity.class);

        if(StringUtil.isNotEmpty(brandDto.getSort())){
            example.setOrderByClause(brandDto.getOrderByClause());
        }

        if(StringUtil.isNotEmpty(brandDto.getName())){
            example.createCriteria().andLike("name","%"+brandDto.getName()+"%");
        }

        List<BrandEntity> list = mapper.selectByExample(example);

        PageInfo<BrandEntity> pageInfo = new PageInfo<BrandEntity>(list);
        return this.setResultSuccess(pageInfo);
    }

    @Override
    public Result<JsonObject> saveBrandInfo(BrandDto brandDto) {
        BrandEntity entity = BaiduBeanUtil.copyProperties(brandDto, BrandEntity.class);
        mapper.insertSelective(entity);
        return this.setResultSuccess();
    }


}
