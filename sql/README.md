# 数据库脚本（技术组）

## 设计文档

完整说明见 **[docs/design/数据库设计.md](../docs/design/数据库设计.md)**（E-R、字段、DAO-SQL 对照、事务约定）。

## 数据库选型

| 项 | 规定 |
|----|------|
| 数据库 | MySQL 8.0+ |
| 库名 | `building_manos` |
| 访问 | JDBC + `dao` 层，`config/DBConfig.java` |

## 文件说明

| 文件 | 用途 |
|------|------|
| `schema.sql` | 建库、建表、索引、外键 |
| `init-data.sql` | 答辩演示用初始数据 |

## 执行顺序

```bash
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/init-data.sql
```

## 表一览

| 表 | 说明 |
|----|------|
| `building` | 楼盘 |
| `house` | 房屋 |
| `sale_record` | 成交记录 |
