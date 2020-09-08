package com.baidu.shop.mapper;

import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName SpuMapper
 * @Description: SpuMapper
 * @Author jinluying
 * @create: 2020-09-07 14:17
 * @Version V1.0
 **/
@org.apache.ibatis.annotations.Mapper
public interface SpuMapper extends Mapper<SpuEntity> {

    @Select(value = "SELECT\n" +
            "\t\ts.*, b.`name` brand_name,\n" +
            "\t\tconcat_ws(\n" +
            "\t\t\t'/',\n" +
            "\t\t\tMAX( CASE s.cid1 WHEN y.id THEN y.NAME ELSE 0 END ),\n" +
            "\t\t\tMAX( CASE s.cid2 WHEN y.id THEN y.NAME ELSE 0 END ),\n" +
            "\t\t\tMAX( CASE s.cid3 WHEN y.id THEN y.NAME ELSE 0 END ) \n" +
            "\t\t) category_name \n" +
            "\tFROM tb_category y, tb_spu s,tb_brand b \n" +
            "\tWHERE  b.id = s.brand_id \n" +
            "\tGROUP BY s.id \n" +
            "\torder by #{sort} \n" +
            "\tlimit #{page},#{rows} ")
    List<SpuEntity> getSpuInfo(String sort, Integer page, Integer rows);
}
