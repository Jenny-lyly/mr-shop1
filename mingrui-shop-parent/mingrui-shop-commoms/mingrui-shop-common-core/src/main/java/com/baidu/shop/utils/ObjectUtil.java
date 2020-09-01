package com.baidu.shop.utils;

/**
 * @ClassName ObjectUtil
 * @Description: 对象工具类
 * @Author jinluying
 * @create: 2020-08-28 14:30
 * @Version V1.0
 **/
public class ObjectUtil {

    public static Boolean isNull(Object obj){

        return null == obj;
    }

    public static Boolean isNotNull(Object obj){

        return null != obj;
    }
}
