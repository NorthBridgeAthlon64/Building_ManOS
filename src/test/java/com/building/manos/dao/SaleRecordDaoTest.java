package com.building.manos.dao;

import com.building.manos.config.DBConfig;
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
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link SaleRecordDao} 的真实数据库集成测试。
 *
 * @author 陈辉
 * @since 2026-07-13
 */
@EnabledIfEnvironmentVariable(named = "RUN_DB_TESTS", matches = "(?i)true")
class SaleRecordDaoTest {

    private static final String BUILDING_ID = "P0A_SALE_BUILDING";
    private static final String HOUSE_ID = "P0A_SALE_HOUSE";
    private static final String SALE_ID = "P0A_SALE_TEST";
    private static final String DUPLICATE_SALE_ID = "P0A_SALE_DUPLICATE";

    private final BuildingDao buildingDao = new BuildingDao();
    private final HouseDao houseDao = new HouseDao();
    private final SaleRecordDao saleRecordDao = new SaleRecordDao();

    @BeforeEach
    void insertHouse() throws Exception {
        buildingDao.insert(new Building(BUILDING_ID, "P0 成交测试楼盘",
                new BigDecimal("3000.00"), "测试地址", null, null, null));
        houseDao.insert(new House(HOUSE_ID, BUILDING_ID, "2栋", "201",
                new BigDecimal("120.00"), new BigDecimal("15000.00"),
                new BigDecimal("1800000.00"), HouseStatus.SOLD, LocalDateTime.now()));
    }

    @AfterEach
    void cleanTestData() throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            delete(connection, "DELETE FROM sale_record WHERE house_id = ?", HOUSE_ID);
            delete(connection, "DELETE FROM house WHERE id = ?", HOUSE_ID);
            delete(connection, "DELETE FROM building WHERE id = ?", BUILDING_ID);
        }
    }

    @Test
    void shouldInsertQueryAndRejectDuplicateHouseSale() throws Exception {
        SaleRecord record = createRecord(SALE_ID, "陈辉测试客户");
        assertEquals(1, saleRecordDao.insert(record));

        assertEquals(1, saleRecordDao.findByHouseId(HOUSE_ID).size());
        assertEquals(SALE_ID, saleRecordDao.findByHouseId(HOUSE_ID).get(0).getId());
        assertTrue(saleRecordDao.findAll().stream()
                .anyMatch(item -> SALE_ID.equals(item.getId())));

        SaleRecord duplicate = createRecord(DUPLICATE_SALE_ID, "重复成交客户");
        assertThrows(SQLException.class, () -> saleRecordDao.insert(duplicate));
        assertEquals(1, saleRecordDao.findByHouseId(HOUSE_ID).size());
    }

    private SaleRecord createRecord(String id, String customerName) {
        return new SaleRecord(id, HOUSE_ID, new BigDecimal("1800000.00"),
                "PERCENTAGE", new BigDecimal("0.9000"),
                new BigDecimal("1620000.00"), customerName, LocalDateTime.now());
    }

    private void delete(Connection connection, String sql, String id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        }
    }
}
