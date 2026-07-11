package com.building.manos.discount;

import java.math.BigDecimal;

/**
 * 房屋购买折扣策略接口，由 {@link com.building.manos.service.PurchaseService} 调用计算实付金额。
 * <p>
 * 实现类将折扣类型与参数写入成交记录 {@code sale_record.discount_type}、
 * {@code sale_record.discount_value}。折扣力度按房屋原价档位由
 * {@link PriceTier} 统一划分。
 * </p>
 *
 * @author 邓单
 * @since 2026-07-11
 * @see PercentageDiscount
 * @see ThresholdDiscount
 */
public interface DiscountStrategy {

    /**
     * 根据原价计算折后实付金额。
     *
     * @param originalPrice 房屋原价（元），非空且非负
     * @return 折后实付金额（元），保留两位小数
     */
    BigDecimal apply(BigDecimal originalPrice);

    /**
     * 折扣类型名称，写入成交记录。
     *
     * @return 如 {@code PERCENTAGE}、{@code THRESHOLD}
     */
    String getTypeName();

    /**
     * 折扣参数，写入成交记录。
     * <p>
     * 须先调用 {@link #apply(BigDecimal)}，返回该次计算实际使用的参数：
     * 比例折扣为折扣率，满减为减免金额（元）。
     * </p>
     *
     * @return 折扣率或减免金额（元）
     */
    BigDecimal getDiscountValue();
}
