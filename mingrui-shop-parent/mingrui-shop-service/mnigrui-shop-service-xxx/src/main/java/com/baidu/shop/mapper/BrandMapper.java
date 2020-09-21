package com.baidu.shop.mapper;

import com.baidu.shop.entity.BrandEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName BrandMapper
 * @Description: BrandMapper
 * @Author jinluying
 * @create: 2020-08-31 14:50
 * @Version V1.0
 **/
@org.apache.ibatis.annotations.Mapper
public interface BrandMapper  extends Mapper<BrandEntity> , SelectByIdListMapper<BrandEntity,Integer> {

    @Select(value = "select * from tb_brand b where b.id in " +
            "(select cb.brand_id from tb_category_brand cb where cb.category_id = #{cid})")
    List<BrandEntity> getBrandByCate(Integer cid);
}
