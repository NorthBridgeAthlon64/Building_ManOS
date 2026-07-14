# Building_ManOS — 房屋销售管理系统

**JAVA 高级编程大作业** | 控制台 +（可选）Vue/API | MySQL | 分层架构

> **答辩说明（2026-07-14）**：老师已取消 PPT 要求；提交重点为 **[大作业报告](./docs/report/大作业报告.md)** + **现场演示**。

---

## 你是谁？从这里开始 👇

| 你的角色 | 第 1 步：读这个 | 第 2 步：做这个 | 第 3 步：交这个 |
|----------|----------------|----------------|----------------|
| **技术组** | [Java 技术框架](./docs/design/Java技术框架.md) | 保演示畅通、协助截图 | 可运行程序 + 注释 |
| **文档组** | **[大作业报告](./docs/report/大作业报告.md)** | 补学号/截图 → 导出 Word | 大作业报告 + 分工表 |
| **原 PPT 组** | [ppt/README](./ppt/README.md) | 转做文档截图与演示彩排 | （PPT 非必需） |

> 全员必做：在 [团队分工表](./docs/report/团队分工表.md) 填写真实姓名与学号。

---

## 项目简介

某房地产公司的**房屋销售管理系统**：

- 楼盘 / 房屋管理（增删改查）
- 多条件查询、档位折扣购买、成交记录
- **MySQL + JDBC** 持久化；**cli 与 api/Vue 双表示层** 共用 service

```
cli / (Vue → api) → service（业务/折扣）→ dao（JDBC）→ MySQL
```

---

## 目录结构（一眼看懂）

```
Building_ManOS/
├── src/                  # Java 源代码
│   └── main/java/com/building/manos/
│       ├── Main.java / ServerMain.java
│       ├── config/ model/ dao/ service/ discount/
│       ├── cli/          # 控制台
│       ├── api/          # Javalin REST
│       └── util/
├── frontend/             # Vue 3 前端（经 /api 接库）
├── sql/                  # schema.sql / init-data.sql
├── docs/                 # 文档 ★ 终稿在 report/大作业报告.md
├── ppt/                  # 可选口述提纲（非必需）
├── scripts/ + run_building_os.cmd
├── 项目计划.md
└── AGENTS.md
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

Windows：`scripts/run.ps1`（控制台）或 `run_building_os.cmd`（API+前端）

---

## 课程硬性要求

| 要求 | 说明 |
|------|------|
| 语言 | 必须用 Java |
| 数据库 | **必须**用数据库（MySQL + JDBC） |
| 架构 | **分层架构**（cli/api → service → dao） |
| 界面 | 控制台须可运行；本仓库另含 API+Vue |
| 注释 | 每个类须有 JavaDoc（@author、功能说明） |
| 交付 | 代码 + **大作业报告（含团队分工）** + 现场演示（PPT 非必需） |
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

### 文档组（本阶段重点）

| 文档 | 说明 |
|------|------|
| **[大作业报告](./docs/report/大作业报告.md)** | **按老师模板的提交终稿 ★** |
| [团队分工表](./docs/report/团队分工表.md) | 姓名学号与工作量 |
| [需求分析](./docs/requirements/需求分析.md) | 功能需求、用例 |
| [概要设计](./docs/design/概要设计.md) | 架构、模块、流程 |
| [测试用例](./docs/test/测试用例.md) | 功能测试表 |
| [用户操作手册](./docs/user-manual/用户操作手册.md) | 安装与演示步骤 |
| [各组下发指引](./docs/各组下发指引.md) | 本周任务 |

### 原 PPT 组（可选参考）

| 文档 | 说明 |
|------|------|
| [ppt/README](./ppt/README.md) | 已标注非必需；建议转文档截图 |
| [答辩 PPT 大纲](./ppt/答辩PPT大纲.md) | 可选口述提纲 |

---

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
