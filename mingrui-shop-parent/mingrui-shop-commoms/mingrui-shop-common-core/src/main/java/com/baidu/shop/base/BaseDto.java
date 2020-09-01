package com.baidu.shop.base;

import com.baidu.shop.utils.StringUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName BaseDao
 * @Description: BaseDao
 * @Author jinluying
 * @create: 2020-08-31 19:31
 * @Version V1.0
 **/
@Data
@ApiModel("BaseDTO用于数据传输,其他dto需要继承此类")
public class BaseDto {

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer page;

    @ApiModelProperty(value = "每页条数", example = "5")
    private Integer rows;

    @ApiModelProperty(value = "排序字段")
    private String sort;

    @ApiModelProperty(value = "是否降序")
    private Boolean order;

    @ApiModelProperty(hidden = true)
    public String getOrderByClause(){

        if(StringUtil.isNotEmpty(sort)) return sort+" "+(order?"desc":"");
        return "";
    }
}
