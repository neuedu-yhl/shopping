package com.neuedu.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neuedu.common.HigherResponse;
import com.neuedu.dao.ShippingMapper;
import com.neuedu.pojo.Shipping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private ShippingMapper shippingMapper;
    @Override
    public HigherResponse addShipping(Shipping shipping) {
        int i = shippingMapper.insertSelective(shipping);
        if(i != 0)
        {
            return HigherResponse.getResponseSuccess("添加成功",shipping.getId());
        }
        return HigherResponse.getResponseFailed("添加失败。。");
    }

    @Override
    public HigherResponse<Shipping> listShipping(Integer pageNum, Integer pageSize,Integer userId) {
        PageHelper.startPage(pageNum,pageSize);
        //查询地址
        List<Shipping> allShipping = shippingMapper.getAllShipping(userId);
        if(null == allShipping)
        {
            return HigherResponse.getResponseFailed("您还没有地址信息,请添加...");
        }
        PageInfo pageInfo = new PageInfo<>(allShipping);
        return HigherResponse.getResponseSuccess("获取成功",pageInfo);
    }
}
