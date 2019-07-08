package com.neuedu.service;

import com.neuedu.common.HigherResponse;
import com.neuedu.vo.OrderVo;

public interface OrderService {

    public HigherResponse createOrder(Integer userId,Integer shippingId);


    public HigherResponse cancelOrder(Long orderNo);


    public HigherResponse getOrderDetail(Long orderNo);


    public HigherResponse payDo(Integer userId,Long orderNo);


}
