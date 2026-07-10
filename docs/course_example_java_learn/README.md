# 课堂 Java 示例代码

本目录为课程上学习的 Java 知识点与示例，**供 Building ManOS 技术组参考**。

> **编码**：已全部转换为 **UTF-8**。若新增文件来自课堂（GBK/GB2312），可运行：
> `python scripts/convert-course-examples-to-utf8.py`

## 与本项目的对照文档

👉 **[课堂知识点对照.md](../../design/课堂知识点对照.md)** — 说明哪些示例用于本项目、如何映射。

## 重点参考（按学习顺序）

| 顺序 | 目录/文件 | 学什么 |
|------|-----------|--------|
| 1 | `mysql安装.txt` | MySQL 安装、建库、导入导出 |
| 2 | `JdbcTest.java` | JDBC 最简连接查询 |
| 3 | `JDBC代码/代码/1~7` | 连接 → CRUD → PreparedStatement |
| 4 | `鲜花商店代码/` | **分层架构 + 控制台 + dao/service**（最接近大作业） |
| 5 | `异常处理代码/` | try-catch-finally、自定义异常 |
| 6 | `assignment.sql` | SQL 建表与数据示例 |

## 不必用于本项目

- `bcalc/`、`ncalc/` — 网络编程
- `excel1/`、`excel2/` — Excel 操作
- `lesson6.txt` 多线程 — 本期控制台单线程即可

## 注意

课堂鲜花商店 `Main.java` 中部分代码直接调用 Dao；本项目要求 **cli 只调 service**，见 [Java技术框架.md](../../design/Java技术框架.md)。
