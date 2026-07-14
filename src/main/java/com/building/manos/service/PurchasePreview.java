package com.building.manos.service;

import java.math.BigDecimal;

/**
 * 购买预览结果，供控制台/API 展示折后金额，不含数据库写入。
 *
 * @author 邓单
 * @since 2026-07-13
 */
public class PurchasePreview {

    private final BigDecimal originalPrice;
    private final BigDecimal finalPrice;
    private final String discountType;
    private final BigDecimal discountValue;
    private final BigDecimal saving;
    private final String tierLabel;
    private final String formula;

    /**
     * 创建购买预览结果。
     *
     * @param originalPrice 原价（元）
     * @param finalPrice    折后实付（元）
     * @param discountType  折扣类型标识
     * @param discountValue 折扣参数
     * @param saving        节省金额（元）
     * @param tierLabel     档位说明
     * @param formula       计算公式说明
     */
    public PurchasePreview(BigDecimal originalPrice, BigDecimal finalPrice,
                           String discountType, BigDecimal discountValue,
                           BigDecimal saving, String tierLabel, String formula) {
        this.originalPrice = originalPrice;
        this.finalPrice = finalPrice;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.saving = saving;
        this.tierLabel = tierLabel;
        this.formula = formula;
    }

    /**
     * @return 原价（元）
     */
    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    /**
     * @return 折后实付（元）
     */
    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    /**
     * @return 折扣类型标识
     */
    public String getDiscountType() {
        return discountType;
    }

    /**
     * @return 折扣参数
     */
    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    /**
     * @return 节省金额（元）
     */
    public BigDecimal getSaving() {
        return saving;
    }

    /**
     * @return 档位说明
     */
    public String getTierLabel() {
        return tierLabel;
    }

    /**
     * @return 计算公式说明
     */
    public String getFormula() {
        return formula;
    }
}
