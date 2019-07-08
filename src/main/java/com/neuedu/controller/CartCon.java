package com.neuedu.controller;

import com.neuedu.common.Const;
import com.neuedu.common.HigherResponse;
import com.neuedu.pojo.User;
import com.neuedu.service.CartSerivce;
import com.neuedu.service.CategoryService;
import com.neuedu.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user/cart")
public class CartCon {

    @Autowired
    private CartSerivce cs;


    @RequestMapping("/list.do")
    public HigherResponse<CartVo> listDo(HttpSession session)
    {
        User attribute = (User)session.getAttribute(Const.CURRENTUSER);
        if(null == attribute)
        {
            return HigherResponse.getResponseFailed("请登录.");
        }else
        {
            return cs.getCartList(attribute.getId());
        }
    }


    //购物车添加商品
    @RequestMapping("/add.do")
    public HigherResponse addDo(HttpSession session,Integer proId,@RequestParam(defaultValue = "1") Integer count)
    {
        User attribute = (User)session.getAttribute(Const.CURRENTUSER);
        if(null == attribute)
        {
            return HigherResponse.getResponseFailed("请登录.");
        }
        return cs.addCart(attribute.getId(),proId,count);
    }


    //购物车选中某个商品
    @RequestMapping("/select.do")
    public HigherResponse<CartVo> selectDo(HttpSession session,Integer proId)
    {
        User attribute = (User)session.getAttribute(Const.CURRENTUSER);
        if(null == attribute)
        {
            return HigherResponse.getResponseFailed("请登录.");
        }
        return cs.selectPro(attribute.getId(),proId);
    }
}
