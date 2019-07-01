package com.neuedu.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductListVO {

    private Integer id;

    private Integer categoryId;

    private String name;

    private String subTitle;

    private  String mainImage;

    private Byte status;

    private BigDecimal price;

}