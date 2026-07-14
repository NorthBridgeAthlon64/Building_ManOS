package com.building.manos.api;

import com.building.manos.service.DashboardService;
import io.javalin.http.Context;

/**
 * 看板汇总 REST 接口。
 *
 * @author 马玉
 * @since 2026-07-14
 */
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * @param dashboardService 看板服务
     */
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * GET /api/dashboard/summary
     *
     * @param ctx 请求上下文
     */
    public void summary(Context ctx) {
        ctx.json(ApiResponse.ok(dashboardService.summary()));
    }
}
