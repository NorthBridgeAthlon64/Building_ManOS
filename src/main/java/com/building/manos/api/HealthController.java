package com.building.manos.api;

import com.building.manos.config.DBConfig;
import io.javalin.http.Context;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健康检查 REST 接口。
 *
 * @author 马玉
 * @since 2026-07-14
 */
public class HealthController {

    /**
     * GET /api/health
     *
     * @param ctx 请求上下文
     */
    public void health(Context ctx) {
        Map<String, Object> data = new LinkedHashMap<>();
        String dbStatus = "DOWN";
        try (Connection connection = DBConfig.getConnection()) {
            if (connection != null && connection.isValid(2)) {
                dbStatus = "UP";
            }
        } catch (Exception ignored) {
            dbStatus = "DOWN";
        }
        data.put("status", "UP".equals(dbStatus) ? "UP" : "DEGRADED");
        data.put("db", dbStatus);
        data.put("service", "Building ManOS API");
        ctx.json(ApiResponse.ok(data));
    }
}
