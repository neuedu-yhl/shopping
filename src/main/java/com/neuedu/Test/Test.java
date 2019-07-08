package com.neuedu.Test;

import com.neuedu.util.BigDecimalUtils;

import java.math.BigDecimal;

public class Test {


    public static void main(String[] args) {
        //BigDecimal
        BigDecimal plus = BigDecimalUtils.div(0.1, 0.2);
        System.out.println(plus.doubleValue());

        Long orderNo = 46456465L;
        String s = orderNo.toString();
        System.out.println(s);
    }
}