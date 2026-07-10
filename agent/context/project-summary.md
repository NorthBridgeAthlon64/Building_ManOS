# Building ManOS — AI 上下文速览

> 详细规则由 `.cursor/rules/` **自动加载**；本文供 @ 引用时快速对齐。

## 项目

房地产公司房屋销售管理系统，JAVA 高级编程大作业。

## 技术约束

- Java 17 + Maven + MySQL 8 + JDBC
- **仅控制台**，禁止 GUI/Web
- **分层**：`cli` → `service` → `dao` → MySQL

## 包结构

`com.building.manos.{cli,service,dao,model,config,discount,util}`

## 核心业务

楼盘 CRUD、房屋 CRUD、多条件查询、购买+折扣、成交记录

## 注释

标准 JavaDoc：`@author` `@since` `@param` `@return` `@throws` `@see`  
详见 `docs/design/代码注释规范.md`

## 智能体配置位置

| 自动 | 手动 @ |
|------|--------|
| `.cursor/rules/*.mdc` | `agent/prompts/*.md` |
| `AGENTS.md` | `项目计划.md` |
| `.cursor/skills/building-manos-dev/` | `docs/design/` |

## 开发者身份

写代码前在对话中声明：`我是 <姓名>，负责 <模块>`，以便 `@author` 正确。
