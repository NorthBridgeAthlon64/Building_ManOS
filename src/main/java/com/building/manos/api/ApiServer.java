package com.building.manos.api;

import com.building.manos.config.ServerConfig;
import com.building.manos.dao.BuildingDao;
import com.building.manos.dao.HouseDao;
import com.building.manos.dao.SaleRecordDao;
import com.building.manos.service.BuildingService;
import com.building.manos.service.DashboardService;
import com.building.manos.service.HouseService;
import com.building.manos.service.PurchaseService;
import com.building.manos.service.SaleRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.json.JavalinJackson;

/**
 * HTTP API 服务器：组装 service、注册路由、CORS 与异常映射。
 *
 * @author 马玉
 * @since 2026-07-14
 */
public class ApiServer {

    private final Javalin app;

    /**
     * 创建并配置 API 服务器（尚未 start）。
     */
    public ApiServer() {
        BuildingDao buildingDao = new BuildingDao();
        HouseDao houseDao = new HouseDao();
        SaleRecordDao saleRecordDao = new SaleRecordDao();

        BuildingService buildingService = new BuildingService(buildingDao, houseDao);
        HouseService houseService = new HouseService(houseDao, buildingDao);
        PurchaseService purchaseService = new PurchaseService(houseDao, saleRecordDao);
        SaleRecordService saleRecordService = new SaleRecordService(saleRecordDao);
        DashboardService dashboardService = new DashboardService(
                buildingService, houseService, saleRecordService);

        BuildingController buildingController = new BuildingController(buildingService);
        HouseController houseController = new HouseController(houseService);
        PurchaseController purchaseController = new PurchaseController(purchaseService);
        SaleController saleController = new SaleController(saleRecordService);
        DashboardController dashboardController = new DashboardController(dashboardService);
        HealthController healthController = new HealthController();

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.jsonMapper(new JavalinJackson(mapper, false));
            config.bundledPlugins.enableCors(cors -> cors.addRule(rule -> {
                String origins = ServerConfig.corsAllowedOrigins();
                if ("*".equals(origins)) {
                    rule.anyHost();
                } else {
                    for (String origin : origins.split(",")) {
                        if (!origin.isBlank()) {
                            rule.allowHost(origin.trim());
                        }
                    }
                }
            }));
        });

        registerRoutes(buildingController, houseController, purchaseController,
                saleController, dashboardController, healthController);
        registerExceptionHandlers();
    }

    private void registerRoutes(BuildingController buildingController,
                                HouseController houseController,
                                PurchaseController purchaseController,
                                SaleController saleController,
                                DashboardController dashboardController,
                                HealthController healthController) {
        app.get("/api/health", healthController::health);
        app.get("/api/dashboard/summary", dashboardController::summary);

        app.get("/api/buildings", buildingController::list);
        app.get("/api/buildings/{id}", buildingController::get);
        app.post("/api/buildings", buildingController::create);
        app.put("/api/buildings/{id}", buildingController::update);
        app.delete("/api/buildings/{id}", buildingController::delete);

        app.get("/api/houses", houseController::list);
        app.get("/api/houses/{id}", houseController::get);
        app.post("/api/houses", houseController::create);
        app.put("/api/houses/{id}", houseController::update);
        app.delete("/api/houses/{id}", houseController::delete);

        app.post("/api/purchases/preview", purchaseController::preview);
        app.post("/api/purchases", purchaseController::purchase);

        app.get("/api/sales", saleController::list);
    }

    private void registerExceptionHandlers() {
        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            String message = e.getMessage() == null ? "请求参数不合法" : e.getMessage();
            int code = message.contains("不存在") ? ApiResponse.CODE_NOT_FOUND : ApiResponse.CODE_BAD_REQUEST;
            int status = code == ApiResponse.CODE_NOT_FOUND
                    ? HttpStatus.NOT_FOUND.getCode()
                    : HttpStatus.BAD_REQUEST.getCode();
            ctx.status(status).json(ApiResponse.fail(code, message));
        });
        app.exception(IllegalStateException.class, (e, ctx) ->
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR.getCode())
                        .json(ApiResponse.fail(ApiResponse.CODE_SERVER_ERROR,
                                e.getMessage() == null ? "服务器错误" : e.getMessage())));
        app.exception(Exception.class, (e, ctx) ->
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR.getCode())
                        .json(ApiResponse.fail(ApiResponse.CODE_SERVER_ERROR,
                                "服务器异常：" + e.getMessage())));
    }

    /**
     * 启动服务器并阻塞。
     */
    public void start() {
        String host = ServerConfig.host();
        int port = ServerConfig.port();
        app.start(host, port);
        System.out.println("Building ManOS API listening on http://" + host + ":" + port);
        System.out.println("Health: http://127.0.0.1:" + port + "/api/health");
    }

    /**
     * 停止服务器。
     */
    public void stop() {
        app.stop();
    }
}
