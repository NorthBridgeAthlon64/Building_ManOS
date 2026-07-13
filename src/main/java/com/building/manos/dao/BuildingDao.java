package com.building.manos.dao;

import com.building.manos.config.DBConfig;
import com.building.manos.model.Building;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 楼盘数据访问对象，负责 {@code building} 表的增删改查。
 *
 * @author 陈辉
 * @since 2026-07-11
 * @see Building
 * @see DBConfig
 */
public class BuildingDao {

    private static final String COLUMNS =
            "id, name, land_area, address, developer, remark, created_at";

    /**
     * 创建楼盘数据访问对象。
     */
    public BuildingDao() {
    }

    /**
     * 新增楼盘记录。
     *
     * @param building 待保存的楼盘，非 {@code null}
     * @return 受影响行数
     * @throws SQLException 数据库访问失败时
     */
    public int insert(Building building) throws SQLException {
        String sql = "INSERT INTO building "
                + "(id, name, land_area, address, developer, remark, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, COALESCE(?, CURRENT_TIMESTAMP))";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, building.getId());
            statement.setString(2, building.getName());
            statement.setBigDecimal(3, building.getLandArea());
            statement.setString(4, building.getAddress());
            statement.setString(5, building.getDeveloper());
            statement.setString(6, building.getRemark());
            setTimestamp(statement, 7, building.getCreatedAt());
            return statement.executeUpdate();
        }
    }

    /**
     * 根据主键查询楼盘。
     *
     * @param id 楼盘主键，非空
     * @return 找到时返回楼盘，否则返回 {@code null}
     * @throws SQLException 数据库访问失败时
     */
    public Building findById(String id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM building WHERE id = ?";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    /**
     * 查询全部楼盘，按创建时间降序排列。
     *
     * @return 楼盘列表，无结果时返回空列表
     * @throws SQLException 数据库访问失败时
     */
    public List<Building> findAll() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM building ORDER BY created_at DESC";
        List<Building> buildings = new ArrayList<>();
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                buildings.add(mapRow(resultSet));
            }
        }
        return buildings;
    }

    /**
     * 更新指定楼盘的非主键字段。
     *
     * @param building 包含最新字段和主键的楼盘，非 {@code null}
     * @return 受影响行数
     * @throws SQLException 数据库访问失败时
     */
    public int update(Building building) throws SQLException {
        String sql = "UPDATE building SET name = ?, land_area = ?, address = ?, "
                + "developer = ?, remark = ? WHERE id = ?";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, building.getName());
            statement.setBigDecimal(2, building.getLandArea());
            statement.setString(3, building.getAddress());
            statement.setString(4, building.getDeveloper());
            statement.setString(5, building.getRemark());
            statement.setString(6, building.getId());
            return statement.executeUpdate();
        }
    }

    /**
     * 根据主键删除楼盘。
     * <p>关联房屋检查由业务层负责，数据库外键提供最终保护。</p>
     *
     * @param id 楼盘主键，非空
     * @return 受影响行数
     * @throws SQLException 数据库访问失败或外键约束阻止删除时
     */
    public int deleteById(String id) throws SQLException {
        String sql = "DELETE FROM building WHERE id = ?";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            return statement.executeUpdate();
        }
    }

    private Building mapRow(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        return new Building(
                resultSet.getString("id"),
                resultSet.getString("name"),
                resultSet.getBigDecimal("land_area"),
                resultSet.getString("address"),
                resultSet.getString("developer"),
                resultSet.getString("remark"),
                createdAt == null ? null : createdAt.toLocalDateTime());
    }

    private void setTimestamp(PreparedStatement statement, int index,
                              java.time.LocalDateTime value) throws SQLException {
        statement.setTimestamp(index, value == null ? null : Timestamp.valueOf(value));
    }
}
