package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName OrderStatusEntity
 * @Description: OrderStatusEntity
 * @Author jinluying
 * @create: 2020-10-21 14:12
 * @Version V1.0
 **/
@Table(name = "tb_order_status")
@Data
public class OrderStatusEntity {

    @Id
    private Long orderId;

    private Integer status;

    private Date createTime;//创建时间

    private Date paymentTime;//支付时间
}
