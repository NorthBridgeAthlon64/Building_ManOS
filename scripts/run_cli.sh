#!/usr/bin/env bash
# 控制台菜单（临时运行，非常驻）
set -euo pipefail
cd "$(dirname "$0")/.."
echo ">>> mvn compile"
mvn -q compile
echo ">>> 启动 cli（Main）"
mvn -q exec:java -Dexec.mainClass=com.building.manos.Main
