package com.baidu.shop.utils;

/**
 * @ClassName StringUtil
 * @Description: StringUtil
 * @Author jinluying
 * @create: 2020-08-31 19:36
 * @Version V1.0
 **/
public class StringUtil {

    public static Boolean isEmpty(String str){

        return null == str &&  "".equals(str);
    }

    public static Boolean isNotEmpty(String str){

        return null != str || "".equals(str);
    }
}
