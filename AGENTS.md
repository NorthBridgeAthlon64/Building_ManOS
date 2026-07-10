# AGENTS.md — AI 协作入口

本仓库已配置 Cursor 规则，智能体**会自动加载** `.cursor/rules/` 下的规范。

## 自动生效的规则

| 规则文件 | 何时生效 |
|----------|----------|
| `project-core.mdc` | **每次对话**（alwaysApply） |
| `java-architecture.mdc` | 编辑 `**/*.java` 时 |
| `java-javadoc.mdc` | 编辑 `**/*.java` 时 |

团队成员无需每次手动粘贴规范，打开项目即可。

## 目录分工

| 路径 | 用途 | 智能体是否自动读 |
|------|------|------------------|
| `.cursor/rules/` | **机器可读**规范（优先） | ✅ 自动 |
| `.cursor/skills/` | 专项任务技能 | ✅ 匹配时自动 |
| `agent/context/` | 项目摘要 | ❌ 需 @ 引用 |
| `agent/prompts/` | 可复制 Prompt 模板 | ❌ 需 @ 引用或复制 |
| `docs/` | 人类文档 | ❌ 需 @ 引用 |

## Vibe Coding 推荐流程

1. **日常写 Java**：直接描述需求即可，规则已自动注入
2. **新功能较大时**：`@agent/prompts/java-feature.md` 并填写分工表中的姓名
3. **不确定架构时**：`@项目计划.md` 或 `@agent/context/project-summary.md`
4. **合并前自查**：`@docs/design/代码注释规范.md` 让 AI 做注释审查

## 给智能体的一句话

> 控制台 + MySQL + 四层架构（cli/service/dao/model），标准 JavaDoc，@author 用团队分工表中的真实姓名。

## 组员上手

1. 克隆仓库，用 Cursor 打开 **项目根目录**
2. 设置 → Rules 中确认 Project Rules 已启用
3. 阅读 `agent/README.md`
4. 在 `docs/report/团队分工表.md` 填好姓名，写代码时告诉 AI「我是张三，负责 dao 层」
