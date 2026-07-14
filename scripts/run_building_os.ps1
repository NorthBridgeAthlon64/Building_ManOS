# Building ManOS 一键启动：MySQL 检查 →（可选）初始化库 → API + 前端
param(
    [switch]$SkipDbInit,
    [switch]$NoBrowser
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $Root

function Write-Step([string]$Message) {
    Write-Host ""
    Write-Host ">>> $Message" -ForegroundColor Cyan
}

function Resolve-Maven {
    $cmd = Get-Command mvn -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
    $portable = Join-Path $Root ".tools\apache-maven-3.9.6\bin\mvn.cmd"
    if (Test-Path $portable) { return (Resolve-Path $portable).Path }
    throw "未找到 Maven。请安装 Maven 或放到 .tools/apache-maven-3.9.6/"
}

function Test-PortOpen([int]$Port) {
    try {
        $client = New-Object System.Net.Sockets.TcpClient
        $iar = $client.BeginConnect("127.0.0.1", $Port, $null, $null)
        $ok = $iar.AsyncWaitHandle.WaitOne(800)
        if (-not $ok) { $client.Close(); return $false }
        $client.EndConnect($iar)
        $client.Close()
        return $true
    } catch {
        return $false
    }
}

function Start-MySqlIfNeeded {
    if (Test-PortOpen 3306) {
        Write-Host "MySQL 已在 3306 监听" -ForegroundColor Green
        return
    }

    Write-Step "启动 MySQL"
    $svc = Get-Service -Name "MySQL" -ErrorAction SilentlyContinue
    if ($svc -and $svc.Status -ne "Running") {
        try {
            Start-Service -Name MySQL -ErrorAction Stop
            Start-Sleep -Seconds 3
        } catch {
            Write-Host "Windows 服务启动失败（可能缺管理员权限），尝试直接启动 mysqld..." -ForegroundColor Yellow
        }
    }

    if (-not (Test-PortOpen 3306)) {
        $mysqld = $null
        $svcInfo = Get-CimInstance Win32_Service -Filter "Name='MySQL'" -ErrorAction SilentlyContinue
        if ($svcInfo -and $svcInfo.PathName) {
            if ($svcInfo.PathName -match '"?([^"]*mysqld\.exe)') {
                $mysqld = $Matches[1]
            }
        }
        if (-not $mysqld) {
            foreach ($candidate in @(
                "E:\mysql-9.7.1-winx64\bin\mysqld.exe",
                "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe",
                "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe"
            )) {
                if (Test-Path $candidate) { $mysqld = $candidate; break }
            }
        }
        if (-not $mysqld -or -not (Test-Path $mysqld)) {
            throw "3306 未监听，且无法自动启动 MySQL。请手动启动 MySQL 后重试。"
        }

        $defaults = Join-Path (Split-Path (Split-Path $mysqld -Parent) -Parent) "my.ini"
        $args = @()
        if (Test-Path $defaults) {
            $args += "--defaults-file=$defaults"
        }
        Start-Process -FilePath $mysqld -ArgumentList $args -WindowStyle Hidden | Out-Null
        Start-Sleep -Seconds 5
    }

    if (-not (Test-PortOpen 3306)) {
        throw "MySQL 仍未在 3306 监听，请先手动启动数据库服务。"
    }
    Write-Host "MySQL 已启动（3306）" -ForegroundColor Green
}

function Get-MysqlConnectorJar {
    Get-ChildItem "$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\*\mysql-connector-j-*.jar" |
        Sort-Object FullName -Descending |
        Select-Object -First 1 -ExpandProperty FullName
}

function Invoke-DbBootstrap {
    Write-Step "初始化 / 刷新数据库（schema + init-data）"
    $jar = Get-MysqlConnectorJar
    if (-not $jar) {
        throw "未找到 mysql-connector-j，请先编译项目"
    }
    $workDir = Join-Path $env:TEMP "building-manos-db"
    New-Item -ItemType Directory -Path $workDir -Force | Out-Null
    Copy-Item (Join-Path $Root "scripts\DbBootstrap.java") $workDir -Force
    javac -encoding UTF-8 -cp $jar (Join-Path $workDir "DbBootstrap.java")
    Push-Location $Root
    try {
        java -cp "$workDir;$jar" DbBootstrap sql/schema.sql sql/init-data.sql
    } finally {
        Pop-Location
    }
    Write-Host "数据库就绪" -ForegroundColor Green
}

function Wait-HttpOk([string]$Url, [int]$TimeoutSec = 60) {
    $deadline = (Get-Date).AddSeconds($TimeoutSec)
    while ((Get-Date) -lt $deadline) {
        try {
            $resp = Invoke-RestMethod -Uri $Url -TimeoutSec 2
            if ($resp.code -eq 0) { return $true }
        } catch {
            # keep waiting
        }
        Start-Sleep -Seconds 1
    }
    return $false
}

Write-Host "========================================" -ForegroundColor Green
Write-Host "  Building ManOS 一键启动" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

Start-MySqlIfNeeded

$mvn = Resolve-Maven
Write-Step "编译后端"
& $mvn -q compile

if (-not $SkipDbInit) {
    Invoke-DbBootstrap
} else {
    Write-Host "已跳过数据库初始化（-SkipDbInit）" -ForegroundColor Yellow
}

# 若端口占用则复用已有进程
$apiAlready = Test-PortOpen 8080
$webAlready = Test-PortOpen 5173

if (-not $apiAlready) {
    Write-Step "启动 API（新窗口，端口 8080）"
    Start-Process -FilePath "powershell.exe" -ArgumentList @(
        "-NoExit", "-NoProfile", "-ExecutionPolicy", "Bypass",
        "-File", (Join-Path $Root "scripts\run-api.ps1")
    ) -WorkingDirectory $Root | Out-Null
} else {
    Write-Host "8080 已占用，复用现有 API" -ForegroundColor Yellow
}

if (-not $webAlready) {
    Write-Step "启动前端（新窗口，端口 5173）"
    Start-Process -FilePath "powershell.exe" -ArgumentList @(
        "-NoExit", "-NoProfile", "-ExecutionPolicy", "Bypass",
        "-File", (Join-Path $Root "scripts\run-web.ps1")
    ) -WorkingDirectory $Root | Out-Null
} else {
    Write-Host "5173 已占用，复用现有前端" -ForegroundColor Yellow
}

Write-Step "等待 API 健康检查"
if (Wait-HttpOk "http://127.0.0.1:8080/api/health" 90) {
    $health = Invoke-RestMethod "http://127.0.0.1:8080/api/health"
    Write-Host ("API OK  status={0}  db={1}" -f $health.data.status, $health.data.db) -ForegroundColor Green
} else {
    Write-Host "API 健康检查超时，请查看 API 窗口日志。" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "前端: http://127.0.0.1:5173/" -ForegroundColor Green
Write-Host "API : http://127.0.0.1:8080/api/health" -ForegroundColor Green
Write-Host "关闭对应窗口即可停止服务。" -ForegroundColor DarkGray

if (-not $NoBrowser) {
    Start-Process "http://127.0.0.1:5173/"
}
