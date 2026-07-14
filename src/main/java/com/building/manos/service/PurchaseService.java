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
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

/**
 * 房屋购买服务，完成选房、折扣计算、状态更新与成交记录写入。
 * <p>
 * 由 {@link com.building.manos.cli.MenuController} 与 API 层调用；
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

    private static final BigDecimal TIER_1M = new BigDecimal("1000000");
    private static final BigDecimal TIER_3M = new BigDecimal("3000000");

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
     * 预览指定房屋的折后实付金额，不写入数据库。
     *
     * @param houseId        房屋编号
     * @param discountChoice 折扣类型：1=档位比例，2=档位满减
     * @return 购买预览结果
     */
    public PurchasePreview preview(String houseId, int discountChoice) {
        return preview(houseId, toDiscountType(discountChoice));
    }

    /**
     * 预览指定房屋的折后实付金额，不写入数据库。
     *
     * @param houseId      房屋编号
     * @param discountType {@code PERCENTAGE} 或 {@code THRESHOLD}
     * @return 购买预览结果
     */
    public PurchasePreview preview(String houseId, String discountType) {
        House house = loadOnSaleHouse(houseId);
        DiscountStrategy strategy = createStrategy(discountType);
        BigDecimal originalPrice = house.getTotalPrice();
        BigDecimal finalPrice = strategy.apply(originalPrice);
        BigDecimal discountValue = strategy.getDiscountValue();
        String typeName = strategy.getTypeName();
        return new PurchasePreview(
                originalPrice,
                finalPrice,
                typeName,
                discountValue,
                originalPrice.subtract(finalPrice).setScale(2, RoundingMode.HALF_UP),
                tierLabel(originalPrice),
                formula(typeName, discountValue));
    }

    /**
     * 购买指定房屋并记录成交信息（按折扣编号选择）。
     *
     * @param houseId        房屋编号
     * @param discountChoice 折扣类型：1=档位比例，2=档位满减
     * @param customerName   客户姓名
     * @return 成交记录
     */
    public SaleRecord purchase(String houseId, int discountChoice, String customerName) {
        return purchase(houseId, createStrategy(toDiscountType(discountChoice)), customerName);
    }

    /**
     * 购买指定房屋并记录成交信息（按折扣类型字符串）。
     *
     * @param houseId      房屋编号
     * @param discountType {@code PERCENTAGE} 或 {@code THRESHOLD}
     * @param customerName 客户姓名
     * @return 成交记录
     */
    public SaleRecord purchase(String houseId, String discountType, String customerName) {
        return purchase(houseId, createStrategy(discountType), customerName);
    }

    /**
     * 购买指定房屋并记录成交信息。
     *
     * @param houseId      房屋编号
     * @param strategy     折扣策略
     * @param customerName 客户姓名
     * @return 成交记录
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

    private static String toDiscountType(int discountChoice) {
        if (discountChoice == 1) {
            return "PERCENTAGE";
        }
        if (discountChoice == 2) {
            return "THRESHOLD";
        }
        throw new IllegalArgumentException("无效的折扣类型：" + discountChoice);
    }

    private static DiscountStrategy createStrategy(String discountType) {
        requireNonBlank(discountType, "折扣类型");
        String normalized = discountType.trim().toUpperCase(Locale.ROOT);
        if ("PERCENTAGE".equals(normalized)) {
            return new PercentageDiscount();
        }
        if ("THRESHOLD".equals(normalized)) {
            return new ThresholdDiscount();
        }
        throw new IllegalArgumentException("无效的折扣类型：" + discountType);
    }

    private static String tierLabel(BigDecimal originalPrice) {
        if (originalPrice.compareTo(TIER_1M) < 0) {
            return "100 万以下";
        }
        if (originalPrice.compareTo(TIER_3M) < 0) {
            return "100–300 万";
        }
        return "300 万及以上";
    }

    private static String formula(String typeName, BigDecimal discountValue) {
        if ("PERCENTAGE".equals(typeName)) {
            BigDecimal percent = discountValue.multiply(new BigDecimal("100"))
                    .setScale(0, RoundingMode.HALF_UP);
            return "原价 × " + percent.toPlainString() + "%";
        }
        return "原价 − ¥" + discountValue.toPlainString();
    }

    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
    }
}
