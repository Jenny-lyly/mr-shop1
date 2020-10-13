package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName UserEntity
 * @Description: UserEntity
 * @Author jinluying
 * @create: 2020-10-13 14:13
 * @Version V1.0
 **/
@Table(name = "tb_user")
@Data
public class UserEntity {

    private Integer id;

    private String username;

    private String password;

    private String phone;

    private Date created;

    private String salt;

}
