#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."

echo ">>> mvn clean compile"
mvn clean compile -q

echo ">>> mvn exec:java"
mvn exec:java -q
