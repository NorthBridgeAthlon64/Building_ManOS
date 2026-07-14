package com.building.manos.api;

import com.building.manos.model.SaleRecord;
import com.building.manos.service.SaleRecordService;
import io.javalin.http.Context;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 成交记录 REST 接口。
 *
 * @author 马玉
 * @since 2026-07-14
 */
public class SaleController {

    private final SaleRecordService saleRecordService;

    /**
     * @param saleRecordService 成交业务服务
     */
    public SaleController(SaleRecordService saleRecordService) {
        this.saleRecordService = saleRecordService;
    }

    /**
     * GET /api/sales?houseId=
     *
     * @param ctx 请求上下文
     */
    public void list(Context ctx) {
        String houseId = ctx.queryParam("houseId");
        List<SaleRecord> records = (houseId == null || houseId.isBlank())
                ? saleRecordService.listAll()
                : saleRecordService.listByHouseId(houseId);
        List<?> data = records.stream().map(DtoMapper::toSale).collect(Collectors.toList());
        ctx.json(ApiResponse.ok(data));
    }
}
