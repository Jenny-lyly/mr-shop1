package com.baidu.shop.mapper;

import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.entity.SkuEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName SkuMapper
 * @Description: SkuMapper
 * @Author jinluying
 * @create: 2020-09-08 19:44
 * @Version V1.0
 **/
@org.apache.ibatis.annotations.Mapper
public interface SkuMapper extends Mapper<SkuEntity> {

    @Select(value = "select k.*,stock from tb_sku k , tb_stock t where k.id = t.sku_id and k.spu_id=#{spuId}")
    List<SkuDTO> selectBySkuAndStock(Integer spuId);
}
