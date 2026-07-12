package com.building.manos.dao;

import com.building.manos.config.DBConfig;
import com.building.manos.model.Building;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link BuildingDao} 的真实数据库集成测试。
 *
 * @author 开发 A（待填写）
 * @since 2026-07-11
 */
@EnabledIfEnvironmentVariable(named = "RUN_DB_TESTS", matches = "(?i)true")
class BuildingDaoTest {
    private final BuildingDao buildingDao = new BuildingDao();
    private final String buildingId = "P0A_BUILDING_TEST";

    @AfterEach
    void cleanTestData() throws Exception {
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM building WHERE id = ?")) {
            statement.setString(1, buildingId);
            statement.executeUpdate();
        }
    }

    @Test
    void shouldCompleteBuildingCrud() throws Exception {
        Building building = new Building(buildingId, "P0 测试楼盘",
                new BigDecimal("1234.50"), "测试地址", "测试开发商", null, null);
        assertEquals(1, buildingDao.insert(building));
        Building inserted = buildingDao.findById(buildingId);
        assertNotNull(inserted);
        assertEquals("P0 测试楼盘", inserted.getName());
        assertEquals(0, new BigDecimal("1234.50").compareTo(inserted.getLandArea()));
        assertTrue(buildingDao.findAll().stream().anyMatch(item -> buildingId.equals(item.getId())));
        inserted.setName("P0 测试楼盘-已修改");
        assertEquals(1, buildingDao.update(inserted));
        assertEquals("P0 测试楼盘-已修改", buildingDao.findById(buildingId).getName());
        assertEquals(1, buildingDao.deleteById(buildingId));
        assertNull(buildingDao.findById(buildingId));
    }
}
