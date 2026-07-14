package com.building.manos.api.dto;

/**
 * 购买/预览请求体。
 *
 * @author 马玉
 * @since 2026-07-14
 */
public class PurchaseRequest {

    private String houseId;
    private String discountType;
    private String customerName;

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
