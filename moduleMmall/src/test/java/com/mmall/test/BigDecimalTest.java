package com.mmall.test;

import org.junit.Test;

import java.math.BigDecimal;

public class BigDecimalTest {

    @Test
    public void test1(){
        BigDecimal b1 = new BigDecimal("0.005");
        BigDecimal b2 = new BigDecimal("0.001");
        System.out.println(b1.add(b2));
    }
}
