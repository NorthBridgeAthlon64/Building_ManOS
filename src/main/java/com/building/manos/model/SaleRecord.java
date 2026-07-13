package com.building.manos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成交记录实体，对应数据库表 {@code sale_record}。
 *
 * @author 陈辉
 * @since 2026-07-11
 */
public class SaleRecord {

    /** 成交记录主键 */
    private String id;
    /** 房屋主键 */
    private String houseId;
    /** 成交原价，单位：元 */
    private BigDecimal originalPrice;
    /** 折扣类型 */
    private String discountType;
    /** 折扣参数 */
    private BigDecimal discountValue;
    /** 实付金额，单位：元 */
    private BigDecimal finalPrice;
    /** 客户姓名 */
    private String customerName;
    /** 成交时间 */
    private LocalDateTime soldAt;

    /** 创建空的成交记录实体。 */
    public SaleRecord() {
    }

    /**
     * 创建包含全部字段的成交记录实体。
     *
     * @param id 成交记录主键
     * @param houseId 房屋主键
     * @param originalPrice 成交原价，单位：元
     * @param discountType 折扣类型，可为 {@code null}
     * @param discountValue 折扣参数，可为 {@code null}
     * @param finalPrice 实付金额，单位：元
     * @param customerName 客户姓名，可为 {@code null}
     * @param soldAt 成交时间，可为 {@code null}
     */
    public SaleRecord(String id, String houseId, BigDecimal originalPrice,
                      String discountType, BigDecimal discountValue, BigDecimal finalPrice,
                      String customerName, LocalDateTime soldAt) {
        this.id = id;
        this.houseId = houseId;
        this.originalPrice = originalPrice;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.finalPrice = finalPrice;
        this.customerName = customerName;
        this.soldAt = soldAt;
    }

    /**
     * 获取字段值。
     *
     * @return 成交记录主键
     */
    public String getId() { return id; }
    /**
     * 设置字段值。
     *
     * @param id 成交记录主键
     */
    public void setId(String id) { this.id = id; }
    /**
     * 获取字段值。
     *
     * @return 房屋主键
     */
    public String getHouseId() { return houseId; }
    /**
     * 设置字段值。
     *
     * @param houseId 房屋主键
     */
    public void setHouseId(String houseId) { this.houseId = houseId; }
    /**
     * 获取字段值。
     *
     * @return 成交原价，单位：元
     */
    public BigDecimal getOriginalPrice() { return originalPrice; }
    /**
     * 设置字段值。
     *
     * @param originalPrice 成交原价，单位：元
     */
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    /**
     * 获取字段值。
     *
     * @return 折扣类型
     */
    public String getDiscountType() { return discountType; }
    /**
     * 设置字段值。
     *
     * @param discountType 折扣类型，可为 {@code null}
     */
    public void setDiscountType(String discountType) { this.discountType = discountType; }
    /**
     * 获取字段值。
     *
     * @return 折扣参数
     */
    public BigDecimal getDiscountValue() { return discountValue; }
    /**
     * 设置字段值。
     *
     * @param discountValue 折扣参数，可为 {@code null}
     */
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    /**
     * 获取字段值。
     *
     * @return 实付金额，单位：元
     */
    public BigDecimal getFinalPrice() { return finalPrice; }
    /**
     * 设置字段值。
     *
     * @param finalPrice 实付金额，单位：元
     */
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }
    /**
     * 获取字段值。
     *
     * @return 客户姓名
     */
    public String getCustomerName() { return customerName; }
    /**
     * 设置字段值。
     *
     * @param customerName 客户姓名，可为 {@code null}
     */
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    /**
     * 获取字段值。
     *
     * @return 成交时间
     */
    public LocalDateTime getSoldAt() { return soldAt; }
    /**
     * 设置字段值。
     *
     * @param soldAt 成交时间，可为 {@code null}
     */
    public void setSoldAt(LocalDateTime soldAt) { this.soldAt = soldAt; }

    /**
     * 返回成交记录字段的字符串表示。
     *
     * @return 成交记录字段字符串
     */
    @Override
    public String toString() {
        return "SaleRecord{" + "id='" + id + '\'' + ", houseId='" + houseId + '\''
                + ", originalPrice=" + originalPrice + ", discountType='" + discountType + '\''
                + ", discountValue=" + discountValue + ", finalPrice=" + finalPrice
                + ", customerName='" + customerName + '\'' + ", soldAt=" + soldAt + '}';
    }
}
