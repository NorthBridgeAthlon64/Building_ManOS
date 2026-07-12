# 开发 A 数据层实施计划

> 文档性质：开发 A 主工程文档
> 项目：房地产公司房屋销售管理系统（Building ManOS）
> 负责人：待填写
> 学号：待填写
> 当前版本：v0.2
> 制定日期：2026-07-11  |  更新日期：2026-07-12
> 状态：P0 已实现，真实数据库联调待环境验证

---

## 1. 文档目标

本文档用于统一开发 A 的职责边界、实现顺序、接口契约、数据库映射、测试范围和交付标准，作为开发、联调、代码评审及课程报告个人贡献说明的依据。

开发 A 的核心目标是提供稳定的数据访问基础，使业务层能够通过明确的 Java 接口完成楼盘和房屋数据的持久化，不接触 JDBC、SQL 或数据库连接细节。

关联文档：

- [项目计划](../../项目计划.md)
- [Java 技术框架](../design/Java技术框架.md)
- [数据库设计](../design/数据库设计.md)
- [数据字典](../requirements/数据字典.md)
- [代码注释规范](../design/代码注释规范.md)
- [团队分工表](../report/团队分工表.md)
- [数据库建表脚本](../../sql/schema.sql)

---

## 2. 职责范围

### 2.1 负责内容

开发 A 按《项目计划》的角色定义负责以下模块：

| 模块 | 文件或包 | 主要职责 |
|------|----------|----------|
| 数据库配置 | `config/DBConfig.java` | 加载配置并创建 JDBC 连接 |
| 数据模型 | `model/Building.java` | 楼盘实体 |
| 数据模型 | `model/House.java` | 房屋实体 |
| 数据模型 | `model/HouseStatus.java` | 房屋状态枚举 |
| 数据模型 | `model/SaleRecord.java` | 成交记录实体 |
| 楼盘数据访问 | `dao/BuildingDao.java` | 楼盘 CRUD |
| 房屋数据访问 | `dao/HouseDao.java` | 房屋 CRUD、条件查询和售出状态更新 |
| 成交数据访问 | `dao/SaleRecordDao.java` | 成交记录写入、查询及事务连接重载 |
| 主键工具 | `util/IdGenerator.java` | 生成 B/H/S 前缀业务主键 |
| 数据库联调 | `sql/schema.sql`、`sql/init-data.sql` | 校验表结构、约束和演示数据与 DAO 一致 |
| 数据层测试 | `src/test/java/.../config`、`dao` | 数据库连接和 DAO 集成测试 |

### 2.2 不负责内容

开发 A 不直接实现以下业务，但需要提供稳定接口支持：

- `service` 中的参数校验、总价计算、删除前业务检查和购买流程；
- `discount` 中的折扣算法；
- `cli` 中的菜单、输入输出和异常提示；
- 购买事务的业务编排；`SaleRecordDao` 已因远端 `PurchaseService` 对接纳入开发 A；
- Web、GUI、连接池、ORM 等当前需求未要求的扩展。

### 2.3 待确认的分工冲突

现有文档存在以下不一致：

- 《项目计划》规定开发 A 负责 `model`、楼盘/房屋 `dao` 和 `config`；
- 《Java 技术框架》7.1 的分支归属建议将 `House`、`HouseStatus`、`HouseDao` 分配给开发 B。

本计划暂以《项目计划》为准，由开发 A 统一负责全部 `model`、`BuildingDao` 和 `HouseDao`。团队评审后应同步修改《Java 技术框架》和《团队分工表》，避免重复开发。

---

## 3. 技术与架构约束

### 3.1 固定技术栈

| 项目 | 规定 |
|------|------|
| Java | Java 17 |
| 构建工具 | Maven |
| 数据库 | MySQL 8.0+ |
| 数据访问 | JDBC + `PreparedStatement` |
| 测试 | JUnit 5 |
| 根包 | `com.building.manos` |

### 3.2 分层规则

调用方向固定为：

```text
cli -> service -> dao -> MySQL
```

- `model` 只承载数据，不包含数据库访问和业务流程；
- `dao` 只执行 SQL、参数绑定和结果映射，不计算价格或折扣；
- `config` 只负责连接配置，不演变为通用 CRUD 工具；
- DAO 必须使用 `PreparedStatement`，禁止拼接用户输入生成 SQL；
- JDBC 资源必须通过 try-with-resources 关闭。

---

## 4. 数据库连接方案

### 4.1 DBConfig 接口

```java
public final class DBConfig {
    public static Connection getConnection() throws SQLException;
}
```

`DBConfig` 不允许被实例化。数据库配置读取优先级为：

1. 环境变量 `DB_URL`、`DB_USER`、`DB_PASSWORD`；
2. classpath 下的 `database.properties`；
3. 项目约定的本地默认值。

