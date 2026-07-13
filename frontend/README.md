# Building ManOS Frontend P0

Vue 3 + TypeScript 的可选前端美术概念原型，使用浏览器内 Mock 数据，不连接 Java、HTTP API 或 MySQL，也不改变主项目的控制台运行链路。

## 已覆盖模块

- 资产总览：指标卡、Three.js 城市住宅、库存分布、最近成交
- 楼盘资产：卡片/表格、详情、新增、编辑、删除约束
- 房源中心：五类筛选、详情、新增、编辑、删除、状态动作
- 成交工作台：房屋选择、比例折扣、满额减、确认和模拟回执
- 成交记录：账本摘要、搜索、只读成交票据
- 系统状态：Mock 边界、模块清单、恢复种子数据

## 本地运行

要求 Node.js 20.19+，推荐 Node.js 22.12+ 或 24。

推荐从项目根目录运行，无需全局安装 pnpm：

```powershell
.\scripts\frontend.ps1 install
.\scripts\frontend.ps1 dev
```

已配置 Node.js 20.19+ 和 pnpm 时，也可以在 `frontend/` 目录运行 `pnpm install`、`pnpm dev`。

生产构建：

```powershell
.\scripts\frontend.ps1 build
```

完整 API、运行命令与未来对接契约见 `api.md`。

## 数据边界

- 所有数据定义在 `src/store/mockStore.ts`。
- 增删改、筛选和成交只修改当前标签页内存。
- 刷新页面或在“系统状态”中重置后恢复种子数据。
- P0 不发送网络请求，不读取数据库配置。
