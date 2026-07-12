package com.building.manos.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link DBConfig} 的真实数据库连通性测试。
 *
 * @author 开发 A（待填写）
 * @since 2026-07-11
 */
@EnabledIfEnvironmentVariable(named = "RUN_DB_TESTS", matches = "(?i)true")
class DBConfigTest {

    @Test
    void shouldOpenDatabaseConnection() throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());
        }
    }
}
