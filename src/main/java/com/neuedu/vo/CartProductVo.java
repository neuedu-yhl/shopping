package com.neuedu.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车里产品的VO
 */
@Data
public class CartProductVo {

    //id
    private Integer id;
    //商品id
    private Integer proId;
    //用户id
    private Integer userId;
    //主图
    private String mainImage;
    //副标题
    private String proSubtitle;
    //单价
    private Double price;
    //数量
    private Integer quality;
    //当前商品的总价
    private BigDecimal totalPrice;
    //是否选中
    private Integer isChecked;
    //商品名
    private String proName;
    //商品状态
    private Integer status;
    //商品库存
    private Integer stock;
    //限制提示
    private String limitQuality;




























}
