package com.building.manos.discount;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link ThresholdDiscount} 单元测试。
 *
 * @author 邓单
 * @since 2026-07-11
 */
class ThresholdDiscountTest {

    private final ThresholdDiscount discount = new ThresholdDiscount();

    @Test
    void apply_低于100万_减2万() {
        BigDecimal result = discount.apply(new BigDecimal("800000"));
        assertEquals(new BigDecimal("780000.00"), result);
        assertEquals(new BigDecimal("20000"), discount.getDiscountValue());
    }

    @Test
    void apply_100万到300万_减5万() {
        BigDecimal result = discount.apply(new BigDecimal("1500000"));
        assertEquals(new BigDecimal("1450000.00"), result);
        assertEquals(new BigDecimal("50000"), discount.getDiscountValue());
    }

    @Test
    void apply_恰好100万_减5万() {
        BigDecimal result = discount.apply(new BigDecimal("1000000"));
        assertEquals(new BigDecimal("950000.00"), result);
        assertEquals(new BigDecimal("50000"), discount.getDiscountValue());
    }

    @Test
    void apply_300万及以上_减15万() {
        BigDecimal result = discount.apply(new BigDecimal("3500000"));
        assertEquals(new BigDecimal("3350000.00"), result);
        assertEquals(new BigDecimal("150000"), discount.getDiscountValue());
    }

    @Test
    void apply_恰好300万_减15万() {
        BigDecimal result = discount.apply(new BigDecimal("3000000"));
        assertEquals(new BigDecimal("2850000.00"), result);
        assertEquals(new BigDecimal("150000"), discount.getDiscountValue());
    }

    @Test
    void apply_减免后不低于零() {
        BigDecimal result = discount.apply(new BigDecimal("10000"));
        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    void getTypeName_返回THRESHOLD() {
        assertEquals("THRESHOLD", discount.getTypeName());
    }

    @Test
    void apply_原价为负_抛异常() {
        assertThrows(IllegalArgumentException.class,
                () -> discount.apply(new BigDecimal("-1")));
    }
}
