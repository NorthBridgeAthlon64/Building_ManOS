package com.building.manos.api;

import com.building.manos.api.dto.HouseRequest;
import com.building.manos.model.House;
import com.building.manos.service.HouseService;
import io.javalin.http.Context;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 房屋 REST 接口。
 *
 * @author 马玉
 * @since 2026-07-14
 */
public class HouseController {

    private final HouseService houseService;

    /**
     * @param houseService 房屋业务服务
     */
    public HouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    /**
     * GET /api/houses
     *
     * @param ctx 请求上下文
     */
    public void list(Context ctx) {
        List<?> data = houseService.listAll().stream()
                .map(DtoMapper::toHouse)
                .collect(Collectors.toList());
        ctx.json(ApiResponse.ok(data));
    }

    /**
     * GET /api/houses/{id}
     *
     * @param ctx 请求上下文
     */
    public void get(Context ctx) {
        String id = ctx.pathParam("id");
        House house = houseService.getById(id);
        if (house == null) {
            ctx.status(404).json(ApiResponse.fail(ApiResponse.CODE_NOT_FOUND, "房屋不存在：" + id));
            return;
        }
        ctx.json(ApiResponse.ok(DtoMapper.toHouse(house)));
    }

    /**
     * POST /api/houses
     *
     * @param ctx 请求上下文
     */
    public void create(Context ctx) {
        HouseRequest request = ctx.bodyAsClass(HouseRequest.class);
        House house = new House();
        house.setBuildingId(request.getBuildingId());
        house.setBuildingNo(request.getBuildingNo());
        house.setRoomNo(request.getRoomNo());
        house.setArea(request.getArea());
        house.setUnitPrice(request.getUnitPrice());
        houseService.add(house);
        ctx.status(201).json(ApiResponse.ok(DtoMapper.toHouse(houseService.getById(house.getId()))));
    }

    /**
     * PUT /api/houses/{id}
     *
     * @param ctx 请求上下文
     */
    public void update(Context ctx) {
        String id = ctx.pathParam("id");
        HouseRequest request = ctx.bodyAsClass(HouseRequest.class);
        House existing = houseService.getById(id);
        if (existing == null) {
            ctx.status(404).json(ApiResponse.fail(ApiResponse.CODE_NOT_FOUND, "房屋不存在：" + id));
            return;
        }
        House house = new House();
        house.setId(id);
        house.setBuildingId(request.getBuildingId() != null ? request.getBuildingId() : existing.getBuildingId());
        house.setBuildingNo(request.getBuildingNo());
        house.setRoomNo(request.getRoomNo());
        house.setArea(request.getArea());
        house.setUnitPrice(request.getUnitPrice());
        houseService.update(house);
        ctx.json(ApiResponse.ok(DtoMapper.toHouse(houseService.getById(id))));
    }

    /**
     * DELETE /api/houses/{id}
     *
     * @param ctx 请求上下文
     */
    public void delete(Context ctx) {
        houseService.delete(ctx.pathParam("id"));
        ctx.json(ApiResponse.ok());
    }
}
