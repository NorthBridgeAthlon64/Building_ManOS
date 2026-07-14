package com.building.manos.api;

import com.building.manos.api.dto.BuildingRequest;
import com.building.manos.model.Building;
import com.building.manos.service.BuildingService;
import io.javalin.http.Context;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 楼盘 REST 接口。
 *
 * @author 马玉
 * @since 2026-07-14
 */
public class BuildingController {

    private final BuildingService buildingService;

    /**
     * @param buildingService 楼盘业务服务
     */
    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    /**
     * GET /api/buildings
     *
     * @param ctx 请求上下文
     */
    public void list(Context ctx) {
        List<?> data = buildingService.listAll().stream()
                .map(DtoMapper::toBuilding)
                .collect(Collectors.toList());
        ctx.json(ApiResponse.ok(data));
    }

    /**
     * GET /api/buildings/{id}
     *
     * @param ctx 请求上下文
     */
    public void get(Context ctx) {
        String id = ctx.pathParam("id");
        Building building = buildingService.getById(id);
        if (building == null) {
            ctx.status(404).json(ApiResponse.fail(ApiResponse.CODE_NOT_FOUND, "楼盘不存在：" + id));
            return;
        }
        ctx.json(ApiResponse.ok(DtoMapper.toBuilding(building)));
    }

    /**
     * POST /api/buildings
     *
     * @param ctx 请求上下文
     */
    public void create(Context ctx) {
        BuildingRequest request = ctx.bodyAsClass(BuildingRequest.class);
        Building building = new Building();
        building.setName(request.getName());
        building.setLandArea(request.getLandArea());
        building.setAddress(request.getAddress());
        building.setDeveloper(blankToNull(request.getDeveloper()));
        building.setRemark(blankToNull(request.getRemark()));
        buildingService.add(building);
        Building saved = buildingService.getById(building.getId());
        ctx.status(201).json(ApiResponse.ok(DtoMapper.toBuilding(saved)));
    }

    /**
     * PUT /api/buildings/{id}
     *
     * @param ctx 请求上下文
     */
    public void update(Context ctx) {
        String id = ctx.pathParam("id");
        BuildingRequest request = ctx.bodyAsClass(BuildingRequest.class);
        Building building = new Building();
        building.setId(id);
        building.setName(request.getName());
        building.setLandArea(request.getLandArea());
        building.setAddress(request.getAddress());
        building.setDeveloper(blankToNull(request.getDeveloper()));
        building.setRemark(blankToNull(request.getRemark()));
        buildingService.update(building);
        ctx.json(ApiResponse.ok(DtoMapper.toBuilding(buildingService.getById(id))));
    }

    /**
     * DELETE /api/buildings/{id}
     *
     * @param ctx 请求上下文
     */
    public void delete(Context ctx) {
        buildingService.delete(ctx.pathParam("id"));
        ctx.json(ApiResponse.ok());
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
