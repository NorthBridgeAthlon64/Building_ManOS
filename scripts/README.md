# 脚本目录

## 脚本

| 脚本 | 说明 |
|------|------|
| **`run_building_os.cmd`** / `run_building_os.ps1` | **一键启动**：MySQL → 初始化库 → API + 前端 |
| `setup-db.ps1` | JDBC 执行 schema + init-data |
| `run-api.ps1` | 启动 Javalin HTTP API（8080） |
| `run-web.ps1` | 启动 Vue 前端（Vite 5173） |
| `run.ps1` | 启动控制台菜单（cli） |
| `run.sh` | Linux/macOS：控制台 |

## 一键启动（推荐）

双击项目根目录 [`run_building_os.cmd`](../run_building_os.cmd)，或：

```powershell
.\run_building_os.cmd
# 或
powershell -File scripts/run_building_os.ps1
```

可选参数：

```powershell
powershell -File scripts/run_building_os.ps1 -SkipDbInit   # 不刷新演示数据
powershell -File scripts/run_building_os.ps1 -NoBrowser    # 不自动打开浏览器
```

成功后访问 `http://127.0.0.1:5173/`。

## 分步启动

```powershell
powershell -File scripts/setup-db.ps1
powershell -File scripts/run-api.ps1   # 终端 A
powershell -File scripts/run-web.ps1   # 终端 B
```


穿透说明见 [docs/ops/内网穿透说明.md](../docs/ops/内网穿透说明.md)。
API 契约见 [docs/design/API设计.md](../docs/design/API设计.md)。

## 测试

```powershell
$env:RUN_DB_TESTS="true"
mvn test
```
