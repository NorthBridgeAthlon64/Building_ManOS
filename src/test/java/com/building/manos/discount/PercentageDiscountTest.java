package com.building.manos.discount;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link PercentageDiscount} 单元测试。
 *
 * @author 邓单
 * @since 2026-07-11
 */
class PercentageDiscountTest {

    private final PercentageDiscount discount = new PercentageDiscount();

    @Test
    void apply_低于100万_不打折() {
        BigDecimal result = discount.apply(new BigDecimal("800000"));
        assertEquals(new BigDecimal("800000.00"), result);
        assertEquals(BigDecimal.ONE, discount.getDiscountValue());
    }

    @Test
    void apply_100万到300万_97折() {
        BigDecimal result = discount.apply(new BigDecimal("1500000"));
        assertEquals(new BigDecimal("1455000.00"), result);
        assertEquals(new BigDecimal("0.97"), discount.getDiscountValue());
    }

    @Test
    void apply_恰好100万_97折() {
        BigDecimal result = discount.apply(new BigDecimal("1000000"));
        assertEquals(new BigDecimal("970000.00"), result);
        assertEquals(new BigDecimal("0.97"), discount.getDiscountValue());
    }

    @Test
    void apply_300万及以上_92折() {
        BigDecimal result = discount.apply(new BigDecimal("3500000"));
        assertEquals(new BigDecimal("3220000.00"), result);
        assertEquals(new BigDecimal("0.92"), discount.getDiscountValue());
    }

    @Test
    void apply_恰好300万_92折() {
        BigDecimal result = discount.apply(new BigDecimal("3000000"));
        assertEquals(new BigDecimal("2760000.00"), result);
        assertEquals(new BigDecimal("0.92"), discount.getDiscountValue());
    }

    @Test
    void getTypeName_返回PERCENTAGE() {
        assertEquals("PERCENTAGE", discount.getTypeName());
    }

    @Test
    void apply_原价为负_抛异常() {
        assertThrows(IllegalArgumentException.class,
                () -> discount.apply(new BigDecimal("-1")));
    }
}
