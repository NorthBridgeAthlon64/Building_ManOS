package com.building.manos.api.dto;

import java.math.BigDecimal;

/**
 * 楼盘写入请求体。
 *
 * @author 马玉
 * @since 2026-07-14
 */
public class BuildingRequest {

    private String name;
    private BigDecimal landArea;
    private String address;
    private String developer;
    private String remark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getLandArea() {
        return landArea;
    }

    public void setLandArea(BigDecimal landArea) {
        this.landArea = landArea;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