### 4.2 配置键

| 配置键 | 环境变量 | 说明 |
|--------|----------|------|
| `driver` | 无 | MySQL JDBC 驱动类 |
| `url` | `DB_URL` | JDBC URL |
| `user` | `DB_USER` | 数据库用户名 |
| `password` | `DB_PASSWORD` | 数据库密码 |

### 4.3 安全要求

- 不在 Java 源码中硬编码真实生产密码；
- 提交前检查 `database.properties` 中是否包含个人敏感凭据；
- 连接失败时保留原始 `SQLException` 作为原因，便于定位；
- 不在控制台输出数据库密码或完整敏感连接信息。

---

## 5. Java 模型与数据库映射

### 5.1 Building

| Java 属性 | Java 类型 | 数据库列 | 可空 |
|-----------|-----------|----------|------|
| `id` | `String` | `building.id` | 否 |
| `name` | `String` | `building.name` | 否 |
| `landArea` | `BigDecimal` | `building.land_area` | 否 |
| `address` | `String` | `building.address` | 否 |
| `developer` | `String` | `building.developer` | 是 |
| `remark` | `String` | `building.remark` | 是 |
| `createdAt` | `LocalDateTime` | `building.created_at` | 是 |

### 5.2 House

| Java 属性 | Java 类型 | 数据库列 | 可空 |
|-----------|-----------|----------|------|
| `id` | `String` | `house.id` | 否 |
| `buildingId` | `String` | `house.building_id` | 否 |
| `buildingNo` | `String` | `house.building_no` | 否 |
| `roomNo` | `String` | `house.room_no` | 否 |
| `area` | `BigDecimal` | `house.area` | 否 |
| `unitPrice` | `BigDecimal` | `house.unit_price` | 否 |
| `totalPrice` | `BigDecimal` | `house.total_price` | 否 |
| `status` | `HouseStatus` | `house.status` | 否 |
| `soldAt` | `LocalDateTime` | `house.sold_at` | 是 |

### 5.3 SaleRecord

| Java 属性 | Java 类型 | 数据库列 | 可空 |
|-----------|-----------|----------|------|
| `id` | `String` | `sale_record.id` | 否 |
| `houseId` | `String` | `sale_record.house_id` | 否 |
| `originalPrice` | `BigDecimal` | `sale_record.original_price` | 否 |
| `discountType` | `String` | `sale_record.discount_type` | 是 |
| `discountValue` | `BigDecimal` | `sale_record.discount_value` | 是 |
| `finalPrice` | `BigDecimal` | `sale_record.final_price` | 否 |
| `customerName` | `String` | `sale_record.customer_name` | 是 |
| `soldAt` | `LocalDateTime` | `sale_record.sold_at` | 是 |

### 5.4 实体实现约定

- 金额和面积统一使用 `BigDecimal`；
- 日期时间统一使用 `LocalDateTime`；
- `HouseStatus` 仅包含 `ON_SALE` 和 `SOLD`；
- 数据库枚举通过 `status.name()` 写入，通过 `HouseStatus.valueOf(...)` 读取；
- 每个实体提供无参构造、必要的全参构造、getter/setter 和 `toString()`；
- 暂不引入 Lombok，避免增加非必要依赖；
- JavaDoc 的 `@author` 必须填写《团队分工表》中的真实姓名。

---

## 6. DAO 接口契约

### 6.1 通用约定

- 写操作返回受影响行数；
- 单条查询未命中时返回 `null`，在接口冻结后所有调用方统一遵守；
- 列表查询无结果时返回空列表，不返回 `null`；
- DAO 不吞掉 `SQLException`；
- `ResultSet` 到实体的转换集中在私有映射方法中；
- SQL 字段显式列出，不依赖 `SELECT *` 的列顺序；
- 排序规则按设计文档执行，保证演示输出稳定。

### 6.2 BuildingDao

| 方法 | 行为 | 关键约束 |
|------|------|----------|
| `insert(Building)` | 新增楼盘 | 使用参数绑定 |
| `findById(String)` | 按主键查询 | 未找到返回 `null` |
| `findAll()` | 查询全部楼盘 | 按 `created_at DESC` |
| `update(Building)` | 更新楼盘信息 | 不更新主键 |
| `deleteById(String)` | 删除楼盘 | 关联房屋检查由 Service 完成 |

### 6.3 HouseDao

