# 初始化 building_manos 数据库（建表 + 演示数据）
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot\..

$jar = Get-ChildItem "$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\*\mysql-connector-j-*.jar" |
    Sort-Object FullName -Descending |
    Select-Object -First 1 -ExpandProperty FullName
if (-not $jar) {
    throw "未找到 mysql-connector-j，请先运行 mvn compile"
}

$workDir = Join-Path $env:TEMP "building-manos-db"
New-Item -ItemType Directory -Path $workDir -Force | Out-Null
Copy-Item "scripts\DbBootstrap.java" $workDir
javac -encoding UTF-8 -cp $jar (Join-Path $workDir "DbBootstrap.java")
java -cp "$workDir;$jar" DbBootstrap sql/schema.sql sql/init-data.sql

Write-Host ">>> 数据库初始化完成" -ForegroundColor Green
