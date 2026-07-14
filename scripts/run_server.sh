#!/usr/bin/env bash
# Building ManOS — Linux 一键编译并常驻部署 API（systemd）
# 用法：
#   chmod +x scripts/run_server.sh
#   ./scripts/run_server.sh              # 编译 + 安装/重启 systemd 服务
#   ./scripts/run_server.sh --fg         # 前台跑 API（不装 systemd，SSH 断开即停）
#   ./scripts/run_server.sh --with-web   # 额外 npm build 前端（不自动配 Nginx）
#   ./scripts/run_server.sh --skip-build # 只用已有 target，仅重启服务
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

SERVICE_NAME="building-manos-api"
SERVICE_FILE="/etc/systemd/system/${SERVICE_NAME}.service"
JAVA_BIN="$(command -v java || true)"
MVN_BIN="$(command -v mvn || true)"
PORT="${SERVER_PORT:-8080}"
HOST="${SERVER_HOST:-0.0.0.0}"

FG=0
WITH_WEB=0
SKIP_BUILD=0
for arg in "$@"; do
  case "$arg" in
    --fg) FG=1 ;;
    --with-web) WITH_WEB=1 ;;
    --skip-build) SKIP_BUILD=1 ;;
    -h|--help)
      sed -n '2,9p' "$0"
      exit 0
      ;;
    *)
      echo "未知参数: $arg（支持 --fg / --with-web / --skip-build）" >&2
      exit 1
      ;;
  esac
done

log() { echo ">>> $*"; }

need_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "缺少命令: $1。请先安装（例: sudo apt install -y openjdk-17-jdk maven）" >&2
    exit 1
  fi
}

need_cmd java
need_cmd mvn
JAVA_BIN="$(command -v java)"

if [[ ! -f "$ROOT/src/main/resources/database.properties" ]]; then
  echo "未找到 database.properties，请先配置 JDBC。" >&2
  exit 1
fi

build_backend() {
  log "编译后端并复制依赖 → target/"
  mvn -DskipTests package dependency:copy-dependencies -DoutputDirectory=target/dependency -q
  if [[ ! -d "$ROOT/target/classes" ]] || [[ ! -d "$ROOT/target/dependency" ]]; then
    echo "编译产物不完整，请检查 mvn 输出。" >&2
    exit 1
  fi
}

build_frontend() {
  need_cmd npm
  log "构建前端 frontend/dist（VITE_API_BASE_URL 置空，走同源 /api）"
  (
    cd "$ROOT/frontend"
    if [[ ! -d node_modules ]]; then
      npm install
    fi
    printf 'VITE_API_BASE_URL=\n' > .env.production
    npm run build
  )
  log "静态资源: $ROOT/frontend/dist （可用 Nginx 托管，见 docs/ops/Linux服务器部署.md）"
}

run_foreground() {
  log "前台启动 API  ${HOST}:${PORT} （Ctrl+C 结束）"
  export SERVER_HOST="$HOST" SERVER_PORT="$PORT"
  exec java -cp "$ROOT/target/classes:$ROOT/target/dependency/*" com.building.manos.ServerMain
}

write_systemd_unit() {
  if [[ "$(id -u)" -ne 0 ]]; then
    SUDO=(sudo)
  else
    SUDO=()
  fi

  log "写入 systemd 单元: $SERVICE_FILE"
  "${SUDO[@]}" tee "$SERVICE_FILE" >/dev/null <<EOF
[Unit]
Description=Building ManOS HTTP API
After=network.target mysql.service
Wants=mysql.service

[Service]
Type=simple
WorkingDirectory=${ROOT}
Environment=SERVER_HOST=${HOST}
Environment=SERVER_PORT=${PORT}
ExecStart=${JAVA_BIN} -cp ${ROOT}/target/classes:${ROOT}/target/dependency/* com.building.manos.ServerMain
Restart=on-failure
RestartSec=5
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
EOF

  log "daemon-reload + enable --now ${SERVICE_NAME}"
  "${SUDO[@]}" systemctl daemon-reload
  "${SUDO[@]}" systemctl enable --now "$SERVICE_NAME"
  "${SUDO[@]}" systemctl restart "$SERVICE_NAME"
  sleep 2
  "${SUDO[@]}" systemctl --no-pager --full status "$SERVICE_NAME" || true
}

health_check() {
  local url="http://127.0.0.1:${PORT}/api/health"
  log "健康检查: $url"
  if command -v curl >/dev/null 2>&1; then
    if curl -fsS --max-time 8 "$url"; then
      echo
      log "部署成功。公网请配合安全组/Nginx，详见 docs/ops/Linux服务器部署.md"
    else
      echo
      echo "健康检查失败。查看日志: journalctl -u ${SERVICE_NAME} -n 80 --no-pager" >&2
      exit 1
    fi
  else
    log "未安装 curl，跳过健康检查。请手动访问 $url"
  fi
}

# ---- main ----
if [[ "$SKIP_BUILD" -eq 0 ]]; then
  build_backend
else
  log "跳过编译（--skip-build）"
fi

if [[ "$WITH_WEB" -eq 1 ]]; then
  build_frontend
fi

if [[ "$FG" -eq 1 ]]; then
  run_foreground
fi

if ! command -v systemctl >/dev/null 2>&1; then
  echo "当前环境无 systemctl，改为前台运行…" >&2
  run_foreground
fi

write_systemd_unit
health_check
