package com.neuedu.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 运算类
 */
public class BigDecimalUtils {

    //定义加的方法
    public static BigDecimal plus(double val1,double val2)
    {
        BigDecimal add = new BigDecimal(Double.toString(val1)).add(new BigDecimal(Double.toString(val2)));
        return add;
    }

    //定义减的方法
    public static BigDecimal minus(double val1,double val2)
    {
        BigDecimal subtract = new BigDecimal(Double.toString(val1)).subtract(new BigDecimal(Double.toString(val2)));
        return subtract;
    }

    //乘法
    public static BigDecimal multipy(double val1,double val2)
    {
        BigDecimal subtract = new BigDecimal(Double.toString(val1)).multiply(new BigDecimal(Double.toString(val2)));
        return subtract;
    }

    //除法
    public static BigDecimal div(double val1,double val2)
    {
        BigDecimal subtract = new BigDecimal(Double.toString(val1)).divide(new BigDecimal(Double.toString(val2)),2, RoundingMode.HALF_UP);
        return subtract;
    }
}
