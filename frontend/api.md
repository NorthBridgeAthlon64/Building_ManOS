# Building ManOS 前端 API 与运行说明

## 1. 当前状态

当前 `frontend/` 是 Vue 3 + TypeScript 的 P0 交互原型：

- 不发送 HTTP 请求。
- 不连接 Java、JDBC 或 MySQL。
- 所有数据和业务动作由 `src/store/mockStore.ts` 在浏览器内存中完成。
- 刷新页面后恢复种子数据。

本文将 API 分为两类：

1. **P0 已实现 Mock API**：前端当前真实调用的 TypeScript 方法。
2. **未来 REST 契约草案**：若项目后续获准增加 Web 适配层，前端需要的 HTTP 接口。当前 Java 主工程尚未实现这些 HTTP 接口。

## 2. 运行命令

### 2.1 推荐：使用项目 PowerShell 脚本

该方式会优先使用 Codex 环境内置的 Node.js 和 pnpm，不要求在系统 PATH 中全局安装 pnpm。

在项目根目录执行：

```powershell
cd "D:/projects/java/project/Building_ManOS"

# 首次安装依赖
.\scripts\frontend.ps1 install

# 启动开发服务器
.\scripts\frontend.ps1 dev

# TypeScript 检查和生产构建
.\scripts\frontend.ps1 build
```

开发服务器地址：`http://127.0.0.1:5173`

### 2.2 已安装 Node.js 与 pnpm 时

要求 Node.js 20.19+，推荐 Node.js 22.12+ 或 24。

```powershell
cd "D:/projects/java/project/Building_ManOS/frontend"

pnpm install
pnpm dev
pnpm build
pnpm preview --host 127.0.0.1 --port 4173
```

生产预览地址：`http://127.0.0.1:4173`

## 3. 前端页面路由

| 路由 | 模块 | 主要能力 |
| --- | --- | --- |
| `/dashboard` | 资产总览 | Three.js 城市、指标、库存分布、最近成交 |
| `/buildings` | 楼盘资产 | 查询、新增、编辑、删除、详情 |
| `/houses` | 房源中心 | 组合筛选、新增、编辑、删除、详情 |
| `/transactions/new` | 成交工作台 | 选房、折扣预览、确认成交、回执 |
| `/sales` | 成交账本 | 摘要、搜索、只读票据 |
| `/system` | 系统状态 | Mock 边界、种子数据重置 |

## 4. 数据类型

类型定义位于 `src/types.ts`。

### 4.1 Building

```ts
interface Building {
  id: string
  name: string
  landArea: number
  address: string
  developer: string
  remark: string
  createdAt: string
}
```

约束：

- `name`、`address` 必填。
- `landArea` 必须大于 0，单位为平方米。
- `id` 和 `createdAt` 由数据层生成。
- 楼盘下仍有房屋时禁止删除。

### 4.2 House

```ts
type HouseStatus = 'ON_SALE' | 'SOLD'

interface House {
  id: string
  buildingId: string
  buildingNo: string
  roomNo: string
  area: number
  unitPrice: number
  totalPrice: number
  status: HouseStatus
  soldAt: string | null
}
```

约束：

- `buildingId` 必须指向已存在楼盘。
- `buildingNo`、`roomNo` 必填。
- `area`、`unitPrice` 必须大于 0。
- `totalPrice = area × unitPrice`，四舍五入保留两位。
- 同一楼盘内 `buildingNo + roomNo` 唯一。
- 只有 `ON_SALE` 房屋允许编辑或删除。

### 4.3 SaleRecord

```ts
type DiscountType = 'PERCENTAGE' | 'THRESHOLD'

interface SaleRecord {
  id: string
  houseId: string
  originalPrice: number
  discountType: DiscountType
  discountValue: number
  finalPrice: number
  customerName: string
  soldAt: string
}
```

约束：

- 每套房屋最多一条成交记录。
- 只有 `ON_SALE` 房屋可以成交。
- 成交操作必须在同一事务中完成房屋状态更新和成交记录写入。
- 时间字段使用 ISO 8601 字符串。

### 4.4 DiscountPreview

```ts
interface DiscountPreview {
  type: DiscountType
  value: number
  finalPrice: number
  saving: number
  tierLabel: string
  formula: string
}
```

折扣档位：

