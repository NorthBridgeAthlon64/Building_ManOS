package com.building.manos.service;

import com.building.manos.dao.SaleRecordDao;
import com.building.manos.model.SaleRecord;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * 成交记录业务服务，负责查询历史销售记录。
 * <p>
 * 由 {@link com.building.manos.cli.MenuController} 调用，
 * 通过 {@link SaleRecordDao} 访问数据库。
 * </p>
 *
 * @author 邓单
 * @since 2026-07-13
 * @see SaleRecordDao
 * @see SaleRecord
 */
public class SaleRecordService {

    private final SaleRecordDao saleRecordDao;

    /**
     * 创建成交记录业务服务。
     *
     * @param saleRecordDao 成交记录数据访问
     */
    public SaleRecordService(SaleRecordDao saleRecordDao) {
        this.saleRecordDao = Objects.requireNonNull(saleRecordDao, "saleRecordDao 不能为空");
    }

    /**
     * 查询全部成交记录，按成交时间降序排列。
     *
     * @return 成交记录列表，无数据时返回空列表
     * @throws IllegalStateException 数据库操作失败时
     */
    public List<SaleRecord> listAll() {
        try {
            return saleRecordDao.findAll();
        } catch (SQLException e) {
            throw new IllegalStateException("数据库操作失败：" + e.getMessage(), e);
        }
    }

    /**
     * 按房屋编号查询成交记录。
     *
     * @param houseId 房屋编号
     * @return 成交记录列表，无数据时返回空列表
     * @throws IllegalArgumentException 房屋编号为空时
     * @throws IllegalStateException    数据库操作失败时
     */
    public List<SaleRecord> listByHouseId(String houseId) {
        if (houseId == null || houseId.isBlank()) {
            throw new IllegalArgumentException("房屋编号不能为空");
        }
        try {
            return saleRecordDao.findByHouseId(houseId.trim());
        } catch (SQLException e) {
            throw new IllegalStateException("数据库操作失败：" + e.getMessage(), e);
        }
    }
}
