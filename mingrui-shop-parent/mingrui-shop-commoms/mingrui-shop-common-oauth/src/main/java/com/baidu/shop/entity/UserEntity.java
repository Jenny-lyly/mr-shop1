package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @ClassName UserEntity
 * @Description: UserEntity
 * @Author jinluying
 * @create: 2020-10-15 17:50
 * @Version V1.0
 **/
@Table(name = "tb_user")
@Data
public class UserEntity {

    @Id
    private Integer id;

//    @NotNull(message = "账户不能为空")
    private String username;

//    @NotNull(message = "密码不能为空")
    private String password;

//    @NotNull(message = "手机号不能为空")
    private String phone;

    private Date created;

    private String salt;
}
