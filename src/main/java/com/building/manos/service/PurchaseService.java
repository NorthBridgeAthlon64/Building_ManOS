package com.building.manos.service;

import com.building.manos.config.DBConfig;
import com.building.manos.dao.HouseDao;
import com.building.manos.dao.SaleRecordDao;
import com.building.manos.discount.DiscountStrategy;
import com.building.manos.discount.PercentageDiscount;
import com.building.manos.discount.ThresholdDiscount;
import com.building.manos.model.House;
import com.building.manos.model.HouseStatus;
import com.building.manos.model.SaleRecord;
import com.building.manos.util.IdGenerator;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 房屋购买服务，完成选房、折扣计算、状态更新与成交记录写入。
 * <p>
 * 由 {@link com.building.manos.cli.MenuController} 调用；
 * 购买流程在事务中更新 {@code house} 并插入 {@code sale_record}。
 * </p>
 *
 * @author 邓单
 * @since 2026-07-12
 * @see HouseDao
 * @see SaleRecordDao
 * @see DiscountStrategy
 */
public class PurchaseService {

    private final HouseDao houseDao;
    private final SaleRecordDao saleRecordDao;

    /**
     * 创建购买服务。
     *
     * @param houseDao      房屋数据访问
     * @param saleRecordDao 成交记录数据访问
     */
    public PurchaseService(HouseDao houseDao, SaleRecordDao saleRecordDao) {
        this.houseDao = Objects.requireNonNull(houseDao, "houseDao 不能为空");
        this.saleRecordDao = Objects.requireNonNull(saleRecordDao, "saleRecordDao 不能为空");
    }

    /**
     * 预览指定房屋的折扣结果，不修改数据库。
     *
     * @param houseId 房屋编号
     * @param discountChoice 折扣选项，1 为档位比例折扣，2 为档位满减
     * @return 原价、折扣参数与实付金额组成的预览结果
     * @throws IllegalArgumentException 参数不合法、房屋不存在或已售出时
     * @throws IllegalStateException 数据库操作失败时
     */
    public PurchasePreview preview(String houseId, int discountChoice) {
        House house = loadOnSaleHouse(houseId);
        DiscountStrategy strategy = createStrategy(discountChoice);
        BigDecimal finalPrice = strategy.apply(house.getTotalPrice());
        return new PurchasePreview(
                house.getTotalPrice(),
                finalPrice,
                strategy.getTypeName(),
                strategy.getDiscountValue());
    }

    /**
     * 按控制台折扣选项购买房屋并记录成交信息。
     *
     * @param houseId 房屋编号
     * @param discountChoice 折扣选项，1 为档位比例折扣，2 为档位满减
     * @param customerName 客户姓名
     * @return 成交记录
     * @throws IllegalArgumentException 参数不合法、房屋不存在或已售出时
     * @throws IllegalStateException 数据库或事务失败时
     */
    public SaleRecord purchase(String houseId, int discountChoice, String customerName) {
        return purchase(houseId, createStrategy(discountChoice), customerName);
    }

    /**
     * 购买指定房屋并记录成交信息。
     * <p>
     * 流程：校验在售 → 折扣计算 → 事务内更新房屋为 SOLD → 插入 sale_record。
     * 须先调用 {@link DiscountStrategy#apply(BigDecimal)}，再读取 {@link DiscountStrategy#getDiscountValue()}。
     * </p>
     *
     * @param houseId      房屋编号
     * @param strategy     折扣策略
     * @param customerName 客户姓名
     * @return 成交记录
     * @throws IllegalArgumentException 参数不合法、房屋不存在或已售出时
     * @throws IllegalStateException    数据库或事务失败时
     */
    public SaleRecord purchase(String houseId, DiscountStrategy strategy, String customerName) {
        requireNonBlank(houseId, "房屋编号");
        Objects.requireNonNull(strategy, "折扣策略不能为空");
        requireNonBlank(customerName, "客户姓名");

        House house = loadOnSaleHouse(houseId);

        BigDecimal originalPrice = house.getTotalPrice();
        BigDecimal finalPrice = strategy.apply(originalPrice);
        LocalDateTime soldAt = LocalDateTime.now();

        SaleRecord record = new SaleRecord();
        record.setId(IdGenerator.nextSaleId());
        record.setHouseId(houseId);
        record.setOriginalPrice(originalPrice);
        record.setDiscountType(strategy.getTypeName());
        record.setDiscountValue(strategy.getDiscountValue());
        record.setFinalPrice(finalPrice);
        record.setCustomerName(customerName.trim());
        record.setSoldAt(soldAt);

        try (Connection conn = DBConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int updated = houseDao.updateStatusSold(conn, houseId, soldAt);
                if (updated <= 0) {
                    conn.rollback();
                    throw new IllegalArgumentException("房屋已售出，无法购买");
                }
                if (saleRecordDao.insert(conn, record) <= 0) {
                    conn.rollback();
                    throw new IllegalStateException("写入成交记录失败");
                }
                conn.commit();
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof SQLException) {
                    throw new IllegalStateException("数据库操作失败：" + e.getMessage(), e);
                }
                throw new IllegalStateException("购买失败：" + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("数据库连接失败：" + e.getMessage(), e);
        }
        return record;
    }

    private House loadOnSaleHouse(String houseId) {
        requireNonBlank(houseId, "房屋编号");
        House house;
        try {
            house = houseDao.findById(houseId);
        } catch (SQLException e) {
            throw new IllegalStateException("数据库操作失败：" + e.getMessage(), e);
        }
        if (house == null) {
            throw new IllegalArgumentException("房屋不存在：" + houseId);
        }
        if (house.getStatus() != HouseStatus.ON_SALE) {
            throw new IllegalArgumentException("房屋已售出，无法购买");
        }
        return house;
    }

    private static DiscountStrategy createStrategy(int discountChoice) {
        return switch (discountChoice) {
            case 1 -> new PercentageDiscount();
            case 2 -> new ThresholdDiscount();
            default -> throw new IllegalArgumentException("无效的折扣类型：" + discountChoice);
        };
    }

    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
    }
}
