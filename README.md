# Building_ManOS — 房屋销售管理系统

**JAVA 高级编程大作业** | 控制台程序 | MySQL 数据库 | 分层架构

---

## 你是谁？从这里开始 👇

| 你的角色 | 第 1 步：读这个 | 第 2 步：做这个 | 第 3 步：交这个 |
|----------|----------------|----------------|----------------|
| **技术组** | [Java 技术框架](./docs/design/Java技术框架.md) | 建 MySQL → 写 Java 代码 | 可运行程序 + 注释 |
| **文档组** | [需求分析](./docs/requirements/需求分析.md) | 画图 → 写报告 | 课程设计报告 + 分工表 |
| **PPT 组** | [答辩 PPT 大纲](./ppt/答辩PPT大纲.md) | 做幻灯片 → 写演讲稿 | 答辩 PPT |

> 全员必做：在 [团队分工表](./docs/report/团队分工表.md) 填写真实姓名。

---

## 项目简介

某房地产公司的**房屋销售管理系统**，在命令行终端完成：

- 楼盘管理（增删改查）
- 房屋管理（增删改查、多条件查询）
- 房屋购买（选房 → 折扣 → 成交）
- 数据通过 **MySQL** 持久化，采用 **四层分层架构**

```
cli（菜单/输入输出）→ service（业务逻辑/折扣）→ dao（JDBC/SQL）→ MySQL
```

---

## 目录结构（一眼看懂）

```
Building_ManOS/
├── src/                  # Java 源代码 ← 技术组在这里写代码
│   └── main/java/com/building/manos/
│       ├── Main.java     # 程序入口
│       ├── config/       # 数据库连接配置
│       ├── model/        # 实体类（Building, House, SaleRecord）
│       ├── dao/          # 数据访问层（JDBC 操作）
│       ├── service/      # 业务逻辑层（CRUD、折扣计算）
│       ├── discount/     # 折扣策略（策略模式）
│       ├── cli/          # 控制台菜单与输入输出
│       └── util/         # 工具类
│
├── sql/                  # 数据库脚本 ← 技术组先跑这个建表
│   ├── schema.sql        # 建库建表
│   └── init-data.sql     # 演示用初始数据
│
├── docs/                 # 项目文档 ← 文档组在这里写文档
│   ├── requirements/     # 需求分析、数据字典
│   ├── design/           # 概要设计、数据库设计、Java技术框架 ★
│   ├── test/             # 测试用例
│   ├── report/           # 课程设计报告、团队分工表
│   └── user-manual/      # 用户操作手册
│
├── ppt/                  # 答辩 PPT ← PPT 组在这里做幻灯片
│   ├── slides/           # .pptx 终稿
│   ├── assets/           # 配图、截图
│   └── scripts/          # 演讲稿
│
├── examples/             # 课堂示例代码 ← 写代码时参考用
│
├── scripts/              # 一键运行脚本
├── 项目计划.md            # 项目总览与时间线
└── AGENTS.md             # AI 辅助开发配置（新手可忽略）
```

> ⚠️ 以下目录为 **AI 辅助工具配置**，与项目功能无关，新手可忽略：`agent/`、`.cursor/`、`.arts/`、`.codeartsdoer/`

---

## 快速开始（技术组）

```bash
# 1. 建库建表
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/init-data.sql

# 2. 修改数据库账号
#    编辑 src/main/resources/database.properties

# 3. 编译运行
mvn clean compile
mvn exec:java -Dexec.mainClass="com.building.manos.Main"
```

Windows 也可执行：`scripts/run.ps1`

---

## 课程硬性要求

| 要求 | 说明 |
|------|------|
| 语言 | 必须用 Java |
| 数据库 | **必须**用数据库（MySQL + JDBC） |
| 架构 | **分层架构**（cli → service → dao） |
| 界面 | **仅控制台**，禁止 GUI / Web / 安卓 |
| 注释 | 每个类须有 JavaDoc（@author、@date、功能说明） |
| 交付 | 代码 + 文档（含团队分工）+ PPT + 答辩 |
| 原创 | 两组代码一样 → **均为零分** |

详细对照见 [课程要求对照](./docs/requirements/课程要求对照.md)

---

## 各组详细文档导航

### 技术组

| 文档 | 说明 |
|------|------|
| [Java 技术框架](./docs/design/Java技术框架.md) | 包结构、类职责、接口签名、开发顺序 |
| [数据库设计](./docs/design/数据库设计.md) | 表结构、字段映射、DAO-SQL 对照 |
| [代码注释规范](./docs/design/代码注释规范.md) | JavaDoc 标准 |
| [课堂知识点对照](./docs/design/课堂知识点对照.md) | 课堂示例 → 本项目映射 |
| [课堂示例代码](./examples/) | JDBC、分层架构参考（鲜花商店最接近） |

### 文档组

| 文档 | 说明 |
|------|------|
| [需求分析](./docs/requirements/需求分析.md) | 功能需求、用例 |
| [数据字典](./docs/requirements/数据字典.md) | 三表字段与状态码 |
| [概要设计](./docs/design/概要设计.md) | 架构、模块、流程 |
| [课程设计报告](./docs/report/课程设计报告.md) | 主报告（复制到学校模板） |
| [测试用例](./docs/test/测试用例.md) | 功能测试表 |
| [用户操作手册](./docs/user-manual/用户操作手册.md) | 安装与操作说明 |

### PPT 组

| 文档 | 说明 |
|------|------|
| [答辩 PPT 大纲](./ppt/答辩PPT大纲.md) | 6 页口述，≤ 5 分钟 |
| [演讲稿](./ppt/scripts/演讲稿.md) | 台词与演示脚本 |
| [Java 技术框架](./docs/design/Java技术框架.md) | 架构图素材 |
| [数据库设计](./docs/design/数据库设计.md) | E-R 图素材 |

---

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
