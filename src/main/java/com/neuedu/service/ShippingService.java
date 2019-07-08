package com.neuedu.service;

import com.neuedu.common.HigherResponse;
import com.neuedu.pojo.Shipping;

public interface ShippingService {

    HigherResponse addShipping(Shipping shipping);

    HigherResponse<Shipping> listShipping(Integer pageNum,Integer pageSize,Integer userId);

}
