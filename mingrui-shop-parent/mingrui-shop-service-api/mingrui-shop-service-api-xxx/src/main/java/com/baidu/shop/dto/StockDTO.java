package com.baidu.shop.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName StockDTO
 * @Description: StockDTO
 * @Author jinluying
 * @create: 2020-09-08 19:19
 * @Version V1.0
 **/
@ApiModel("库存数据传输类")
@Data
public class StockDTO {

    @ApiModelProperty(value = "sku主键",example = "1")
    private Long skuId;

    @ApiModelProperty(value = "可秒杀库存",example = "1")
    private Integer seckillStock;

    @ApiModelProperty(value = "秒杀总数",example = "1")
    private Integer seckillTotal;

    @ApiModelProperty(value = "库存数量",example = "1")
    private Integer stock;
}
