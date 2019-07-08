package com.neuedu.controller;

import com.neuedu.common.Const;
import com.neuedu.common.HigherResponse;
import com.neuedu.pojo.User;
import com.neuedu.service.OrderService;
import com.neuedu.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/portal/order")
public class OrderCon {

    @Autowired
    private OrderService os;

    @RequestMapping("/create.do")
    public HigherResponse createOrderDo(HttpSession session,Integer shippingId)
    {
        User user = (User)session.getAttribute(Const.CURRENTUSER);
        if(null == user)
        {
            return HigherResponse.getResponseFailed("您还未登录");
        }
        if(null == shippingId)
        {
            return HigherResponse.getResponseFailed("没有收货人信息,请添加。。。");
        }
        return os.createOrder(user.getId(),shippingId);
    }

    //取消订单
    @RequestMapping("/cancel.do")
    public HigherResponse cancelDo(HttpSession session,long orderNo)
    {
        User user = (User)session.getAttribute(Const.CURRENTUSER);
        if(null == user)
        {
            return HigherResponse.getResponseFailed("您还未登录");
        }
        return os.cancelOrder(orderNo);
    }


    //查询订单明细
    @RequestMapping("/orderDetail.do")
    public HigherResponse<OrderVo> getOrderDetail(HttpSession session,long orderNo)
    {
        User user = (User)session.getAttribute(Const.CURRENTUSER);
        if(null == user)
        {
            return HigherResponse.getResponseFailed("您还未登录");
        }
        return os.getOrderDetail(orderNo);
    }


    //支付接口
    @RequestMapping("/pay.do")
    public HigherResponse payDo(HttpSession session,Long orderNo)
    {
        User user = (User)session.getAttribute(Const.CURRENTUSER);
        if(null == user)
        {
            return HigherResponse.getResponseFailed("您还未登录");
        }
        if(null == orderNo)
        {
            return HigherResponse.getResponseFailed("您还没有产品订单...");
        }
        return os.payDo(user.getId(),orderNo);
    }


    //回调接口
    @RequestMapping("/callback.do")
    public HigherResponse helloNat()
    {
        System.out.println("回调接口");
        return HigherResponse.getResponseSuccess("成功");
    }
}