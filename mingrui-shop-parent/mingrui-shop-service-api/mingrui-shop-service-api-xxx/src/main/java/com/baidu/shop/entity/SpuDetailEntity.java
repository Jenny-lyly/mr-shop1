package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName SpuDetailEntity
 * @Description: SpuDetailEntity
 * @Author jinluying
 * @create: 2020-09-08 19:04
 * @Version V1.0
 **/
@Table(name = "tb_spu_detail")
@Data
public class SpuDetailEntity {

    @Id
    private Integer spuId;

    private String description;

    private String genericSpec;

    private String specialSpec;

    private String packingList;

    private String afterService;
}
