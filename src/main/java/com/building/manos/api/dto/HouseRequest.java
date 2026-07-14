package com.building.manos.api.dto;

import java.math.BigDecimal;

/**
 * 房屋写入请求体。
 *
 * @author 马玉
 * @since 2026-07-14
 */
public class HouseRequest {

    private String buildingId;
    private String buildingNo;
    private String roomNo;
    private BigDecimal area;
    private BigDecimal unitPrice;

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
