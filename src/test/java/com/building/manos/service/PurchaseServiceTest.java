package com.building.manos.service;

import com.building.manos.config.DBConfig;
import com.building.manos.dao.BuildingDao;
import com.building.manos.dao.HouseDao;
import com.building.manos.dao.SaleRecordDao;
import com.building.manos.model.Building;
import com.building.manos.model.House;
import com.building.manos.model.HouseStatus;
import com.building.manos.model.SaleRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link PurchaseService} 的真实数据库集成测试。
 *
 * @author 邓单
 * @since 2026-07-13
 */
@EnabledIfEnvironmentVariable(named = "RUN_DB_TESTS", matches = "(?i)true")
class PurchaseServiceTest {

    private final BuildingDao buildingDao = new BuildingDao();
    private final HouseDao houseDao = new HouseDao();
    private final SaleRecordDao saleRecordDao = new SaleRecordDao();
    private final PurchaseService purchaseService = new PurchaseService(houseDao, saleRecordDao);

    private final String buildingId = "P1P_SERVICE_BUILDING";
    private final String houseId = "P1P_SERVICE_HOUSE";

    @BeforeEach
    void insertOnSaleHouse() throws Exception {
        buildingDao.insert(new Building(buildingId, "购买测试楼盘",
                new BigDecimal("5000.00"), "测试地址", null, null, null));
        House house = new House(houseId, buildingId, "2", "801",
                new BigDecimal("200.00"), new BigDecimal("16000.00"),
                new BigDecimal("3200000.00"), HouseStatus.ON_SALE, null);
        houseDao.insert(house);
    }

    @AfterEach
    void cleanTestData() throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            deleteById(connection, "sale_record", "house_id", houseId);
            deleteById(connection, "house", "id", houseId);
            deleteById(connection, "building", "id", buildingId);
        }
    }

    @Test
    void shouldPreviewAndPurchaseWithPercentageDiscount() throws Exception {
        PurchasePreview preview = purchaseService.preview(houseId, 1);
        assertEquals(0, new BigDecimal("3200000.00").compareTo(preview.getOriginalPrice()));
        assertEquals(0, new BigDecimal("2944000.00").compareTo(preview.getFinalPrice()));
        assertEquals("PERCENTAGE", preview.getDiscountType());
        assertEquals(0, new BigDecimal("0.92").compareTo(preview.getDiscountValue()));

        SaleRecord record = purchaseService.purchase(houseId, 1, "测试客户");
        assertNotNull(record.getId());
        assertEquals(houseId, record.getHouseId());
        assertEquals(0, new BigDecimal("2944000.00").compareTo(record.getFinalPrice()));
        assertEquals(HouseStatus.SOLD, houseDao.findById(houseId).getStatus());
        assertFalse(saleRecordDao.findByHouseId(houseId).isEmpty());
    }

    @Test
    void shouldRejectPurchaseWhenHouseAlreadySold() throws Exception {
        purchaseService.purchase(houseId, 1, "第一次购买");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> purchaseService.purchase(houseId, 1, "第二次购买"));
        assertTrue(exception.getMessage().contains("已售出"));
    }

    private void deleteById(Connection connection, String table, String column, String id)
            throws Exception {
        String sql = "DELETE FROM " + table + " WHERE " + column + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        }
    }
}
