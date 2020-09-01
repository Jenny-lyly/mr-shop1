package com.baidu.shop.entity;

import com.baidu.shop.validate.group.MrOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName BrandEntity
 * @Description: BrandEntity
 * @Author jinluying
 * @create: 2020-08-31 14:39
 * @Version V1.0
 **/

@Table(name = "tb_brand")
@Data
public class BrandEntity {

    @Id
    private Integer id;

    private String name;

    private String image;

    private Character letter;

}
