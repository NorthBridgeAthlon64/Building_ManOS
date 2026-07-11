package com.building.manos.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 按原价档位计算比例折扣，公式：{@code 实付 = 原价 × rate}。
 * <p>
 * 档位优惠：&lt;100万不打折；100~300万 97 折；≥300万 92 折。
 * {@link #getDiscountValue()} 返回最近一次 {@link #apply(BigDecimal)} 使用的折扣率。
 * </p>
 *
 * @author 邓单
 * @since 2026-07-11
 * @see DiscountStrategy
 * @see PriceTier
 */
public class PercentageDiscount implements DiscountStrategy {

    private static final String TYPE_NAME = "PERCENTAGE";

    private BigDecimal lastAppliedRate = BigDecimal.ONE;

    /**
     * 创建按档位计算的比例折扣策略。
     */
    public PercentageDiscount() {
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
        lastAppliedRate = PriceTier.percentageRate(originalPrice);
        return originalPrice.multiply(lastAppliedRate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 返回折扣类型标识，写入成交记录。
     *
     * @return 固定为 {@code PERCENTAGE}
     */
    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    /**
     * 返回最近一次计算使用的折扣率。
     *
     * @return 折扣率，未调用 {@link #apply(BigDecimal)} 前为 1
     */
    @Override
    public BigDecimal getDiscountValue() {
        return lastAppliedRate;
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
