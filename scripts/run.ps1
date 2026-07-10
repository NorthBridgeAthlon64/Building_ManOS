# 编译并运行（Windows PowerShell）
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot\..

Write-Host ">>> mvn clean compile" -ForegroundColor Cyan
mvn clean compile -q

Write-Host ">>> mvn exec:java" -ForegroundColor Cyan
mvn exec:java -q
