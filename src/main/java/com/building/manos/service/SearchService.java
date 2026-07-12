package com.building.manos.service;

import com.building.manos.dao.HouseDao;
import com.building.manos.model.House;
import com.building.manos.model.HouseStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * 房屋多条件查询服务。
 * <p>
 * 由 {@link com.building.manos.cli.MenuController} 调用，
 * 通过 {@link HouseDao} 执行组合查询，不含 SQL 拼接。
 * </p>
 *
 * @author 邓单
 * @since 2026-07-12
 * @see HouseDao
 */
public class SearchService {

    private final HouseDao houseDao;

    /**
     * 创建房屋查询服务。
     *
     * @param houseDao 房屋数据访问
     */
    public SearchService(HouseDao houseDao) {
        this.houseDao = Objects.requireNonNull(houseDao, "houseDao 不能为空");
    }

    /**
     * 按楼盘名称模糊查询房屋，不限销售状态。
     *
     * @param name 楼盘名称关键字
     * @return 匹配的房屋列表
     * @throws IllegalArgumentException 名称为空时
     * @throws IllegalStateException    数据库操作失败时
     */
    public List<House> searchByBuildingName(String name) {
        String keyword = requireNonBlankTrimmed(name, "楼盘名称");
        try {
            return houseDao.findByBuildingNameLike("%" + keyword + "%");
        } catch (SQLException e) {
            throw new IllegalStateException("数据库操作失败：" + e.getMessage(), e);
        }
    }

    /**
     * 按楼号查询房屋。
     *
     * @param buildingNo 楼号
     * @return 匹配的房屋列表
     * @throws IllegalArgumentException 楼号为空时
     * @throws IllegalStateException    数据库操作失败时
     */
    public List<House> searchByBuildingNo(String buildingNo) {
        requireNonBlank(buildingNo, "楼号");
        try {
            return houseDao.findByBuildingNo(buildingNo.trim());
        } catch (SQLException e) {
            throw new IllegalStateException("数据库操作失败：" + e.getMessage(), e);
        }
    }

    /**
     * 按总价区间查询房屋，不限销售状态。
     *
     * @param min 最低价（元），可为 {@code null}
     * @param max 最高价（元），可为 {@code null}
     * @return 匹配的房屋列表
     * @throws IllegalArgumentException 区间不合法时
     * @throws IllegalStateException    数据库操作失败时
     */
    public List<House> searchByPriceRange(BigDecimal min, BigDecimal max) {
        validateRange(min, max, "价格");
        try {
            return houseDao.findByPriceRange(min, max, null);
        } catch (SQLException e) {
            throw new IllegalStateException("数据库操作失败：" + e.getMessage(), e);
        }
    }

    /**
     * 按面积区间查询房屋，不限销售状态。
     *
     * @param min 最小面积（㎡），可为 {@code null}
     * @param max 最大面积（㎡），可为 {@code null}
     * @return 匹配的房屋列表
     * @throws IllegalArgumentException 区间不合法时
     * @throws IllegalStateException    数据库操作失败时
     */
    public List<House> searchByAreaRange(BigDecimal min, BigDecimal max) {
        validateRange(min, max, "面积");
        try {
            return houseDao.findByAreaRange(min, max, null);
        } catch (SQLException e) {
            throw new IllegalStateException("数据库操作失败：" + e.getMessage(), e);
        }
    }

    /**
     * 按销售状态查询房屋。
     *
     * @param status 销售状态
     * @return 匹配的房屋列表
     * @throws IllegalArgumentException 状态为空时
     * @throws IllegalStateException    数据库操作失败时
     */
    public List<House> searchByStatus(HouseStatus status) {
        Objects.requireNonNull(status, "销售状态不能为空");
        try {
            return houseDao.findByStatus(status);
        } catch (SQLException e) {
            throw new IllegalStateException("数据库操作失败：" + e.getMessage(), e);
        }
    }

    private static void validateRange(BigDecimal min, BigDecimal max, String fieldName) {
        if (min != null && min.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + "下限不能为负数");
        }
        if (max != null && max.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + "上限不能为负数");
        }
        if (min != null && max != null && min.compareTo(max) > 0) {
            throw new IllegalArgumentException(fieldName + "下限不能大于上限");
        }
    }

    private static String requireNonBlankTrimmed(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
        return value.trim();
    }

    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
    }
}
