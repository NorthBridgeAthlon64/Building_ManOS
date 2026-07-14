package com.building.manos.service;

import com.building.manos.model.House;
import com.building.manos.model.HouseStatus;
import com.building.manos.model.SaleRecord;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 看板汇总服务，聚合楼盘、房屋与成交统计。
 *
 * @author 邓单
 * @since 2026-07-14
 */
public class DashboardService {

    private final BuildingService buildingService;
    private final HouseService houseService;
    private final SaleRecordService saleRecordService;

    /**
     * 创建看板汇总服务。
     *
     * @param buildingService   楼盘服务
     * @param houseService      房屋服务
     * @param saleRecordService 成交服务
     */
    public DashboardService(BuildingService buildingService, HouseService houseService,
                            SaleRecordService saleRecordService) {
        this.buildingService = Objects.requireNonNull(buildingService, "buildingService 不能为空");
        this.houseService = Objects.requireNonNull(houseService, "houseService 不能为空");
        this.saleRecordService = Objects.requireNonNull(saleRecordService, "saleRecordService 不能为空");
    }

    /**
     * 汇总看板指标。
     *
     * @return 含楼盘数、在售/已售数、成交额等字段的 Map
     */
    public Map<String, Object> summary() {
        List<House> houses = houseService.listAll();
        List<SaleRecord> sales = saleRecordService.listAll();
        long onSale = houses.stream().filter(h -> h.getStatus() == HouseStatus.ON_SALE).count();
        long sold = houses.stream().filter(h -> h.getStatus() == HouseStatus.SOLD).count();
        BigDecimal totalSalesAmount = sales.stream()
                .map(SaleRecord::getFinalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("buildingCount", buildingService.listAll().size());
        result.put("houseCount", houses.size());
        result.put("onSaleCount", onSale);
        result.put("soldCount", sold);
        result.put("saleCount", sales.size());
        result.put("totalSalesAmount", totalSalesAmount);
        return result;
    }
}
