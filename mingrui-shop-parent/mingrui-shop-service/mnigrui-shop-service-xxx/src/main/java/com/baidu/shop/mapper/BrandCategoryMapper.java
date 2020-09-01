package com.baidu.shop.mapper;

import com.baidu.shop.entity.CategoryBrandEntity;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @ClassName BrandCategoryMapper
 * @Description: BrandCategoryMapper
 * @Author jinluying
 * @create: 2020-09-01 14:27
 * @Version V1.0
 **/
@org.apache.ibatis.annotations.Mapper
public interface BrandCategoryMapper extends Mapper<CategoryBrandEntity>, InsertListMapper<CategoryBrandEntity> {

}
