package com.building.manos.dao;

import com.building.manos.config.DBConfig;
import com.building.manos.model.Building;
import com.building.manos.model.House;
import com.building.manos.model.HouseStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link HouseDao} 的真实数据库集成测试。
 *
 * @author 开发 A（待填写）
 * @since 2026-07-11
 */
@EnabledIfEnvironmentVariable(named = "RUN_DB_TESTS", matches = "(?i)true")
class HouseDaoTest {
    private final BuildingDao buildingDao = new BuildingDao();
    private final HouseDao houseDao = new HouseDao();
    private final String buildingId = "P0A_HOUSE_BUILDING";
    private final String houseId = "P0A_HOUSE_TEST";

    @BeforeEach
    void insertBuilding() throws Exception {
        buildingDao.insert(new Building(buildingId, "P0 房屋测试楼盘",
                new BigDecimal("2000.00"), "测试地址", null, null, null));
    }

    @AfterEach
    void cleanTestData() throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            deleteById(connection, "house", houseId);
            deleteById(connection, "building", buildingId);
        }
    }

    @Test
    void shouldQueryAndSellHouse() throws Exception {
        House house = new House(houseId, buildingId, "1栋", "101",
                new BigDecimal("100.00"), new BigDecimal("12000.00"),
                new BigDecimal("1200000.00"), HouseStatus.ON_SALE, null);
        assertEquals(1, houseDao.insert(house));
        assertEquals(1, houseDao.countByBuildingId(buildingId));
        assertEquals(HouseStatus.ON_SALE, houseDao.findById(houseId).getStatus());
        assertTrue(houseDao.findByBuildingId(buildingId).stream()
                .anyMatch(item -> houseId.equals(item.getId())));
        assertTrue(houseDao.findByPriceRange(new BigDecimal("1000000"),
                        new BigDecimal("1300000"), HouseStatus.ON_SALE).stream()
                .anyMatch(item -> houseId.equals(item.getId())));
        assertTrue(houseDao.findByAreaRange(new BigDecimal("90"),
                        new BigDecimal("110"), HouseStatus.ON_SALE).stream()
                .anyMatch(item -> houseId.equals(item.getId())));
        LocalDateTime soldAt = LocalDateTime.now().withNano(0);
        assertEquals(1, houseDao.updateStatusSold(houseId, soldAt));
        assertEquals(0, houseDao.updateStatusSold(houseId, soldAt));
        assertEquals(HouseStatus.SOLD, houseDao.findById(houseId).getStatus());
        assertEquals(0, houseDao.deleteById(houseId));
    }

    private void deleteById(Connection connection, String table, String id) throws Exception {
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        }
    }
}
