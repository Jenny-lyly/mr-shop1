package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName SpecGroupEntity
 * @Description: SpecGroupEntity
 * @Author jinluying
 * @create: 2020-09-03 11:41
 * @Version V1.0
 **/
@Data
@Table(name="tb_spec_group")
public class SpecGroupEntity {

    @Id
    private Integer id;

    private Integer cid;

    private String name;
}