| 方法 | 行为 | 关键约束 |
|------|------|----------|
| `insert(House)` | 新增房屋 | 保存 Service 已计算的总价 |
| `findById(String)` | 按主键查询 | 映射状态和可空售出时间 |
| `update(House)` | 更新房屋 | 是否允许更新由 Service 判断 |
| `deleteById(String)` | 删除房屋 | SQL 限制 `status = 'ON_SALE'` |
| `countByBuildingId(String)` | 统计楼盘下房屋 | 支持删除楼盘前检查 |
| `findByBuildingId(String)` | 查询楼盘下房屋 | 返回空列表而非 `null` |
| `findByPriceRange(...)` | 按总价和状态查询 | 明确空上下界处理方式 |
| `findByAreaRange(...)` | 按面积和状态查询 | 明确空上下界处理方式 |
| `updateStatusSold(...)` | 原子更新为已售 | SQL 限制原状态为 `ON_SALE` |

### 6.4 可选区间约定

价格和面积查询统一支持开放边界：

| `min` | `max` | 查询语义 |
|-------|-------|----------|
| 非空 | 非空 | `BETWEEN min AND max` |
| 非空 | 空 | `>= min` |
| 空 | 非空 | `<= max` |
| 空 | 空 | 不添加区间条件 |

状态参数非空时添加 `status = ?`；状态为空时不添加状态条件。动态 SQL 只能拼接固定 SQL 片段，所有数据值仍使用参数绑定。

---

## 7. 购买事务协作契约

购买操作必须在同一数据库事务内完成：

1. 将房屋从 `ON_SALE` 更新为 `SOLD`；
2. 插入一条 `sale_record`；
3. 两步均成功后提交，否则回滚。

开发 A 为事务调用提供以下重载：

```java
int updateStatusSold(
        Connection connection,
        String houseId,
        LocalDateTime soldAt) throws SQLException;
```

约定：

- 传入 `Connection` 的 DAO 方法不得关闭该连接；
- 事务的开启、提交和回滚由 `PurchaseService` 负责；
- 更新影响行数不是 1 时，业务层应视为房屋不存在或已售出；
- 非事务重载可自行获取并关闭连接；
- `SaleRecordDao` 应采用相同的 Connection 重载模式。

---

## 8. 实施计划

| 阶段 | 优先级 | 工作内容 | 交付物 | 验收标准 |
|------|--------|----------|--------|----------|
| A0 | P0 | 冻结职责与接口 | 评审记录、更新后的分工表 | A/B/C 对接口无冲突 |
| A1 | P0 | 实现 `DBConfig` | `DBConfig.java`、连通测试 | 能查询 `building` 表 |
| A2 | P0 | 实现四个模型类 | `model/*` | 字段和类型与数据字典一致 |
| A3 | P0 | 实现 `BuildingDao` | DAO 与测试 | 楼盘 CRUD 闭环通过 |
| A4 | P0 | 实现 `HouseDao` | DAO 与测试 | CRUD、区间、状态更新通过 |
| A5 | P1 | 校验 SQL 与演示数据 | 修订后的 SQL、验证记录 | 脚本可重复执行，约束有效 |
| A6 | P1 | 与 Service 层联调 | 问题清单、联调结果 | 楼盘和房屋三层链路跑通 |
| A7 | P1 | 注释和交付审查 | JavaDoc、验收矩阵 | 编译、测试、文档一致 |

推荐实现顺序：

```text
DBConfig
  -> Building / House / HouseStatus / SaleRecord
  -> BuildingDao
  -> HouseDao
  -> DAO 集成测试
  -> Service 联调
```

---

## 9. 测试与验收矩阵

### 9.1 自动化测试范围

建议测试目录：

```text
src/test/java/com/building/manos/
├── config/
│   └── DBConfigTest.java
└── dao/
    ├── BuildingDaoTest.java
    └── HouseDaoTest.java
```

| 编号 | 测试目标 | 预期结果 |
|------|----------|----------|
| DA-01 | 使用有效配置连接数据库 | 成功获得可用连接 |
| DA-02 | 使用错误配置连接数据库 | 抛出包含原因的异常 |
| DA-03 | 新增并查询楼盘 | 字段完整且一致 |
| DA-04 | 修改楼盘 | 影响 1 行，重新查询后生效 |
| DA-05 | 删除无房屋楼盘 | 影响 1 行 |
| DA-06 | 删除含房屋楼盘 | 被外键或业务层阻止 |
| DA-07 | 新增并查询房屋 | 金额、状态、外键映射正确 |
| DA-08 | 插入重复楼盘/楼号/房号 | 唯一约束拒绝写入 |
| DA-09 | 按楼盘查询房屋 | 仅返回目标楼盘数据 |
| DA-10 | 按价格区间查询 | 边界值和开放边界正确 |
| DA-11 | 按面积区间查询 | 边界值和开放边界正确 |
| DA-12 | 更新在售房屋为已售 | 影响 1 行，售出时间正确 |
| DA-13 | 重复售出同一房屋 | 第二次更新影响 0 行 |
| DA-14 | 删除已售房屋 | 影响 0 行 |
| DA-15 | 空结果查询 | 返回空列表 |

### 9.2 交付命令

