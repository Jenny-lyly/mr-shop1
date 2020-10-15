package com.baidu.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName UserInfo
 * @Description: UserInfo
 * @Author jinluying
 * @create: 2020-10-15 11:41
 * @Version V1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private Integer id;

    private String username;
}
