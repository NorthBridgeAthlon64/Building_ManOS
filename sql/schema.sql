-- Building_ManOS 数据库结构（骨架，技术组完善）
-- @author 技术组
-- @date 2026-07-10

CREATE DATABASE IF NOT EXISTS building_manos
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE building_manos;

-- 楼盘表
CREATE TABLE IF NOT EXISTS building (
    id          VARCHAR(32)  PRIMARY KEY COMMENT '楼盘编号',
    name        VARCHAR(100) NOT NULL COMMENT '楼盘名称',
    land_area   DECIMAL(12,2) NOT NULL COMMENT '占地面积(㎡)',
    address     VARCHAR(200) NOT NULL COMMENT '地址',
    developer   VARCHAR(100) COMMENT '开发商',
    remark      VARCHAR(500) COMMENT '备注',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP
) COMMENT '楼盘信息';

-- 房屋表
CREATE TABLE IF NOT EXISTS house (
    id           VARCHAR(32)  PRIMARY KEY COMMENT '房屋编号',
    building_id  VARCHAR(32)  NOT NULL COMMENT '所属楼盘',
    building_no  VARCHAR(20)  NOT NULL COMMENT '楼号',
    room_no      VARCHAR(20)  NOT NULL COMMENT '房号',
    area         DECIMAL(10,2) NOT NULL COMMENT '面积(㎡)',
    unit_price   DECIMAL(12,2) NOT NULL COMMENT '单价(元/㎡)',
    total_price  DECIMAL(14,2) NOT NULL COMMENT '总价',
    status       VARCHAR(20)  NOT NULL DEFAULT 'ON_SALE' COMMENT 'ON_SALE/SOLD',
    sold_at      DATETIME     NULL COMMENT '售出时间',
    CONSTRAINT fk_house_building FOREIGN KEY (building_id) REFERENCES building(id),
    UNIQUE KEY uk_building_room (building_id, building_no, room_no)
) COMMENT '房屋信息';

-- 成交记录表
CREATE TABLE IF NOT EXISTS sale_record (
    id              VARCHAR(32)  PRIMARY KEY,
    house_id        VARCHAR(32)  NOT NULL,
    original_price  DECIMAL(14,2) NOT NULL COMMENT '原价',
    discount_type   VARCHAR(50)  COMMENT '折扣类型',
    discount_value  DECIMAL(10,4) COMMENT '折扣参数',
    final_price     DECIMAL(14,2) NOT NULL COMMENT '实付金额',
    customer_name   VARCHAR(50)  COMMENT '客户姓名',
    sold_at         DATETIME     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sale_house FOREIGN KEY (house_id) REFERENCES house(id),
    UNIQUE KEY uk_sale_house (house_id)
) COMMENT '销售成交记录';
