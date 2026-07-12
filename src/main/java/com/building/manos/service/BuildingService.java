package com.building.manos.service;

import com.building.manos.dao.BuildingDao;
import com.building.manos.dao.HouseDao;
import com.building.manos.model.Building;
import com.building.manos.util.IdGenerator;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * 楼盘业务服务，负责楼盘增删改查及删除前关联校验。
 * <p>
 * 由 {@link com.building.manos.cli.MenuController} 调用，
 * 通过 {@link BuildingDao}、{@link HouseDao} 访问数据库。
 * </p>
 *
 * @author 邓单
 * @since 2026-07-12
 * @see BuildingDao
 * @see Building
 */
public class BuildingService {

    private final BuildingDao buildingDao;
    private final HouseDao houseDao;

    /**
     * 创建楼盘业务服务。
     *
     * @param buildingDao 楼盘数据访问
     * @param houseDao    房屋数据访问（删除前统计房屋数）
     */
    public BuildingService(BuildingDao buildingDao, HouseDao houseDao) {
        this.buildingDao = Objects.requireNonNull(buildingDao, "buildingDao 不能为空");
        this.houseDao = Objects.requireNonNull(houseDao, "houseDao 不能为空");
    }

    /**
     * 新增楼盘。
     * <p>
     * 校验必填字段后插入；若 id 为空则自动生成。
     * </p>
     *
     * @param building 楼盘实体
     * @throws IllegalArgumentException 参数不合法时
     * @throws IllegalStateException    数据库操作失败时
     */
    public void add(Building building) {
        Objects.requireNonNull(building, "楼盘不能为空");
        validateBuildingFields(building);
        if (isBlank(building.getId())) {
            building.setId(IdGenerator.nextBuildingId());
        }
        try {
            if (buildingDao.insert(building) <= 0) {
                throw new IllegalStateException("新增楼盘失败");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("数据库操作失败：" + e.getMessage(), e);
        }
    }

    /**
     * 更新楼盘信息。
     *
     * @param building 楼盘实体，id 非空且须已存在
     * @throws IllegalArgumentException 参数不合法或记录不存在时
     * @throws IllegalStateException    数据库操作失败时
     */
    public void update(Building building) {
        Objects.requireNonNull(building, "楼盘不能为空");
        requireNonBlank(building.getId(), "楼盘编号");
        validateBuildingFields(building);
        try {
            if (buildingDao.findById(building.getId()) == null) {
                throw new IllegalArgumentException("楼盘不存在：" + building.getId());
            }
            if (buildingDao.update(building) <= 0) {
                throw new IllegalStateException("更新楼盘失败");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("数据库操作失败：" + e.getMessage(), e);
        }
    }

    /**
     * 删除指定楼盘。若楼盘下仍有房屋则拒绝删除。
     * <p>
     * 先 {@link HouseDao#countByBuildingId(String)} 统计房屋数，为 0 时再删除。
     * </p>
     *
     * @param buildingId 楼盘编号
     * @throws IllegalArgumentException 参数不合法、记录不存在或仍有房屋时
     * @throws IllegalStateException    数据库操作失败时
     */
    public void delete(String buildingId) {
        requireNonBlank(buildingId, "楼盘编号");
        try {
            if (buildingDao.findById(buildingId) == null) {
                throw new IllegalArgumentException("楼盘不存在：" + buildingId);
            }
            if (houseDao.countByBuildingId(buildingId) > 0) {
                throw new IllegalArgumentException("楼盘下仍有房屋，无法删除");
            }
            if (buildingDao.deleteById(buildingId) <= 0) {
                throw new IllegalStateException("删除楼盘失败");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("数据库操作失败：" + e.getMessage(), e);
        }
    }

    /**
     * 根据编号查询楼盘。
     *
     * @param id 楼盘编号
     * @return 楼盘实体，不存在返回 {@code null}
     * @throws IllegalArgumentException 编号为空时
     * @throws IllegalStateException    数据库操作失败时
     */
    public Building getById(String id) {
        requireNonBlank(id, "楼盘编号");
        try {
            return buildingDao.findById(id);
        } catch (SQLException e) {
            throw new IllegalStateException("数据库操作失败：" + e.getMessage(), e);
        }
    }

    /**
     * 查询全部楼盘列表。
     *
     * @return 楼盘列表，无数据时返回空列表
     * @throws IllegalStateException 数据库操作失败时
     */
    public List<Building> listAll() {
        try {
            return buildingDao.findAll();
        } catch (SQLException e) {
            throw new IllegalStateException("数据库操作失败：" + e.getMessage(), e);
        }
    }

    private void validateBuildingFields(Building building) {
        requireNonBlank(building.getName(), "楼盘名称");
        requireNonBlank(building.getAddress(), "地址");
        requirePositive(building.getLandArea(), "占地面积");
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
