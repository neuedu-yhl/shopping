package com.neuedu.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车Vo
 */
@Data
public class CartVo {

    private List<CartProductVo> cpvs;

    private BigDecimal totalPrice;

    private boolean allChecked;

    private String imgHost;


}