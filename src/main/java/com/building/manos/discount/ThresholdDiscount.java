package com.building.manos.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 按原价档位减免固定金额，公式：{@code 实付 = 原价 - 减免额}，结果不低于 0。
 * <p>
 * 档位优惠：&lt;100万减 2 万；100~300万减 5 万；≥300万减 15 万。
 * {@link #getDiscountValue()} 返回最近一次 {@link #apply(BigDecimal)} 使用的减免金额。
 * </p>
 *
 * @author 邓单
 * @since 2026-07-11
 * @see DiscountStrategy
 * @see PriceTier
 */
public class ThresholdDiscount implements DiscountStrategy {

    private static final String TYPE_NAME = "THRESHOLD";

    private BigDecimal lastReduceAmount = BigDecimal.ZERO;

    /**
     * 创建按档位计算的满减策略。
     */
    public ThresholdDiscount() {
    }

    /**
     * 按原价档位计算折后实付金额。
     *
     * @param originalPrice 原价（元），非空且非负
     * @return 折后实付金额（元），四舍五入保留两位小数
     * @throws IllegalArgumentException 原价为空或为负时
     */
    @Override
    public BigDecimal apply(BigDecimal originalPrice) {
        validateOriginalPrice(originalPrice);
        lastReduceAmount = PriceTier.reduceAmount(originalPrice);
        BigDecimal result = originalPrice.subtract(lastReduceAmount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            result = BigDecimal.ZERO;
        }
        return result.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 返回折扣类型标识，写入成交记录。
     *
     * @return 固定为 {@code THRESHOLD}
     */
    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    /**
     * 返回最近一次计算使用的减免金额（元）。
     *
     * @return 减免金额，未调用 {@link #apply(BigDecimal)} 前为 0
     */
    @Override
    public BigDecimal getDiscountValue() {
        return lastReduceAmount;
    }

    /**
     * 校验原价非空且非负。
     *
     * @param originalPrice 原价（元）
     * @throws IllegalArgumentException 为空或为负时
     */
    private static void validateOriginalPrice(BigDecimal originalPrice) {
        Objects.requireNonNull(originalPrice, "原价不能为空");
        if (originalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("原价不能为负数");
        }
    }
}
