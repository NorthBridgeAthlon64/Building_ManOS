package com.building.manos.service;

import com.building.manos.dao.BuildingDao;
import com.building.manos.dao.HouseDao;
import com.building.manos.model.House;
import com.building.manos.model.HouseStatus;
import com.building.manos.util.IdGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * 房屋业务服务，负责房屋增删改查、总价计算及在售状态校验。
 * <p>
 * 由 {@link com.building.manos.cli.MenuController} 调用，
 * 通过 {@link HouseDao}、{@link BuildingDao} 访问数据库。
 * </p>
 *
 * @author 邓单
 * @since 2026-07-12
 * @see HouseDao
 * @see House
 */
public class HouseService {

    private final HouseDao houseDao;
    private final BuildingDao buildingDao;

    /**
     * 创建房屋业务服务。
     *
     * @param houseDao    房屋数据访问
     * @param buildingDao 楼盘数据访问（校验楼盘存在）
     */
    public HouseService(HouseDao houseDao, BuildingDao buildingDao) {
        this.houseDao = Objects.requireNonNull(houseDao, "houseDao 不能为空");
        this.buildingDao = Objects.requireNonNull(buildingDao, "buildingDao 不能为空");
    }

    /**
     * 新增房屋，自动计算总价 {@code area × unitPrice}。
     *
     * @param house 房屋实体
     * @throws IllegalArgumentException 参数不合法或楼盘不存在时
     * @throws IllegalStateException    数据库操作失败时
     */
    public void add(House house) {
        Objects.requireNonNull(house, "房屋不能为空");
        validateHouseFields(house);
        ensureBuildingExists(house.getBuildingId());
        house.setTotalPrice(calculateTotalPrice(house.getArea(), house.getUnitPrice()));
        if (house.getStatus() == null) {
            house.setStatus(HouseStatus.ON_SALE);
        }
        if (isBlank(house.getId())) {
            house.setId(IdGenerator.nextHouseId());
        }
        try {
            if (houseDao.insert(house) <= 0) {
                throw new IllegalStateException("新增房屋失败");
            }
        } catch (SQLException e) {
            throw mapSqlException(e);
        }
    }

    /**
     * 更新房屋，仅 {@link HouseStatus#ON_SALE} 可修改并重算总价。
     *
     * @param house 房屋实体
     * @throws IllegalArgumentException 参数不合法、不存在或已售出时
     * @throws IllegalStateException    数据库操作失败时
     */
    public void update(House house) {
        Objects.requireNonNull(house, "房屋不能为空");
        requireNonBlank(house.getId(), "房屋编号");
        validateHouseFields(house);
        try {
            House existing = houseDao.findById(house.getId());
            if (existing == null) {
                throw new IllegalArgumentException("房屋不存在：" + house.getId());
            }
            if (existing.getStatus() != HouseStatus.ON_SALE) {
                throw new IllegalArgumentException("仅在售房屋可修改");
            }
            ensureBuildingExists(house.getBuildingId());
            house.setTotalPrice(calculateTotalPrice(house.getArea(), house.getUnitPrice()));
            house.setStatus(HouseStatus.ON_SALE);
            if (houseDao.update(house) <= 0) {
                throw new IllegalStateException("更新房屋失败");
            }
        } catch (SQLException e) {
            throw mapSqlException(e);
        }
    }

    /**
     * 删除房屋，仅 {@link HouseStatus#ON_SALE} 可删除。
     *
     * @param houseId 房屋编号
     * @throws IllegalArgumentException 参数不合法、不存在或已售出时
     * @throws IllegalStateException    数据库操作失败时
     */
    public void delete(String houseId) {
        requireNonBlank(houseId, "房屋编号");
        try {
            House existing = houseDao.findById(houseId);
            if (existing == null) {
                throw new IllegalArgumentException("房屋不存在：" + houseId);
            }
            if (existing.getStatus() != HouseStatus.ON_SALE) {
                throw new IllegalArgumentException("仅在售房屋可删除");
            }
            if (houseDao.deleteById(houseId) <= 0) {
                throw new IllegalStateException("删除房屋失败");
            }
        } catch (SQLException e) {
            throw mapSqlException(e);
        }
    }

    /**
     * 根据编号查询房屋。
     *
     * @param id 房屋编号
     * @return 房屋实体，不存在返回 {@code null}
     * @throws IllegalArgumentException 编号为空时
     * @throws IllegalStateException    数据库操作失败时
     */
    public House getById(String id) {
        requireNonBlank(id, "房屋编号");
        try {
            return houseDao.findById(id);
        } catch (SQLException e) {
            throw mapSqlException(e);
        }
    }

    /**
     * 按楼盘查询房屋列表。
     *
     * @param buildingId 楼盘编号
     * @return 房屋列表
     * @throws IllegalArgumentException 楼盘编号为空时
     * @throws IllegalStateException    数据库操作失败时
     */
    public List<House> listByBuilding(String buildingId) {
        requireNonBlank(buildingId, "楼盘编号");
        try {
            return houseDao.findByBuildingId(buildingId);
        } catch (SQLException e) {
            throw mapSqlException(e);
        }
    }

    /**
     * 查询全部房屋列表。
     *
     * @return 房屋列表，无数据时返回空列表
     * @throws IllegalStateException 数据库操作失败时
     */
    public List<House> listAll() {
        try {
            return houseDao.findAll();
        } catch (SQLException e) {
            throw mapSqlException(e);
        }
    }

    private void ensureBuildingExists(String buildingId) {
        try {
            if (buildingDao.findById(buildingId) == null) {
                throw new IllegalArgumentException("所属楼盘不存在：" + buildingId);
            }
        } catch (SQLException e) {
            throw mapSqlException(e);
        }
    }

    private void validateHouseFields(House house) {
        requireNonBlank(house.getBuildingId(), "所属楼盘");
        requireNonBlank(house.getBuildingNo(), "楼号");
        requireNonBlank(house.getRoomNo(), "房号");
        requirePositive(house.getArea(), "面积");
        requirePositive(house.getUnitPrice(), "单价");
    }

    private static BigDecimal calculateTotalPrice(BigDecimal area, BigDecimal unitPrice) {
        return area.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
    }

    private static RuntimeException mapSqlException(SQLException e) {
        String message = e.getMessage();
        if (message != null && message.contains("uk_building_room")) {
            return new IllegalArgumentException("同楼盘下楼号与房号不可重复");
        }
        return new IllegalStateException("数据库操作失败：" + e.getMessage(), e);
    }

    private static void requireNonBlank(String value, String fieldName) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
    }

    private static void requirePositive(BigDecimal value, String fieldName) {
        Objects.requireNonNull(value, fieldName + "不能为空");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(fieldName + "必须大于 0");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
