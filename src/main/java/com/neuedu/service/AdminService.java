package com.neuedu.service;

import com.neuedu.common.HigherResponse;
import com.neuedu.pojo.Product;
import com.neuedu.vo.ProductListVO;
import com.neuedu.vo.PrpductDtailVO;
import sun.security.ssl.HandshakeInStream;

import javax.servlet.http.HttpSession;

public interface AdminService {

    //登录
    HigherResponse login(String username, String psw, HttpSession session);


    //添加或者修改商品业务
    HigherResponse addOrUpdatePro(Product product);

    //产品上下架
    HigherResponse updateProStatus(Integer proId,Integer status);

    //查看产品详情
    HigherResponse<PrpductDtailVO> detailDo(Integer proId);


    //查询产品列表
    HigherResponse listDo(Integer pageNum,Integer pageSize);

    //根据商品名和商品id分页查询商品
    HigherResponse<ProductListVO> queryProByNameOrId(String proName,Integer pId,Integer pageNum,Integer pageSize);

}