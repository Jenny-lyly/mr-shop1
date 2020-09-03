package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDto;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.service.SpecificationService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName SpecificationServiceImpl
 * @Description: SpecificationServiceImpl
 * @Author jinluying
 * @create: 2020-09-03 11:54
 * @Version V1.0
 **/
@RestController
public class SpecificationServiceImpl extends BaseApiService implements SpecificationService {

    @Resource
    private SpecGroupMapper specGroupMapper;
    @Override
    public Result<List<SpecGroupEntity>> getSpecInfo(SpecGroupDto specGroupDto) {

        Example example = new Example(SpecGroupEntity.class);
        if(ObjectUtil.isNotNull(specGroupDto.getCid())) example.createCriteria().andEqualTo("cid",specGroupDto.getCid());
        List<SpecGroupEntity> list = specGroupMapper.selectByExample(example);
        return this.setResultSuccess(list);
    }

    @Override
    @Transactional
    public Result<JsonObject> save(SpecGroupDto specGroupDto) {

        specGroupMapper.insertSelective(BaiduBeanUtil.copyProperties(specGroupDto,SpecGroupEntity.class));
        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JsonObject> edit(SpecGroupDto specGroupDto) {

        specGroupMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specGroupDto,SpecGroupEntity.class));
        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JsonObject> delete(Integer id) {

        specGroupMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }
}
