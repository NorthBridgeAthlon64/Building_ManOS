package com.building.manos.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * HTTP 服务端配置，加载 {@code server.properties}，支持环境变量覆盖。
 *
 * @author 马玉
 * @since 2026-07-14
 */
public final class ServerConfig {

    private static final String CONFIG_FILE = "server.properties";
    private static final Properties PROPERTIES = loadProperties();

    private ServerConfig() {
    }

    /**
     * @return 绑定主机，默认 {@code 0.0.0.0}
     */
    public static String host() {
        return envOrProp("SERVER_HOST", "host", "0.0.0.0");
    }

    /**
     * @return 监听端口，默认 8080
     */
    public static int port() {
        String value = envOrProp("SERVER_PORT", "port", "8080");
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 8080;
        }
    }

    /**
     * @return CORS 允许的来源，逗号分隔，默认 {@code *}
     */
    public static String corsAllowedOrigins() {
        return envOrProp("CORS_ALLOWED_ORIGINS", "cors.allowedOrigins", "*");
    }

    private static String envOrProp(String envName, String propName, String defaultValue) {
        String env = System.getenv(envName);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        String prop = PROPERTIES.getProperty(propName);
        return prop == null || prop.isBlank() ? defaultValue : prop.trim();
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        ClassLoader classLoader = ServerConfig.class.getClassLoader();
        try (InputStream input = classLoader.getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("无法读取服务配置：" + e.getMessage());
        }
        return properties;
    }
}
