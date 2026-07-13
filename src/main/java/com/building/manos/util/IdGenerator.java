package com.building.manos.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 业务主键生成器，按表类型生成带前缀的时间序列编号。
 *
 * @author 陈辉
 * @since 2026-07-12
 */
public final class IdGenerator {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static String lastTimestamp = "";
    private static int sequence;

    private IdGenerator() {
    }

    /**
     * 生成楼盘主键。
     *
     * @return 以 {@code B} 开头的楼盘主键
     */
    public static String nextBuildingId() {
        return nextId("B");
    }

    /**
     * 生成房屋主键。
     *
     * @return 以 {@code H} 开头的房屋主键
     */
    public static String nextHouseId() {
        return nextId("H");
    }

    /**
     * 生成成交记录主键。
     *
     * @return 以 {@code S} 开头的成交记录主键
     */
    public static String nextSaleId() {
        return nextId("S");
    }

    private static synchronized String nextId(String prefix) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        if (timestamp.equals(lastTimestamp)) {
            sequence = (sequence + 1) % 1000;
        } else {
            lastTimestamp = timestamp;
            sequence = 0;
        }
        return prefix + timestamp + String.format("%03d", sequence);
    }
}
