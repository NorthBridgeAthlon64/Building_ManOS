package com.building.manos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房屋实体，对应数据库表 {@code house}。
 *
 * @author 陈辉
 * @since 2026-07-11
 */
public class House {

    /** 房屋主键 */
    private String id;
    /** 所属楼盘主键 */
    private String buildingId;
    /** 楼号 */
    private String buildingNo;
    /** 房号 */
    private String roomNo;
    /** 建筑面积，单位：平方米 */
    private BigDecimal area;
    /** 单价，单位：元每平方米 */
    private BigDecimal unitPrice;
    /** 总价，单位：元 */
    private BigDecimal totalPrice;
    /** 销售状态 */
    private HouseStatus status;
    /** 售出时间 */
    private LocalDateTime soldAt;

    /** 创建空的房屋实体。 */
    public House() {
    }

    /**
     * 创建包含全部字段的房屋实体。
     *
     * @param id 房屋主键
     * @param buildingId 所属楼盘主键
     * @param buildingNo 楼号
     * @param roomNo 房号
     * @param area 建筑面积，单位：平方米
     * @param unitPrice 单价，单位：元每平方米
     * @param totalPrice 总价，单位：元
     * @param status 销售状态
     * @param soldAt 售出时间，可为 {@code null}
     */
    public House(String id, String buildingId, String buildingNo, String roomNo,
                 BigDecimal area, BigDecimal unitPrice, BigDecimal totalPrice,
                 HouseStatus status, LocalDateTime soldAt) {
        this.id = id;
        this.buildingId = buildingId;
        this.buildingNo = buildingNo;
        this.roomNo = roomNo;
        this.area = area;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.status = status;
        this.soldAt = soldAt;
    }

    /**
     * 获取字段值。
     *
     * @return 房屋主键
     */
    public String getId() { return id; }
    /**
     * 设置字段值。
     *
     * @param id 房屋主键
     */
    public void setId(String id) { this.id = id; }
    /**
     * 获取字段值。
     *
     * @return 所属楼盘主键
     */
    public String getBuildingId() { return buildingId; }
    /**
     * 设置字段值。
     *
     * @param buildingId 所属楼盘主键
     */
    public void setBuildingId(String buildingId) { this.buildingId = buildingId; }
    /**
     * 获取字段值。
     *
     * @return 楼号
     */
    public String getBuildingNo() { return buildingNo; }
    /**
     * 设置字段值。
     *
     * @param buildingNo 楼号
     */
    public void setBuildingNo(String buildingNo) { this.buildingNo = buildingNo; }
    /**
     * 获取字段值。
     *
     * @return 房号
     */
    public String getRoomNo() { return roomNo; }
    /**
     * 设置字段值。
     *
     * @param roomNo 房号
     */
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    /**
     * 获取字段值。
     *
     * @return 建筑面积，单位：平方米
     */
    public BigDecimal getArea() { return area; }
    /**
     * 设置字段值。
     *
     * @param area 建筑面积，单位：平方米
     */
    public void setArea(BigDecimal area) { this.area = area; }
    /**
     * 获取字段值。
     *
     * @return 单价，单位：元每平方米
     */
    public BigDecimal getUnitPrice() { return unitPrice; }
    /**
     * 设置字段值。
     *
     * @param unitPrice 单价，单位：元每平方米
     */
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    /**
     * 获取字段值。
     *
     * @return 总价，单位：元
     */
    public BigDecimal getTotalPrice() { return totalPrice; }
    /**
     * 设置字段值。
     *
     * @param totalPrice 总价，单位：元
     */
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    /**
     * 获取字段值。
     *
     * @return 销售状态
     */
    public HouseStatus getStatus() { return status; }
    /**
     * 设置字段值。
     *
     * @param status 销售状态
     */
    public void setStatus(HouseStatus status) { this.status = status; }
    /**
     * 获取字段值。
     *
     * @return 售出时间
     */
    public LocalDateTime getSoldAt() { return soldAt; }
    /**
     * 设置字段值。
     *
     * @param soldAt 售出时间，可为 {@code null}
     */
    public void setSoldAt(LocalDateTime soldAt) { this.soldAt = soldAt; }

    /**
     * 返回房屋字段的字符串表示。
     *
     * @return 房屋字段字符串
     */
    @Override
    public String toString() {
        return "House{" + "id='" + id + '\'' + ", buildingId='" + buildingId + '\''
                + ", buildingNo='" + buildingNo + '\'' + ", roomNo='" + roomNo + '\''
                + ", area=" + area + ", unitPrice=" + unitPrice + ", totalPrice=" + totalPrice
                + ", status=" + status + ", soldAt=" + soldAt + '}';
    }
}