| 原价 | PERCENTAGE | THRESHOLD |
| --- | --- | --- |
| `< 100 万` | 100% | 减 2 万 |
| `100 万 ≤ 原价 < 300 万` | 97% | 减 5 万 |
| `≥ 300 万` | 92% | 减 15 万 |

## 5. P0 已实现 Mock API

模块：`src/store/mockStore.ts`

### 5.1 状态与查询

| API | 返回值 | 说明 |
| --- | --- | --- |
| `mockStore.state` | `{ buildings, houses, sales }` | Vue reactive 状态 |
| `buildingById(id)` | `Building \| undefined` | 按楼盘编号查询 |
| `houseById(id)` | `House \| undefined` | 按房屋编号查询 |
| `housesForBuilding(buildingId)` | `House[]` | 查询楼盘关联房屋 |

房源页面的关键词、楼号、价格、面积和状态组合筛选目前由页面计算属性完成，不经过网络或独立查询服务。

### 5.2 楼盘操作

```ts
saveBuilding(input): Building
removeBuilding(id): void
```

- `saveBuilding`：有 `id` 时更新，无 `id` 时新增并生成编号、创建时间。
- `removeBuilding`：存在关联房屋时抛出 `Error('该楼盘仍有房屋，无法删除')`。

### 5.3 房屋操作

```ts
saveHouse(input): House
removeHouse(id): void
```

- 保存时自动计算 `totalPrice`。
- 重复楼盘/楼号/房号时抛出错误。
- 已售房屋禁止编辑和删除。

### 5.4 折扣与成交

```ts
discountPreview(originalPrice, type): DiscountPreview
purchase(houseId, type, customerName): SaleRecord
```

- `discountPreview` 根据价格档位返回折扣值、实付金额、节省金额和公式。
- `purchase` 校验房屋存在且在售，将房屋改为 `SOLD`，然后创建成交记录。
- P0 Mock 中两步操作发生在同一段同步代码内，但不具备数据库事务能力。

### 5.5 重置

```ts
resetMockData(): void
```

清除当前标签页中的变更并恢复初始楼盘、房屋和成交数据。

## 6. 未来 REST 契约草案

> 本节是前端对接需求，不表示主工程已经提供 Web API。当前项目规则仍以控制台 + Service + DAO + MySQL 为正式运行链路。

建议基础路径：`/api/v1`

### 6.1 通用响应

成功：

```json
{
  "data": {},
  "requestId": "optional-request-id"
}
```

失败：

```json
{
  "code": "VALIDATION_ERROR",
  "message": "楼盘名称不能为空",
  "fieldErrors": {
    "name": "不能为空"
  }
}
```

建议状态码：

| HTTP 状态 | 使用场景 |
| --- | --- |
| `200` | 查询、更新、折扣预览成功 |
| `201` | 新增楼盘、房屋或成交成功 |
| `204` | 删除成功 |
| `400` | 参数或业务校验失败 |
| `404` | 楼盘、房屋或成交记录不存在 |
| `409` | 房号重复、房屋已售、楼盘仍有关联房屋、一房重复成交 |
| `500` | 未处理的服务端错误 |

### 6.2 楼盘接口

| 方法 | 路径 | 请求 | 返回 |
| --- | --- | --- | --- |
| `GET` | `/api/v1/buildings` | 可选 `keyword` | `Building[]` |
| `GET` | `/api/v1/buildings/{id}` | 路径参数 | `Building` |
| `POST` | `/api/v1/buildings` | `BuildingCreateRequest` | `Building` |
| `PUT` | `/api/v1/buildings/{id}` | `BuildingUpdateRequest` | `Building` |
| `DELETE` | `/api/v1/buildings/{id}` | 路径参数 | 无响应体 |
| `GET` | `/api/v1/buildings/{id}/houses` | 可选 `status` | `House[]` |

```ts
interface BuildingCreateRequest {
  name: string
  landArea: number
  address: string
  developer?: string
  remark?: string
}

type BuildingUpdateRequest = BuildingCreateRequest
```

### 6.3 房屋接口

| 方法 | 路径 | 请求 | 返回 |
| --- | --- | --- | --- |
| `GET` | `/api/v1/houses` | 组合查询参数 | `House[]` |
| `GET` | `/api/v1/houses/{id}` | 路径参数 | `House` |
| `POST` | `/api/v1/houses` | `HouseCreateRequest` | `House` |
| `PUT` | `/api/v1/houses/{id}` | `HouseUpdateRequest` | `House` |
| `DELETE` | `/api/v1/houses/{id}` | 路径参数 | 无响应体 |

