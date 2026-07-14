# 脚本目录

## 脚本

| 脚本 | 说明 |
|------|------|
| **`run_building_os.cmd`** / `run_building_os.ps1` | **一键启动**：MySQL → API + 前端（默认**不**清空数据库） |
| `setup-db.ps1` | JDBC 执行 schema + init-data |
| `run-api.ps1` | **API + 前端 + 开浏览器**；拉起/退出 MySQL（`-ApiOnly` / `-KeepMySql`） |
| `run-web.ps1` | 仅启动 Vue 前端（Vite 5173） |
| `mysql-lifecycle.ps1` | MySQL 启停公共函数（被上列脚本引用） |
| **`run.ps1`** | **本机默认入口** → 等同 `run_building_os`（API+前端+开浏览器） |
| `run-cli.ps1` | Windows 控制台菜单（不开浏览器） |
| **`run_server.sh`** | **Linux 一键部署 API**（编译 + systemd 常驻） |
| `run_cli.sh` | Linux：临时跑控制台菜单 |

## 一键启动（推荐）

双击项目根目录 [`run_building_os.cmd`](../run_building_os.cmd)，或：

```powershell
.\run_building_os.cmd
# 或
powershell -File scripts/run_building_os.ps1
```

可选参数：

```powershell
powershell -File scripts/run_building_os.ps1              # 默认保留库中已有数据
powershell -File scripts/run_building_os.ps1 -InitDb     # 危险：DELETE 后灌演示数据
powershell -File scripts/run_building_os.ps1 -NoBrowser  # 不自动打开浏览器
```

Java 后端日志在单独弹出的 **API** PowerShell 窗口中（非 `-q`，可见 Javalin/请求日志）。

成功后访问 `http://127.0.0.1:5173/`。

## 分步启动

```powershell
powershell -File scripts/setup-db.ps1
powershell -File scripts/run-api.ps1   # 终端 A
powershell -File scripts/run-web.ps1   # 终端 B
```


穿透说明见 [docs/ops/内网穿透说明.md](../docs/ops/内网穿透说明.md)。  
API 契约见 [docs/design/API设计.md](../docs/design/API设计.md)。  
Linux 常驻部署细则见 [docs/ops/Linux服务器部署.md](../docs/ops/Linux服务器部署.md)。

## Linux 一键部署 API（服务器）

先配置好 `database.properties`（推荐独立用户，勿依赖 `sudo mysql` 免密），再：

```bash
cd /home/admin/Building_ManOS
chmod +x scripts/run_server.sh
./scripts/run_server.sh
```

| 参数 | 含义 |
|------|------|
| （默认） | 编译 + 安装/重启 `building-manos-api` systemd |
| `--fg` | 前台跑，不装 systemd |
| `--with-web` | 额外构建前端 dist |
| `--skip-build` | 不编译，只重启服务 |

临时控制台：`./scripts/run_cli.sh`

## 测试

```powershell
$env:RUN_DB_TESTS="true"
mvn test
```
