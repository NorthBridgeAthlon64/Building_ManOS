# 启动 HTTP API（Javalin）
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot\..

function Resolve-Maven {
    $cmd = Get-Command mvn -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
    $portable = Join-Path $PSScriptRoot "..\.tools\apache-maven-3.9.6\bin\mvn.cmd"
    if (Test-Path $portable) { return (Resolve-Path $portable).Path }
    throw "未找到 Maven。请安装 Maven 或解压到 .tools/apache-maven-3.9.6/"
}

$mvn = Resolve-Maven
Write-Host ">>> mvn compile" -ForegroundColor Cyan
& $mvn -q compile
Write-Host ">>> start API (ServerMain)" -ForegroundColor Cyan
& $mvn -q exec:java
