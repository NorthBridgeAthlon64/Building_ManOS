package com.building.manos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 楼盘实体，对应数据库表 {@code building}。
 *
 * @author 开发 A（待填写）
 * @since 2026-07-11
 */
public class Building {

    /** 楼盘主键 */
    private String id;
    /** 楼盘名称 */
    private String name;
    /** 占地面积，单位：平方米 */
    private BigDecimal landArea;
    /** 楼盘地址 */
    private String address;
    /** 开发商 */
    private String developer;
    /** 备注 */
    private String remark;
    /** 创建时间 */
    private LocalDateTime createdAt;

    /**
     * 创建空的楼盘实体。
     */
    public Building() {
    }

    /**
     * 创建包含全部字段的楼盘实体。
     *
     * @param id 楼盘主键
     * @param name 楼盘名称
     * @param landArea 占地面积，单位：平方米
     * @param address 楼盘地址
     * @param developer 开发商，可为 {@code null}
     * @param remark 备注，可为 {@code null}
     * @param createdAt 创建时间，可为 {@code null}
     */
    public Building(String id, String name, BigDecimal landArea, String address,
                    String developer, String remark, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.landArea = landArea;
        this.address = address;
        this.developer = developer;
        this.remark = remark;
        this.createdAt = createdAt;
    }

    /**
     * 获取字段值。
     *
     * @return 楼盘主键
     */
    public String getId() { return id; }
    /**
     * 设置字段值。
     *
     * @param id 楼盘主键
     */
    public void setId(String id) { this.id = id; }
    /**
     * 获取字段值。
     *
     * @return 楼盘名称
     */
    public String getName() { return name; }
    /**
     * 设置字段值。
     *
     * @param name 楼盘名称
     */
    public void setName(String name) { this.name = name; }
    /**
     * 获取字段值。
     *
     * @return 占地面积，单位：平方米
     */
    public BigDecimal getLandArea() { return landArea; }
    /**
     * 设置字段值。
     *
     * @param landArea 占地面积，单位：平方米
     */
    public void setLandArea(BigDecimal landArea) { this.landArea = landArea; }
    /**
     * 获取字段值。
     *
     * @return 楼盘地址
     */
    public String getAddress() { return address; }
    /**
     * 设置字段值。
     *
     * @param address 楼盘地址
     */
    public void setAddress(String address) { this.address = address; }
    /**
     * 获取字段值。
     *
     * @return 开发商
     */
    public String getDeveloper() { return developer; }
    /**
     * 设置字段值。
     *
     * @param developer 开发商，可为 {@code null}
     */
    public void setDeveloper(String developer) { this.developer = developer; }
    /**
     * 获取字段值。
     *
     * @return 备注
     */
    public String getRemark() { return remark; }
    /**
     * 设置字段值。
     *
     * @param remark 备注，可为 {@code null}
     */
    public void setRemark(String remark) { this.remark = remark; }
    /**
     * 获取字段值。
     *
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /**
     * 设置字段值。
     *
     * @param createdAt 创建时间，可为 {@code null}
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * 返回楼盘字段的字符串表示。
     *
     * @return 楼盘字段字符串
     */
    @Override
    public String toString() {
        return "Building{" + "id='" + id + '\'' + ", name='" + name + '\''
                + ", landArea=" + landArea + ", address='" + address + '\''
                + ", developer='" + developer + '\'' + ", remark='" + remark + '\''
                + ", createdAt=" + createdAt + '}';
    }
}
