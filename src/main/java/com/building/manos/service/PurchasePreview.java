package com.building.manos.service;

import java.math.BigDecimal;

/**
 * 购买预览结果，用于在成交确认前展示折扣与实付金额。
 *
 * @author 邓单
 * @since 2026-07-13
 */
public class PurchasePreview {

    private final BigDecimal originalPrice;
    private final BigDecimal finalPrice;
    private final String discountType;
    private final BigDecimal discountValue;

    /**
     * 创建购买预览结果。
     *
     * @param originalPrice 原价，单位：元
     * @param finalPrice 折后实付金额，单位：元
     * @param discountType 折扣类型标识
     * @param discountValue 本次使用的折扣参数
     */
    public PurchasePreview(BigDecimal originalPrice, BigDecimal finalPrice,
                           String discountType, BigDecimal discountValue) {
        this.originalPrice = originalPrice;
        this.finalPrice = finalPrice;
        this.discountType = discountType;
        this.discountValue = discountValue;
    }

    /**
     * 获取房屋原价。
     *
     * @return 原价，单位：元
     */
    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    /**
     * 获取折后实付金额。
     *
     * @return 实付金额，单位：元
     */
    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    /**
     * 获取折扣类型。
     *
     * @return 折扣类型标识
     */
    public String getDiscountType() {
        return discountType;
    }

    /**
     * 获取本次折扣参数。
     *
     * @return 比例折扣率或满减金额
     */
    public BigDecimal getDiscountValue() {
        return discountValue;
    }
}
