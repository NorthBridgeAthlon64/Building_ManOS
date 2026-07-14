# 编译并运行（Windows PowerShell）
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot\..

function Resolve-Maven {
    $cmd = Get-Command mvn -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }

    $portable = Join-Path $PSScriptRoot "..\.tools\apache-maven-3.9.6\bin\mvn.cmd"
    if (Test-Path $portable) { return (Resolve-Path $portable).Path }

    throw "未找到 Maven。请安装 Maven 或将便携版解压到 .tools/apache-maven-3.9.6/"
}

$mvn = Resolve-Maven

Write-Host ">>> mvn clean compile" -ForegroundColor Cyan
& $mvn clean compile -q

Write-Host ">>> mvn exec:java (控制台 cli)" -ForegroundColor Cyan
& $mvn exec:java -q "-Dexec.mainClass=com.building.manos.Main"
