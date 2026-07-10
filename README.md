# Building_ManOS — 房屋销售管理系统

**JAVA 高级编程大作业** | 控制台程序 | MySQL 数据库 | 分层架构

---

## 文档导航（Clone 后从这里开始）

按组别找到对应文档，即可开始协作开发。

### 技术组（Java + 数据库）

| 文档 | 说明 |
|------|------|
| [**Java 技术框架**](./docs/design/Java技术框架.md) | 分层架构、包结构、类职责、接口签名、菜单、开发顺序 |
| [**数据库设计**](./docs/design/数据库设计.md) | E-R 图、表结构、字段映射、DAO-SQL 对照、事务约定 |
| [**课堂知识点对照**](./docs/design/课堂知识点对照.md) | 课堂示例代码与本项目的映射、JDBC 学习路径 |
| [课堂示例代码](./docs/course_example_java_learn/) | 课程 JDBC、鲜花商店分层等原始示例（UTF-8） |
| [代码注释规范](./docs/design/代码注释规范.md) | JavaDoc 标准（`@author`、`@param` 等） |
| [sql/schema.sql](./sql/schema.sql) | 建库建表脚本 |
| [sql/init-data.sql](./sql/init-data.sql) | 答辩演示初始数据 |
| [sql/README.md](./sql/README.md) | 数据库脚本使用说明 |
| [团队分工表](./docs/report/团队分工表.md) | 填写姓名与负责模块（`@author` 须一致） |

**源码目录**：`src/main/java/com/building/manos/`

### 文档组

| 文档 | 说明 |
|------|------|
| [需求分析](./docs/requirements/需求分析.md) | 功能需求、用例、非功能需求 |
| [数据字典](./docs/requirements/数据字典.md) | 三表字段与状态码 |
| [概要设计](./docs/design/概要设计.md) | 架构、模块、流程、E-R |
| [课程设计报告](./docs/report/课程设计报告.md) | 完整报告初稿（含团队分工章） |
| [测试用例](./docs/test/测试用例.md) | 功能测试表与答辩检查清单 |
| [用户操作手册](./docs/user-manual/用户操作手册.md) | 安装、菜单、演示流程 |
| [课程要求对照](./docs/requirements/课程要求对照.md) | 官方要求逐条对照 |
| [团队分工表](./docs/report/团队分工表.md) | 填写姓名与负责模块 |
| [docs 总目录](./docs/README.md) | 全部文档索引 |

### PPT 组

| 文档 | 说明 |
|------|------|
| [**答辩 PPT 大纲**](./ppt/答辩PPT大纲.md) | **6 页口述**，总时长 **≤ 5 分钟** |
| [**演讲稿**](./ppt/scripts/演讲稿.md) | 台词、演示脚本、Q&A |
| [PPT 目录说明](./ppt/README.md) | 素材与终稿存放 |
| [Java 技术框架](./docs/design/Java技术框架.md) | 架构图素材 |
| [数据库设计](./docs/design/数据库设计.md) | E-R 图素材 |

### 全员

| 文档 | 说明 |
|------|------|
| [**各组下发指引**](./docs/各组下发指引.md) | **今晚分发任务必读**：各组文档清单与本周任务 |
| [项目计划](./项目计划.md) | 项目总览 |
| [AGENTS.md](./AGENTS.md) | AI 协作与 Cursor 规则说明 |
| [docs 总目录](./docs/README.md) | 全部文档索引 |

---

## 课程要求要点

- Java 语言 + **数据库**存取（MySQL + JDBC）
- **分层架构**：cli → service → dao → database
- **仅控制台**，禁止 GUI / Web / 安卓等
- 每类须含完整注释（编写者、完成时间、功能）
- 文档（含**团队分工**）+ PPT + 答辩演示

---

## 目录结构

```
Building_ManOS/
├── src/              # 源代码（技术组）
├── sql/              # 数据库建表与初始数据（技术组）
├── docs/             # 项目文档（文档组）
│   └── design/       # ★ Java技术框架.md、数据库设计.md
├── ppt/              # 答辩 PPT（PPT组）
├── agent/            # AI 协作 Prompt 与上下文
├── .cursor/          # Cursor 自动规则（智能体自动识别）
├── scripts/          # 运行脚本
├── AGENTS.md         # AI 协作入口
└── 项目计划.md
```

---

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+

---

## 快速开始（技术组）

```bash
# 1. 建库建表
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/init-data.sql

# 2. 编译运行
mvn clean compile
mvn exec:java -Dexec.mainClass="com.building.manos.Main"
```

Windows 也可执行：`scripts/run.ps1`

---

## 系统核心：Java + 数据库

| 部分 | 位置 | 设计文档 |
|------|------|----------|
| Java 控制台程序 | `src/` | [Java 技术框架](./docs/design/Java技术框架.md) |
| MySQL 数据库 | `sql/` | [数据库设计](./docs/design/数据库设计.md) |

```
cli（表示层）→ service（业务层）→ dao（数据访问层）→ MySQL
```

| 包 | 层级 | 职责 |
|----|------|------|
| `cli` | 表示层 | 菜单与终端 IO |
| `service` | 业务层 | 业务规则、折扣、事务 |
| `dao` | 数据访问层 | JDBC / SQL |
| `model` | 实体 | 与数据库表对应 |
| `config` | 配置 | 数据库连接 |

---

## AI 协作（Vibe Coding）

团队规范由 Cursor **自动加载**，见 [AGENTS.md](./AGENTS.md)：

- `.cursor/rules/` — 项目总则、Java 分层、JavaDoc
- `agent/prompts/` — 可复制 Prompt 模板
- 对话中声明身份：`我是张三，负责 dao 层`