`GET /api/v1/houses` 查询参数：

| 参数 | 类型 | 说明 |
| --- | --- | --- |
| `keyword` | string | 匹配楼盘名称、楼号、房号或编号 |
| `buildingId` | string | 所属楼盘编号 |
| `buildingNo` | string | 楼号 |
| `minPrice` / `maxPrice` | decimal | 总价范围，单位元 |
| `minArea` / `maxArea` | decimal | 面积范围，单位平方米 |
| `status` | `ON_SALE \| SOLD` | 销售状态 |

```ts
interface HouseCreateRequest {
  buildingId: string
  buildingNo: string
  roomNo: string
  area: number
  unitPrice: number
}

type HouseUpdateRequest = HouseCreateRequest
```

`id`、`totalPrice`、`status` 和 `soldAt` 由服务端管理，前端不得覆盖。

### 6.4 折扣预览

| 方法 | 路径 | 请求 | 返回 |
| --- | --- | --- | --- |
| `POST` | `/api/v1/discounts/preview` | `DiscountPreviewRequest` | `DiscountPreview` |

```ts
interface DiscountPreviewRequest {
  houseId: string
  type: DiscountType
}
```

服务端应以数据库中的房屋总价计算折扣，不能信任前端提交的原价或最终价格。

### 6.5 成交接口

| 方法 | 路径 | 请求 | 返回 |
| --- | --- | --- | --- |
| `GET` | `/api/v1/sales` | 可选 `houseId`、`keyword` | `SaleRecord[]` |
| `GET` | `/api/v1/sales/{id}` | 路径参数 | `SaleRecord` |
| `POST` | `/api/v1/sales` | `PurchaseRequest` | `SaleRecord` |

```ts
interface PurchaseRequest {
  houseId: string
  discountType: DiscountType
  customerName: string
}
```

`POST /api/v1/sales` 必须由服务端完成：

1. 查询并锁定房屋。
2. 校验状态为 `ON_SALE`。
3. 读取数据库原价并计算折扣。
4. 将房屋更新为 `SOLD` 并写入 `soldAt`。
5. 插入唯一成交记录。
6. 在同一数据库事务内提交或整体回滚。

## 7. 页面与 API 映射

| 页面 | P0 数据来源 | 未来 REST API |
| --- | --- | --- |
| 资产总览 | `state` 派生统计 | `/buildings`、`/houses`、`/sales` |
| 楼盘资产 | `saveBuilding`、`removeBuilding` | `/buildings` |
| 房源中心 | 页面组合筛选、`saveHouse`、`removeHouse` | `/houses` |
| 成交工作台 | `discountPreview`、`purchase` | `/discounts/preview`、`/sales` |
| 成交账本 | `state.sales` | `/sales` |
| 系统状态 | 本地常量、`resetMockData` | P0 保持本地；不需要服务端接口 |

## 8. 建议错误码

| 错误码 | 含义 |
| --- | --- |
| `VALIDATION_ERROR` | 字段缺失、范围非法 |
| `BUILDING_NOT_FOUND` | 楼盘不存在 |
| `BUILDING_HAS_HOUSES` | 楼盘仍有关联房屋 |
| `HOUSE_NOT_FOUND` | 房屋不存在 |
| `HOUSE_DUPLICATE` | 同楼盘楼号和房号重复 |
| `HOUSE_NOT_ON_SALE` | 房屋已售，禁止编辑、删除或成交 |
| `SALE_NOT_FOUND` | 成交记录不存在 |
| `SALE_ALREADY_EXISTS` | 同一房屋已经存在成交记录 |
| `DATABASE_ERROR` | 数据库访问或事务失败 |

## 9. 正式对接前缺口

1. 当前 Java 主工程没有 HTTP Controller、JSON 序列化、CORS 或统一异常响应。
2. 项目核心规则仍明确禁止 Web；增加 REST 层前必须先取得课程/维护者许可。
3. 当前 `SearchService` 是单条件查询，前端需要的是组合筛选接口。
4. 当前 `SaleRecordDao` 支持全部查询和按房屋查询，尚无按成交编号查询。
5. 当前前端使用 JavaScript `number` 表示金额；若金额范围扩大，应协商使用十进制定点字符串，避免浮点精度风险。
6. 正式对接时应新增独立 API Client 层，不应让 Vue 页面直接调用 `fetch`。
