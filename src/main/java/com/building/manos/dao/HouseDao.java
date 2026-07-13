package com.building.manos.dao;

import com.building.manos.config.DBConfig;
import com.building.manos.model.House;
import com.building.manos.model.HouseStatus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 房屋数据访问对象，负责 {@code house} 表的增删改查和条件查询。
 *
 * @author 陈辉
 * @since 2026-07-11
 * @see House
 * @see DBConfig
 */
public class HouseDao {

    private static final String COLUMNS = "id, building_id, building_no, room_no, "
            + "area, unit_price, total_price, status, sold_at";

    /**
     * 创建房屋数据访问对象。
     */
    public HouseDao() {
    }

    /**
     * 新增房屋记录。
     *
     * @param house 待保存的房屋，非 {@code null}
     * @return 受影响行数
     * @throws SQLException 数据库访问失败时
     */
    public int insert(House house) throws SQLException {
        String sql = "INSERT INTO house (id, building_id, building_no, room_no, area, "
                + "unit_price, total_price, status, sold_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindHouse(statement, house);
            return statement.executeUpdate();
        }
    }

    /**
     * 根据主键查询房屋。
     *
     * @param id 房屋主键，非空
     * @return 找到时返回房屋，否则返回 {@code null}
     * @throws SQLException 数据库访问失败时
     */
    public House findById(String id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM house WHERE id = ?";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapRow(resultSet) : null;
            }
        }
    }

    /**
     * 更新指定房屋的非主键字段。
     *
     * @param house 包含最新字段和主键的房屋，非 {@code null}
     * @return 受影响行数
     * @throws SQLException 数据库访问失败时
     */
    public int update(House house) throws SQLException {
        String sql = "UPDATE house SET building_id = ?, building_no = ?, room_no = ?, "
                + "area = ?, unit_price = ?, total_price = ?, status = ?, sold_at = ? "
                + "WHERE id = ? AND status = 'ON_SALE'";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, house.getBuildingId());
            statement.setString(2, house.getBuildingNo());
            statement.setString(3, house.getRoomNo());
            statement.setBigDecimal(4, house.getArea());
            statement.setBigDecimal(5, house.getUnitPrice());
            statement.setBigDecimal(6, house.getTotalPrice());
            statement.setString(7, house.getStatus().name());
            setTimestamp(statement, 8, house.getSoldAt());
            statement.setString(9, house.getId());
            return statement.executeUpdate();
        }
    }

    /**
     * 删除指定的在售房屋。
     *
     * @param id 房屋主键，非空
     * @return 受影响行数；房屋不存在或已售时返回 0
     * @throws SQLException 数据库访问失败时
     */
    public int deleteById(String id) throws SQLException {
        String sql = "DELETE FROM house WHERE id = ? AND status = 'ON_SALE'";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            return statement.executeUpdate();
        }
    }

    /**
     * 统计指定楼盘下的房屋数量。
     *
     * @param buildingId 楼盘主键，非空
     * @return 房屋数量
     * @throws SQLException 数据库访问失败时
     */
    public int countByBuildingId(String buildingId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM house WHERE building_id = ?";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, buildingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    /**
     * 查询指定楼盘下的全部房屋。
     *
     * @param buildingId 楼盘主键，非空
     * @return 房屋列表，无结果时返回空列表
     * @throws SQLException 数据库访问失败时
     */
    public List<House> findByBuildingId(String buildingId) throws SQLException {
        String sql = "SELECT " + COLUMNS
                + " FROM house WHERE building_id = ? ORDER BY building_no, room_no";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, buildingId);
            return executeListQuery(statement);
        }
    }

    /**
     * 按楼盘名称模糊查询房屋。
     *
     * @param namePattern 已包含通配符的楼盘名称模式，非空
     * @return 匹配的房屋列表，无结果时返回空列表
     * @throws SQLException 数据库访问失败时
     */
    public List<House> findByBuildingNameLike(String namePattern) throws SQLException {
        String sql = "SELECT " + prefixedColumns("h") + " FROM house h "
                + "INNER JOIN building b ON h.building_id = b.id "
                + "WHERE b.name LIKE ? ORDER BY h.total_price, h.id";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, namePattern);
            return executeListQuery(statement);
        }
    }

    /**
     * 按楼号查询房屋。
     *
     * @param buildingNo 楼号，非空
     * @return 匹配的房屋列表，无结果时返回空列表
     * @throws SQLException 数据库访问失败时
     */
    public List<House> findByBuildingNo(String buildingNo) throws SQLException {
        String sql = "SELECT " + COLUMNS
                + " FROM house WHERE building_no = ? ORDER BY total_price, id";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, buildingNo);
            return executeListQuery(statement);
        }
    }

    /**
     * 按销售状态查询房屋。
     *
     * @param status 销售状态，非 {@code null}
     * @return 匹配的房屋列表，无结果时返回空列表
     * @throws SQLException 数据库访问失败时
     */
    public List<House> findByStatus(HouseStatus status) throws SQLException {
        String sql = "SELECT " + COLUMNS
                + " FROM house WHERE status = ? ORDER BY total_price, id";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            return executeListQuery(statement);
        }
    }

    /**
     * 按总价区间和销售状态查询房屋。
     * <p>区间上下界和状态均可为空，空值表示不添加对应查询条件。</p>
     *
     * @param min 最低总价，单位：元，可为 {@code null}
     * @param max 最高总价，单位：元，可为 {@code null}
     * @param status 销售状态，可为 {@code null}
     * @return 房屋列表，无结果时返回空列表
     * @throws SQLException 数据库访问失败时
     */
    public List<House> findByPriceRange(BigDecimal min, BigDecimal max,
                                        HouseStatus status) throws SQLException {
        return findByRange("total_price", min, max, status);
    }

    /**
     * 按面积区间和销售状态查询房屋。
     * <p>区间上下界和状态均可为空，空值表示不添加对应查询条件。</p>
     *
     * @param min 最小面积，单位：平方米，可为 {@code null}
     * @param max 最大面积，单位：平方米，可为 {@code null}
     * @param status 销售状态，可为 {@code null}
     * @return 房屋列表，无结果时返回空列表
     * @throws SQLException 数据库访问失败时
     */
    public List<House> findByAreaRange(BigDecimal min, BigDecimal max,
                                       HouseStatus status) throws SQLException {
        return findByRange("area", min, max, status);
    }

    /**
     * 将指定在售房屋原子更新为已售。
     *
     * @param houseId 房屋主键，非空
     * @param soldAt 售出时间，非空
     * @return 受影响行数；房屋不存在或已售时返回 0
     * @throws SQLException 数据库访问失败时
     */
    public int updateStatusSold(String houseId, LocalDateTime soldAt) throws SQLException {
        try (Connection connection = DBConfig.getConnection()) {
            return updateStatusSold(connection, houseId, soldAt);
        }
    }

    /**
     * 使用调用方提供的连接将指定在售房屋原子更新为已售。
     * <p>本方法不关闭连接，由购买业务负责提交、回滚和关闭。</p>
     *
     * @param connection 购买事务使用的数据库连接，非 {@code null}
     * @param houseId 房屋主键，非空
     * @param soldAt 售出时间，非空
     * @return 受影响行数；房屋不存在或已售时返回 0
     * @throws SQLException 数据库访问失败时
     */
    public int updateStatusSold(Connection connection, String houseId,
                                LocalDateTime soldAt) throws SQLException {
        String sql = "UPDATE house SET status = 'SOLD', sold_at = ? "
                + "WHERE id = ? AND status = 'ON_SALE'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(soldAt));
            statement.setString(2, houseId);
            return statement.executeUpdate();
        }
    }

    private List<House> findByRange(String column, BigDecimal min, BigDecimal max,
                                    HouseStatus status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT ").append(COLUMNS).append(" FROM house WHERE 1 = 1");
        List<Object> parameters = new ArrayList<>();
        if (min != null) {
            sql.append(" AND ").append(column).append(" >= ?");
            parameters.add(min);
        }
        if (max != null) {
            sql.append(" AND ").append(column).append(" <= ?");
            parameters.add(max);
        }
        if (status != null) {
            sql.append(" AND status = ?");
            parameters.add(status.name());
        }
        sql.append(" ORDER BY total_price, id");

        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int index = 0; index < parameters.size(); index++) {
                statement.setObject(index + 1, parameters.get(index));
            }
            return executeListQuery(statement);
        }
    }

    private String prefixedColumns(String alias) {
        return alias + ".id, " + alias + ".building_id, " + alias + ".building_no, "
                + alias + ".room_no, " + alias + ".area, " + alias + ".unit_price, "
                + alias + ".total_price, " + alias + ".status, " + alias + ".sold_at";
    }

    private List<House> executeListQuery(PreparedStatement statement) throws SQLException {
        List<House> houses = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                houses.add(mapRow(resultSet));
            }
        }
        return houses;
    }

    private House mapRow(ResultSet resultSet) throws SQLException {
        Timestamp soldAt = resultSet.getTimestamp("sold_at");
        return new House(
                resultSet.getString("id"),
                resultSet.getString("building_id"),
                resultSet.getString("building_no"),
                resultSet.getString("room_no"),
                resultSet.getBigDecimal("area"),
                resultSet.getBigDecimal("unit_price"),
                resultSet.getBigDecimal("total_price"),
                HouseStatus.valueOf(resultSet.getString("status")),
                soldAt == null ? null : soldAt.toLocalDateTime());
    }

    private void bindHouse(PreparedStatement statement, House house) throws SQLException {
        statement.setString(1, house.getId());
        statement.setString(2, house.getBuildingId());
        statement.setString(3, house.getBuildingNo());
        statement.setString(4, house.getRoomNo());
        statement.setBigDecimal(5, house.getArea());
        statement.setBigDecimal(6, house.getUnitPrice());
        statement.setBigDecimal(7, house.getTotalPrice());
        statement.setString(8, house.getStatus().name());
        setTimestamp(statement, 9, house.getSoldAt());
    }

    private void setTimestamp(PreparedStatement statement, int index,
                              LocalDateTime value) throws SQLException {
        statement.setTimestamp(index, value == null ? null : Timestamp.valueOf(value));
    }
}
