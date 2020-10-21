package com.baidu.shop.business.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.business.OrderService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.MrShopConstant;
import com.baidu.shop.dto.Car;
import com.baidu.shop.dto.OrderDTO;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.OrderDetailEntity;
import com.baidu.shop.entity.OrderEntity;
import com.baidu.shop.entity.OrderStatusEntity;
import com.baidu.shop.mapper.OrderDetailMapper;
import com.baidu.shop.mapper.OrderMapper;
import com.baidu.shop.mapper.OrderStatusMapper;
import com.baidu.shop.repository.RedisRepository;
import com.baidu.shop.utils.IdWorker;
import com.baidu.shop.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BusinessImpl
 * @Description: BusinessImpl
 * @Author jinluying
 * @create: 2020-10-21 14:26
 * @Version V1.0
 **/
@RestController
public class OrderServiceImpl extends BaseApiService implements OrderService {

    @Resource
    private OrderStatusMapper orderStatusMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private JwtConfig jwtConfig;
    @Autowired
    private IdWorker idWorker;
    @Resource
    private RedisRepository redisRepository;
    @Override
    public Result<String> createOrder(OrderDTO orderDTO,String token) {
        //detail,entity,statue
        long orderId = idWorker.nextId();

        try {
            Date date = new Date();
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            OrderEntity orderEntity = new OrderEntity();

            orderEntity.setOrderId(orderId);
            orderEntity.setUserId(userInfo.getId()+"");
            orderEntity.setCreateTime(date);
            orderEntity.setInvoiceType(1);
            orderEntity.setBuyerMessage("尽快发货");
            orderEntity.setBuyerNick(userInfo.getUsername());
            orderEntity.setBuyerRate(1);//是否评论
            orderEntity.setSourceType(1);//写死的PC端,如果项目健全了以后,这个值应该是常量
            orderEntity.setPaymentType(orderDTO.getPayType());

            List<Long> longs = Arrays.asList(0L);
            List<OrderDetailEntity> orderDetailList = Arrays.asList(orderDTO.getSkuIds().split(",")).stream().map(skuIdStr -> {
                Car car = redisRepository.getHash(MrShopConstant.USER_GOODS_CAR_PRE + userInfo.getId(), skuIdStr, Car.class);
                if (car == null) throw new RuntimeException("数据异常");
                OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
                orderDetailEntity.setImage(car.getImage());
                orderDetailEntity.setOrderId(orderId);
                orderDetailEntity.setNum(car.getNum());
                orderDetailEntity.setSkuId(Long.valueOf(skuIdStr));
                orderDetailEntity.setTitle(car.getTitle());
                orderDetailEntity.setOwnSpec(car.getOwnSpec());
                orderDetailEntity.setPrice(car.getPrice());
                longs.set(0, car.getPrice() * car.getNum() + longs.get(0));
                return orderDetailEntity;
            }).collect(Collectors.toList());

            orderEntity.setTotalPay(longs.get(0));
            orderEntity.setActualPay(longs.get(0));

            OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
            orderStatusEntity.setCreateTime(date);
            orderStatusEntity.setOrderId(orderId);
            orderStatusEntity.setStatus(1);//已创建订单,未支付

            orderMapper.insertSelective(orderEntity);
            orderDetailMapper.insertList(orderDetailList);
            orderStatusMapper.insertSelective(orderStatusEntity);

            //mysql和redis双写一致性问题?????
            //通过用户id和skuid删除购物车中的数据
            Arrays.asList(orderDTO.getSkuIds().split(",")).stream().forEach(skuidStr -> {
                redisRepository.delHash(MrShopConstant.USER_GOODS_CAR_PRE + userInfo.getId(),skuidStr);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("=====");
        return this.setResultSuccess();
    }
}
