package com.baidu.shop.mapper;

import com.baidu.shop.entity.StockEntity;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper; /**
 * @ClassName StockMapper
 * @Description: StockMapper
 * @Author jinluying
 * @create: 2020-09-08 19:45
 * @Version V1.0
 **/
@org.apache.ibatis.annotations.Mapper
public interface StockMapper extends Mapper<StockEntity> , DeleteByIdListMapper<StockEntity,Long> {
}
