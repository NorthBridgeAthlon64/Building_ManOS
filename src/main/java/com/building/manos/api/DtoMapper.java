package com.building.manos.api;

import com.building.manos.model.Building;
import com.building.manos.model.House;
import com.building.manos.model.SaleRecord;
import com.building.manos.service.PurchasePreview;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * model / 预览结果 → 前端 camelCase JSON Map。
 *
 * @author 马玉
 * @since 2026-07-14
 */
public final class DtoMapper {

    private static final DateTimeFormatter ISO_LOCAL = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private DtoMapper() {
    }

    /**
     * @param building 楼盘
     * @return JSON 友好 Map
     */
    public static Map<String, Object> toBuilding(Building building) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", building.getId());
        map.put("name", building.getName());
        map.put("landArea", building.getLandArea());
        map.put("address", building.getAddress());
        map.put("developer", nullToEmpty(building.getDeveloper()));
        map.put("remark", nullToEmpty(building.getRemark()));
        map.put("createdAt", formatDateTime(building.getCreatedAt()));
        return map;
    }

    /**
     * @param house 房屋
     * @return JSON 友好 Map
     */
    public static Map<String, Object> toHouse(House house) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", house.getId());
        map.put("buildingId", house.getBuildingId());
        map.put("buildingNo", house.getBuildingNo());
        map.put("roomNo", house.getRoomNo());
        map.put("area", house.getArea());
        map.put("unitPrice", house.getUnitPrice());
        map.put("totalPrice", house.getTotalPrice());
        map.put("status", house.getStatus() == null ? null : house.getStatus().name());
        map.put("soldAt", formatDateTime(house.getSoldAt()));
        return map;
    }

    /**
     * @param record 成交记录
     * @return JSON 友好 Map
     */
    public static Map<String, Object> toSale(SaleRecord record) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", record.getId());
        map.put("houseId", record.getHouseId());
        map.put("originalPrice", record.getOriginalPrice());
        map.put("discountType", record.getDiscountType());
        map.put("discountValue", record.getDiscountValue());
        map.put("finalPrice", record.getFinalPrice());
        map.put("customerName", nullToEmpty(record.getCustomerName()));
        map.put("soldAt", formatDateTime(record.getSoldAt()));
        return map;
    }

    /**
     * @param preview 购买预览
     * @return 对齐前端 DiscountPreview 的 Map
     */
    public static Map<String, Object> toDiscountPreview(PurchasePreview preview) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", preview.getDiscountType());
        map.put("value", preview.getDiscountValue());
        map.put("originalPrice", preview.getOriginalPrice());
        map.put("finalPrice", preview.getFinalPrice());
        map.put("saving", preview.getSaving());
        map.put("tierLabel", preview.getTierLabel());
        map.put("formula", preview.getFormula());
        return map;
    }

    private static String formatDateTime(LocalDateTime value) {
        return value == null ? null : value.format(ISO_LOCAL);
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
