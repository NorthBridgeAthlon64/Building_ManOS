package com.building.manos.discount;

import java.math.BigDecimal;

/**
 * 按房屋原价划分的折扣档位，供比例折扣与满减策略共用。
 * <p>
 * 档位规则：
 * <ul>
 *   <li>原价 &lt; 100 万</li>
 *   <li>100 万 ≤ 原价 &lt; 300 万</li>
 *   <li>原价 ≥ 300 万</li>
 * </ul>
 * </p>
 *
 * @author 邓单
 * @since 2026-07-11
 * @see PercentageDiscount
 * @see ThresholdDiscount
 */
final class PriceTier {

    /** 第一档上限：100 万元 */
    static final BigDecimal TIER_1M = new BigDecimal("1000000");

    /** 第二档上限：300 万元 */
    static final BigDecimal TIER_3M = new BigDecimal("3000000");

    private PriceTier() {
    }

    /**
     * 按档位返回比例折扣率。
     *
     * @param originalPrice 原价（元）
     * @return &lt;100万为 1（不打折）；100~300万为 0.97；≥300万为 0.92
     */
    static BigDecimal percentageRate(BigDecimal originalPrice) {
        if (originalPrice.compareTo(TIER_1M) < 0) {
            return BigDecimal.ONE;
        }
        if (originalPrice.compareTo(TIER_3M) < 0) {
            return new BigDecimal("0.97");
        }
        return new BigDecimal("0.92");
    }

    /**
     * 按档位返回满减减免金额（元）。
     *
     * @param originalPrice 原价（元）
     * @return &lt;100万减 2 万；100~300万减 5 万；≥300万减 15 万
     */
    static BigDecimal reduceAmount(BigDecimal originalPrice) {
        if (originalPrice.compareTo(TIER_1M) < 0) {
            return new BigDecimal("20000");
        }
        if (originalPrice.compareTo(TIER_3M) < 0) {
            return new BigDecimal("50000");
        }
        return new BigDecimal("150000");
    }
}
