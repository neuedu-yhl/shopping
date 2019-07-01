package com.neuedu.vo;

import com.oracle.webservices.internal.api.databinding.DatabindingMode;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class PrpductDtailVO {

    private Integer id;

    private Integer categoryId;

    private Integer parentCategoryId;

    private String name;

    private String subTitle;

    private String imageHost;

    private String mainImage;

    private String subImages;

    private String detail;

    private BigDecimal price;

    private Integer stock;

    private Byte status;

    private String createTime;

    private String updateTime;

}
