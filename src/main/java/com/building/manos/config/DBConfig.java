package com.building.manos.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接配置，负责加载连接参数并创建 JDBC 连接。
 *
 * @author 开发 A（待填写）
 * @since 2026-07-11
 */
public final class DBConfig {

    private static final String CONFIG_FILE = "database.properties";
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/building_manos"
            + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "root";
    private static final Properties PROPERTIES = loadProperties();

    private DBConfig() {
    }

    /**
     * 根据环境变量或配置文件创建数据库连接。
     *
     * @return 可用的 JDBC 数据库连接
     * @throws SQLException 驱动加载失败或数据库连接失败时
     */
    public static Connection getConnection() throws SQLException {
        String driver = propertyOrDefault("driver", DEFAULT_DRIVER);
        String url = environmentOrProperty("DB_URL", "url", DEFAULT_URL);
        String user = environmentOrProperty("DB_USER", "user", DEFAULT_USER);
        String password = environmentOrProperty("DB_PASSWORD", "password", DEFAULT_PASSWORD);

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("无法加载 JDBC 驱动：" + driver, e);
        }
        return DriverManager.getConnection(url, user, password);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        ClassLoader classLoader = DBConfig.class.getClassLoader();
        try (InputStream input = classLoader.getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("无法读取数据库配置文件：" + e.getMessage());
        }
        return properties;
    }

    private static String environmentOrProperty(
            String environmentName, String propertyName, String defaultValue) {
        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }
        return propertyOrDefault(propertyName, defaultValue);
    }

    private static String propertyOrDefault(String propertyName, String defaultValue) {
        String value = PROPERTIES.getProperty(propertyName);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }
}
