package com.building.manos.api;

import com.building.manos.api.dto.PurchaseRequest;
import com.building.manos.model.SaleRecord;
import com.building.manos.service.PurchasePreview;
import com.building.manos.service.PurchaseService;
import io.javalin.http.Context;

/**
 * 购买与折扣预览 REST 接口。
 *
 * @author 马玉
 * @since 2026-07-14
 */
public class PurchaseController {

    private final PurchaseService purchaseService;

    /**
     * @param purchaseService 购买业务服务
     */
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    /**
     * POST /api/purchases/preview
     *
     * @param ctx 请求上下文
     */
    public void preview(Context ctx) {
        PurchaseRequest request = ctx.bodyAsClass(PurchaseRequest.class);
        PurchasePreview preview = purchaseService.preview(request.getHouseId(), request.getDiscountType());
        ctx.json(ApiResponse.ok(DtoMapper.toDiscountPreview(preview)));
    }

    /**
     * POST /api/purchases
     *
     * @param ctx 请求上下文
     */
    public void purchase(Context ctx) {
        PurchaseRequest request = ctx.bodyAsClass(PurchaseRequest.class);
        SaleRecord record = purchaseService.purchase(
                request.getHouseId(),
                request.getDiscountType(),
                request.getCustomerName());
        ctx.status(201).json(ApiResponse.ok(DtoMapper.toSale(record)));
    }
}
