---
name: building-manos-dev
description: >-
  开发 Building ManOS 房屋销售管理系统功能时使用。适用于新建 Java 类、
  实现 CRUD/查询/购买、编写 dao 与 service、控制台菜单。遵循分层架构、
  MySQL JDBC、JavaDoc 规范。
---

# Building ManOS 开发技能

## 开始前

1. 确认分层：cli → service → dao → MySQL
2. 确认 `@author` 为当前开发者真实姓名
3. 表结构以 `sql/schema.sql` 为准

## 实现顺序（新功能）

1. `model` 实体（若尚无）
2. `dao` — SQL + PreparedStatement + 映射
3. `service` — 业务规则与校验
4. `cli` — 菜单项与输入输出

## 生成代码时必做

- 每个 public 类/方法写标准 JavaDoc（`@author` `@since` `@param` `@return`）
- dao 不写业务判断；service 不直接拼 SQL
- 禁止 GUI/Web 依赖

## 参考文件

- 注释：`docs/design/代码注释规范.md`
- 架构：`项目计划.md` 第三节
- Prompt 模板：`agent/prompts/java-feature.md`