```powershell
mvn clean test
mvn compile
```

涉及真实 MySQL 的集成测试执行前，必须确认数据库已启动并完成建表。测试数据应使用独立前缀或在测试结束后按测试创建顺序清理，禁止依赖个人数据库中的残留数据。

---

## 10. 数据库脚本验收

开发 A 应检查以下内容：

- `schema.sql` 可重复执行；
- 三张表均使用 InnoDB 和统一字符集；
- `house.building_id` 外键有效；
- `(building_id, building_no, room_no)` 唯一约束有效；
- `house.status` 默认值为 `ON_SALE`；
- DAO 使用的列名与脚本完全一致；
- 中文楼盘名、地址和客户姓名写入后无乱码；
- 评审是否为 `sale_record.house_id` 增加唯一约束，以落实一套房最多一条成交记录。

未经团队评审，不擅自修改现有表名、列名、状态值或主键格式。

---

## 11. 联调接口清单

### 11.1 向开发 B（Service/购买）提供

- `BuildingDao`、`HouseDao` 的稳定公开方法；
- DAO 的返回值和异常约定；
- `updateStatusSold(Connection, ...)` 事务重载；
- 模型构造方式和状态枚举；
- 测试数据库初始化方法。

### 11.2 向开发 C（CLI）说明

CLI 不直接调用开发 A 的 DAO。若 CLI 需要新查询，先由 Service 定义业务接口，再判断是否补充 DAO 查询方法。

### 11.3 接口变更规则

以下变更必须先通知受影响成员并更新本文档：

- DAO 公共方法签名；
- 实体字段或类型；
- 数据库表名、列名和约束；
- `null`、空列表和异常语义；
- 事务连接的所有权。

---

## 12. 风险与应对

| 风险 | 影响 | 应对措施 |
|------|------|----------|
| 开发 A/B 分工冲突 | 重复实现、合并冲突 | 开工前冻结职责并更新分工表 |
| 本机数据库配置不同 | 连接失败、演示中断 | 使用环境变量覆盖本地配置 |
| DAO 吞掉 SQL 异常 | 难以定位问题 | 保留异常原因并由上层统一提示 |
| 金额使用浮点数 | 金额精度错误 | 全部使用 `BigDecimal` |
| 状态更新无条件 | 同一房屋重复销售 | 更新 SQL 限制原状态为 `ON_SALE` |
| 购买两次写入不在同一事务 | 房屋状态与成交记录不一致 | 使用同一 Connection 提交或回滚 |
| SQL 与模型不同步 | 映射异常 | 维护映射表并执行集成测试 |
| 文档作者与源码不一致 | 分工不可追溯 | `@author` 与团队分工表统一 |

---

## 13. 完成定义（Definition of Done）

开发 A 的任务只有在以下条件全部满足时才算完成：

- [ ] 开发 A 的真实姓名和学号已写入团队分工表；
- [ ] 分工冲突已经团队确认并同步到相关文档；
- [ ] `DBConfig` 能通过环境变量或 properties 建立连接；
- [ ] 四个模型类与数据字典一致；
- [ ] `BuildingDao` 所有规定方法已实现；
- [ ] `HouseDao` 所有规定方法已实现；
- [ ] 所有 SQL 使用 `PreparedStatement`；
- [ ] JDBC 资源均能正确关闭；
- [ ] 购买事务所需的 Connection 重载已完成联调；
- [ ] DAO 集成测试覆盖正常、边界和失败场景；
- [ ] `mvn clean test` 和 `mvn compile` 通过；
- [ ] JavaDoc 符合项目规范且作者信息真实；
- [ ] 数据库设计、数据字典、SQL 和代码字段保持一致；
- [ ] 联调问题和最终结果已记录。

---

## 14. 个人贡献说明模板

> 开发 A 负责系统数据层基础设施，完成数据库连接配置、楼盘与房屋实体建模，以及楼盘和房屋 DAO 的 JDBC 实现。通过 PreparedStatement、资源自动关闭、状态条件更新和数据库事务协作接口，保证数据访问的安全性与购买流程的数据一致性，并完成数据库脚本及 DAO 集成测试联调。

最终提交课程报告前，应将“开发 A”替换为真实姓名，并根据实际完成内容调整描述。

---

## 15. 评审与变更记录

### 15.1 评审记录

| 日期 | 参与人 | 评审结论 | 待办事项 |
|------|--------|----------|----------|
| 待填写 | 待填写 | 待评审 | 确认 House/HouseDao 归属及 SaleRecordDao 负责人 |

### 15.2 变更记录

| 版本 | 日期 | 修改人 | 说明 |
|------|------|--------|------|
| v0.1 | 2026-07-11 | 开发 A（待填写） | 建立数据层主工程计划、接口契约和验收矩阵 |
