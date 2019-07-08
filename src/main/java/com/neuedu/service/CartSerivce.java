package com.neuedu.service;

import com.neuedu.common.HigherResponse;
import com.neuedu.pojo.Cart;
import com.neuedu.vo.CartVo;

public interface CartSerivce {


    HigherResponse<CartVo> getCartList(Integer userId);


    HigherResponse<CartVo> addCart(Integer userId,Integer proId,Integer count);


    HigherResponse<CartVo> selectPro(Integer userId,Integer proId);

}
