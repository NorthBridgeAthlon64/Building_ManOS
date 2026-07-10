# Agent 协作目录

> **人类可读**的 Prompt、上下文与决策记录。  
> **机器自动识别**的规范在 `.cursor/rules/` 和 `AGENTS.md`，无需每次手动 @。

## 两层体系

```
┌─────────────────────────────────────────┐
│  .cursor/rules/*.mdc   ← Cursor 自动加载 │
│  AGENTS.md             ← 智能体入口说明   │
│  .cursor/skills/       ← 专项任务技能     │
└─────────────────────────────────────────┘
              ↑ 自动生效，vibecoding 默认即用

┌─────────────────────────────────────────┐
│  agent/prompts/        ← 复制或 @ 引用    │
│  agent/context/        ← 项目摘要          │
│  agent/logs/           ← 架构决策记录     │
└─────────────────────────────────────────┘
              ↑ 需要时 @ 文件 或 粘贴到对话
```

## 子目录

| 目录 | 用途 |
|------|------|
| `prompts/` | 统一 Prompt 模板，保证全队生成风格一致 |
| `context/` | 项目背景摘要（比 项目计划.md 更短） |
| `logs/` | 重要决策（如换数据库、改包名） |

## Vibe Coding 怎么用

### 场景 1：日常写代码（最常见）

直接对 Cursor 说需求即可，例如：

> 帮我实现 BuildingDao 的 insert 和 findById，作者是李四

`.cursor/rules/` 会自动约束分层和 JavaDoc。

### 场景 2：较大功能

在对话里 @ 引用：

```
@agent/prompts/java-feature.md
我是王五，实现房屋按价格区间查询，从 dao 到 cli 菜单
```

### 场景 3：代码审查

```
@docs/design/代码注释规范.md
检查 src/.../PurchaseService.java 是否符合团队注释规范
```

### 场景 4：文档组 / PPT 组

```
@agent/prompts/doc-requirements.md
@项目计划.md
```

## 组员检查清单

- [ ] 用 Cursor 打开的是 **Building_ManOS 根目录**（否则 rules 不生效）
- [ ] Settings → Rules → Project Rules 已开启
- [ ] 告诉 AI 你的 **真实姓名**（对应 `@author`）
- [ ] 不绕过规范：禁止让 AI 生成 Swing/Web 或纯文件存储方案

## 维护责任

| 变更类型 | 谁更新 | 更新哪里 |
|----------|--------|----------|
| 架构/课程约束变更 | 组长 | `.cursor/rules/project-core.mdc` |
| Java 编码约定变更 | 技术组长 | `.cursor/rules/java-*.mdc` |
| 注释规范变更 | 文档组 | `docs/design/代码注释规范.md` + `java-javadoc.mdc` |
| 新 Prompt 模板 | 各组 | `agent/prompts/` |

## 注意

`logs/` 下 `*.log` 已在 `.gitignore` 忽略；`decisions.md` 可提交。
