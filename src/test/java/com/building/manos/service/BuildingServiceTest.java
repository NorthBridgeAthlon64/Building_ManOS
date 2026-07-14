package com.building.manos.service;

import com.building.manos.config.DBConfig;
import com.building.manos.dao.BuildingDao;
import com.building.manos.dao.HouseDao;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link BuildingService} 的真实数据库集成测试。
 *
 * @author 邓单
 * @since 2026-07-13
 */
@EnabledIfEnvironmentVariable(named = "RUN_DB_TESTS", matches = "(?i)true")
class BuildingServiceTest {

    private final BuildingDao buildingDao = new BuildingDao();
    private final HouseDao houseDao = new HouseDao();
    private final BuildingService buildingService = new BuildingService(buildingDao, houseDao);

    private final String buildingId = "P1B_SERVICE_BUILDING";
    private final String houseId = "P1B_SERVICE_HOUSE";

    @BeforeEach
    void insertBuilding() throws Exception {
        buildingDao.insert(new Building(buildingId, "Service 测试楼盘",
                new BigDecimal("3000.00"), "测试地址", null, null, null));
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
    void shouldRejectDeleteWhenBuildingHasHouses() throws Exception {
        House house = new House(houseId, buildingId, "1", "101",
                new BigDecimal("80.00"), new BigDecimal("10000.00"),
                new BigDecimal("800000.00"), HouseStatus.ON_SALE, null);
        houseDao.insert(house);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> buildingService.delete(buildingId));
        assertTrue(exception.getMessage().contains("仍有房屋"));
        assertNotNull(buildingService.getById(buildingId));
    }

    @Test
    void shouldDeleteBuildingWhenNoHouses() {
        assertDoesNotThrow(() -> buildingService.delete(buildingId));
        assertNull(buildingService.getById(buildingId));
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
