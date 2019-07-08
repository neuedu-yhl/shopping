package com.neuedu.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderVo {


    private Long orderNo;

    private BigDecimal allPrice;

    private Integer paymentType;

    private String paymentTypeDesc;

    private Integer postAge;

    private Integer status;

    private String statusDesc;

    private Date paymentTime;

    private Date sendTime;

    private Date endTime;

    private Date closeTime;

    private Date createTime;

    private List<OrderItemVo> orderItemVoList;

    private String imageHost;

    private ShippingVo shippingVo;















}
