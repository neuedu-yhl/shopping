package com.neuedu.controller;


import com.neuedu.common.Const;
import com.neuedu.common.HigherResponse;
import com.neuedu.pojo.Shipping;
import com.neuedu.pojo.User;
import com.neuedu.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/portal/shipping")
public class ShippingCon {

    @Autowired
    private ShippingService ss;

    @RequestMapping("/add.do")
    public HigherResponse addDo(HttpSession session, Shipping shipping)
    {
        User attribute = (User)session.getAttribute(Const.CURRENTUSER);
        if(null == attribute)
        {
            return HigherResponse.getResponseFailed("请登录.");
        }
        shipping.setUserId(attribute.getId());
        //执行添加操作
        return ss.addShipping(shipping);
    }

    @RequestMapping("/list.do")
    //地址列表
    public HigherResponse<Shipping> listDo(HttpSession session, @RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "2")Integer pageSize)
    {
        User attribute = (User)session.getAttribute(Const.CURRENTUSER);
        if(null == attribute)
        {
            return HigherResponse.getResponseFailed("请登录.");
        }
        return  ss.listShipping(pageNum,pageSize,attribute.getId());
    }
}