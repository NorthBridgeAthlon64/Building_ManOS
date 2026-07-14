# Linux 服务器常驻部署（Building ManOS）

> 适用：Ubuntu 24.04（阿里云 ECS 等）  
> 目标：关机重登后 **API 自动起来**；可选 Nginx 托管 Vue 前端  
> 项目路径示例：`/home/admin/Building_ManOS`

**不要**长期用 SSH 里前台跑 `mvn exec:java`——窗口一关进程就没。应用用 **systemd** 托管。

---

## 0. 架构（推荐）

```
浏览器 → :80 Nginx
            ├─ /          → Vue 静态文件（frontend/dist）
            └─ /api/*     → 反代 → 127.0.0.1:8080（Javalin）
                                    ↓ JDBC
                                 MySQL :3306（building_manos）
```

控制台 cli **不必**常驻；答辩时 SSH 上去临时 `mvn exec:java -Dexec.mainClass=com.building.manos.Main` 即可。

---

## 1. 环境依赖

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk maven nginx
java -version    # 17+
mvn -version
```

MySQL 若已装好并完成 `schema.sql` / `init-data.sql`，可跳过装库。

---

## 2. 数据库：给程序开一个带密码的用户

Ubuntu 的 `sudo mysql` 是 socket，**Java 连不上这种免密 root**。在服务器执行：

```bash
sudo mysql
```

```sql
CREATE USER IF NOT EXISTS 'manos'@'localhost' IDENTIFIED BY '请改成强密码';
GRANT ALL PRIVILEGES ON building_manos.* TO 'manos'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

编辑：

```bash
nano /home/admin/Building_ManOS/src/main/resources/database.properties
```

```properties
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/building_manos?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
user=manos
password=请改成强密码
```

自测：

```bash
mysql -u manos -p -e "USE building_manos; SELECT COUNT(*) FROM house;"
```

---

## 3～4. 一键编译并常驻 API（推荐）

先配好 `database.properties`（见上一节），再：

```bash
cd /home/admin/Building_ManOS
chmod +x scripts/run_server.sh
./scripts/run_server.sh
```

脚本会：`mvn package` + 复制依赖 → 写入 `/etc/systemd/system/building-manos-api.service` → `enable --now` → `curl /api/health`。

| 参数 | 说明 |
|------|------|
| `--fg` | 前台运行（不装 systemd） |
| `--with-web` | 同时 `npm run build` 前端 |
| `--skip-build` | 仅重启已编译服务 |

日志与手工验收：

```bash
journalctl -u building-manos-api -f
curl -s http://127.0.0.1:8080/api/health
```

代码更新后：

```bash
cd /home/admin/Building_ManOS
git pull   # 或重新上传
./scripts/run_server.sh
```

> 若需手写 systemd，单元内容与脚本生成一致，见仓库 `scripts/run_server.sh`。

---

## 5. 前端：构建静态站 + Nginx（推荐公网访问）

### 5.1 构建

空 `VITE_API_BASE_URL` 时，浏览器请求相对路径 `/api/...`，由 Nginx 反代即可。

```bash
cd /home/admin/Building_ManOS/frontend
# 任选：npm / pnpm / yarn
sudo apt install -y nodejs npm   # 若还没有；版本过旧可改用 NodeSource / nvm
npm install
# 生产构建不写死外网 API，走同源 /api
echo 'VITE_API_BASE_URL=' > .env.production
npm run build
# 产出 frontend/dist
```

### 5.2 Nginx 站点

```bash
sudo nano /etc/nginx/sites-available/building-manos
```

```nginx
server {
    listen 80;
    server_name _;   # 或填你的域名 / 公网 IP

    root /home/admin/Building_ManOS/frontend/dist;
    index index.html;

    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

启用：

```bash
sudo ln -sf /etc/nginx/sites-available/building-manos /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default   # 若冲突可先禁用默认站
sudo nginx -t
sudo systemctl reload nginx
sudo systemctl enable nginx
```

浏览器访问：`http://你的公网IP/`（需安全组放行 **80**）。

---

## 6. 阿里云安全组 / 防火墙

| 端口 | 用途 | 建议 |
|------|------|------|
| 22 | SSH | 仅自己 IP |
| 80 | Nginx 网页 | 对公网或答辩网段开放 |
| 8080 | API | **建议不对公网开放**，只本机给 Nginx |
| 3306 | MySQL | **切勿对 0.0.0.0/0 开放** |

```bash
# 若启用了 ufw
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw enable
sudo ufw status
```

---

## 7. 最小方案（无 Nginx，仅 API 常驻）

只要接口、不托管前端时：

1. 完成第 1～4 步  
2. 安全组临时放行 **8080**  
3. 访问 `http://公网IP:8080/api/health`  

前端仍可在笔记本上 `npm run dev`，并设：

```env
VITE_API_BASE_URL=http://公网IP:8080
```

---

## 8. 常用运维命令

```bash
# 状态
sudo systemctl status building-manos-api nginx mysql

# 重启 API
sudo systemctl restart building-manos-api

# 看日志
journalctl -u building-manos-api -n 100 --no-pager

# 临时控制台菜单（不设 systemd）
cd /home/admin/Building_ManOS
mvn -q exec:java -Dexec.mainClass=com.building.manos.Main
```

---

## 9. 检查清单

- [ ] `building_manos` 有表、有演示数据  
- [ ] `database.properties` 为可密码登录的账号（非 sudo socket）  
- [ ] `systemctl is-enabled building-manos-api` → enabled  
- [ ] `curl 127.0.0.1:8080/api/health` 正常  
- [ ] 浏览器打开 `http://公网IP/` 能进页面并完成一次购买演示  
- [ ] 3306 未对公网开放；生产密码勿提交 Git  

---

## 10. 排错速查

| 现象 | 处理 |
|------|------|
| API 起不来 / Access denied | 查 `database.properties`；用 `manos` 用户测 `mysql -u manos -p` |
| `curl` 本机 OK，外网不通 | 查阿里云安全组、本机 ufw、是否只绑了 127.0.0.1（应 `0.0.0.0`） |
| 页面能开但接口 404/502 | Nginx `proxy_pass`、API 是否在跑 |
| 改代码不生效 | 重新 `mvn package` + `dependency:copy-dependencies` 后 `systemctl restart` |
