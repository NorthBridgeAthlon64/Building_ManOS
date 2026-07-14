# Building ManOS HTTP API 设计

> **版本**：v1.0（2026-07-14）  
> **服务**：Javalin，默认 `http://0.0.0.0:8080`  
> **前端**：`frontend/` Vue 3，经 `/api` 访问

## 1. 统一响应

```json
{ "code": 0, "message": "ok", "data": {} }
```

| code | 含义 |
|------|------|
| 0 | 成功 |
| 40001 | 业务校验失败 |
| 40401 | 资源不存在 |
| 50000 | 服务器/数据库异常 |

字段 camelCase；金额为 number；时间为 `yyyy-MM-dd'T'HH:mm:ss`。

## 2. 接口清单

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/health` | 健康检查（含 db） |
| GET | `/api/dashboard/summary` | 看板汇总 |
| GET | `/api/buildings` | 楼盘列表 |
| GET | `/api/buildings/{id}` | 楼盘详情 |
| POST | `/api/buildings` | 新增楼盘 |
| PUT | `/api/buildings/{id}` | 修改楼盘 |
| DELETE | `/api/buildings/{id}` | 删除楼盘 |
| GET | `/api/houses` | 房屋列表 |
| GET | `/api/houses/{id}` | 房屋详情 |
| POST | `/api/houses` | 新增房屋 |
| PUT | `/api/houses/{id}` | 修改房屋 |
| DELETE | `/api/houses/{id}` | 删除房屋 |
| POST | `/api/purchases/preview` | 折扣预览 |
| POST | `/api/purchases` | 确认成交 |
| GET | `/api/sales` | 成交列表；`?houseId=` 可选 |

### 2.1 购买预览

```http
POST /api/purchases/preview
Content-Type: application/json

{ "houseId": "H202607130003", "discountType": "PERCENTAGE" }
```

`discountType`：`PERCENTAGE` | `THRESHOLD`。

### 2.2 确认成交

```http
POST /api/purchases
Content-Type: application/json

{ "houseId": "H202607130003", "discountType": "PERCENTAGE", "customerName": "张三" }
```

## 3. 配置

`src/main/resources/server.properties`：

```properties
host=0.0.0.0
port=8080
cors.allowedOrigins=*
```

环境变量覆盖：`SERVER_HOST`、`SERVER_PORT`、`CORS_ALLOWED_ORIGINS`。

## 4. 启动

```powershell
# 数据库
powershell -File scripts/setup-db.ps1
# API
powershell -File scripts/run-api.ps1
# 前端（另一终端）
powershell -File scripts/run-web.ps1
```

控制台菜单仍可用：`mvn exec:java`（默认 cli）。
