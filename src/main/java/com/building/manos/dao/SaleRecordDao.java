package com.building.manos.dao;

import com.building.manos.config.DBConfig;
import com.building.manos.model.SaleRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 成交记录数据访问对象，负责 {@code sale_record} 表的写入和查询。
 *
 * @author 陈辉
 * @since 2026-07-12
 * @see SaleRecord
 */
public class SaleRecordDao {

    private static final String COLUMNS = "id, house_id, original_price, discount_type, "
            + "discount_value, final_price, customer_name, sold_at";

    /**
     * 创建成交记录数据访问对象。
     */
    public SaleRecordDao() {
    }

    /**
     * 使用独立连接写入成交记录。
     *
     * @param record 成交记录，非 {@code null}
     * @return 受影响行数
     * @throws SQLException 数据库访问失败时
     */
    public int insert(SaleRecord record) throws SQLException {
        try (Connection connection = DBConfig.getConnection()) {
            return insert(connection, record);
        }
    }

    /**
     * 使用调用方提供的事务连接写入成交记录。
     * <p>本方法不关闭连接，连接生命周期由调用方负责。</p>
     *
     * @param connection 事务连接，非 {@code null}
     * @param record 成交记录，非 {@code null}
     * @return 受影响行数
     * @throws SQLException 数据库访问失败时
     */
    public int insert(Connection connection, SaleRecord record) throws SQLException {
        String sql = "INSERT INTO sale_record (id, house_id, original_price, discount_type, "
                + "discount_value, final_price, customer_name, sold_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, record.getId());
            statement.setString(2, record.getHouseId());
            statement.setBigDecimal(3, record.getOriginalPrice());
            statement.setString(4, record.getDiscountType());
            statement.setBigDecimal(5, record.getDiscountValue());
            statement.setBigDecimal(6, record.getFinalPrice());
            statement.setString(7, record.getCustomerName());
            statement.setTimestamp(8, record.getSoldAt() == null
                    ? null : Timestamp.valueOf(record.getSoldAt()));
            return statement.executeUpdate();
        }
    }

    /**
     * 查询全部成交记录，按成交时间降序排列。
     *
     * @return 成交记录列表，无结果时返回空列表
     * @throws SQLException 数据库访问失败时
     */
    public List<SaleRecord> findAll() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM sale_record ORDER BY sold_at DESC";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            return executeListQuery(statement);
        }
    }

    /**
     * 查询指定房屋的成交记录。
     *
     * @param houseId 房屋主键，非空
     * @return 成交记录列表，无结果时返回空列表
     * @throws SQLException 数据库访问失败时
     */
    public List<SaleRecord> findByHouseId(String houseId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM sale_record WHERE house_id = ? "
                + "ORDER BY sold_at DESC";
        try (Connection connection = DBConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, houseId);
            return executeListQuery(statement);
        }
    }

    private List<SaleRecord> executeListQuery(PreparedStatement statement) throws SQLException {
        List<SaleRecord> records = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                records.add(mapRow(resultSet));
            }
        }
        return records;
    }

    private SaleRecord mapRow(ResultSet resultSet) throws SQLException {
        Timestamp soldAt = resultSet.getTimestamp("sold_at");
        return new SaleRecord(
                resultSet.getString("id"),
                resultSet.getString("house_id"),
                resultSet.getBigDecimal("original_price"),
                resultSet.getString("discount_type"),
                resultSet.getBigDecimal("discount_value"),
                resultSet.getBigDecimal("final_price"),
                resultSet.getString("customer_name"),
                soldAt == null ? null : soldAt.toLocalDateTime());
    }
}
