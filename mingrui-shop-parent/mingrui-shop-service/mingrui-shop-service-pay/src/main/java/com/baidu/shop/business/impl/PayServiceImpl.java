package com.baidu.shop.business.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.business.PayService;
import com.baidu.shop.dto.PayInfoDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName PayServiceImpl
 * @Description: PayServiceImpl
 * @Author jinluying
 * @create: 2020-10-22 15:04
 * @Version V1.0
 **/
public class PayServiceImpl extends BaseApiService implements PayService {
    @Override
    public void requestPay(PayInfoDTO payInfoDTO, String token, HttpServletResponse response) {
        System.out.println("=====");
    }

    @Override
    public void notify(HttpServletRequest httpServletRequest) {

    }

    @Override
    public void returnHTML(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

    }
}
