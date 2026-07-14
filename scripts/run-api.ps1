# Start API; default = also frontend + browser + MySQL lifecycle
#   powershell -File scripts\run-api.ps1
#   powershell -File scripts\run-api.ps1 -ApiOnly
#   powershell -File scripts\run-api.ps1 -NoBrowser
#   powershell -File scripts\run-api.ps1 -KeepMySql
param(
    [switch]$ApiOnly,
    [switch]$NoBrowser,
    [switch]$KeepMySql
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $Root
. (Join-Path $PSScriptRoot "mysql-lifecycle.ps1")

$script:ChildProcs = @()

function Resolve-Maven {
    $cmd = Get-Command mvn -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
    $portable = Join-Path $Root ".tools\apache-maven-3.9.6\bin\mvn.cmd"
    if (Test-Path $portable) { return (Resolve-Path $portable).Path }
    throw "Maven not found."
}

function Wait-HttpOk([string]$Url, [int]$TimeoutSec = 90) {
    $deadline = (Get-Date).AddSeconds($TimeoutSec)
    while ((Get-Date) -lt $deadline) {
        try {
            $resp = Invoke-RestMethod -Uri $Url -TimeoutSec 2
            if ($resp.code -eq 0) { return $true }
        } catch { }
        Start-Sleep -Seconds 1
    }
    return $false
}

function Wait-PortOpen([int]$Port, [int]$TimeoutSec = 90) {
    $deadline = (Get-Date).AddSeconds($TimeoutSec)
    while ((Get-Date) -lt $deadline) {
        if (Test-BuildingPortOpen $Port) { return $true }
        Start-Sleep -Seconds 1
    }
    return $false
}

function Start-ChildScript([string]$ScriptName, [string[]]$ExtraArgs) {
    $file = Join-Path $PSScriptRoot $ScriptName
    $argList = @(
        "-NoExit", "-NoProfile", "-ExecutionPolicy", "Bypass",
        "-File", $file
    )
    if ($ExtraArgs) { $argList += $ExtraArgs }
    $p = Start-Process -FilePath "powershell.exe" -ArgumentList $argList -WorkingDirectory $Root -PassThru
    $script:ChildProcs += $p
    return $p
}

function Stop-ChildScripts {
    foreach ($p in $script:ChildProcs) {
        if ($null -eq $p) { continue }
        try {
            $tree = Get-CimInstance Win32_Process -Filter ("ParentProcessId={0}" -f $p.Id) -ErrorAction SilentlyContinue
            foreach ($c in $tree) {
                Stop-Process -Id $c.ProcessId -Force -ErrorAction SilentlyContinue
            }
            if (-not $p.HasExited) {
                Stop-Process -Id $p.Id -Force -ErrorAction SilentlyContinue
            }
        } catch { }
    }
    $script:ChildProcs = @()
}

$mvn = Resolve-Maven

if ($ApiOnly) {
    # Child window dedicated to Java logs (do NOT use mvn -q here)
    Set-BuildingDbEnv
    $env:JAVA_TOOL_OPTIONS = "-Dorg.slf4j.simpleLogger.showDateTime=true -Dorg.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss -Dorg.slf4j.simpleLogger.defaultLogLevel=info"
    Write-Host "==> mvn compile" -ForegroundColor Cyan
    & $mvn -q compile
    Write-Host "==> start API (ServerMain) — Java logs below" -ForegroundColor Cyan
    Write-Host "    http://0.0.0.0:8080/api/health" -ForegroundColor DarkGray
    & $mvn exec:java
    exit $LASTEXITCODE
}

try {
    Set-BuildingDbEnv
    Start-BuildingMySqlIfNeeded

    Write-Host "==> mvn compile" -ForegroundColor Cyan
    & $mvn -q compile

    $apiAlready = Test-BuildingPortOpen 8080
    $webAlready = Test-BuildingPortOpen 5173

    if (-not $apiAlready) {
        Write-Host "==> start API (new window, 8080)" -ForegroundColor Cyan
        Start-ChildScript "run-api.ps1" @("-ApiOnly") | Out-Null
    } else {
        Write-Host "port 8080 in use, reuse existing API" -ForegroundColor Yellow
    }

    if (-not $webAlready) {
        Write-Host "==> start frontend (new window, 5173)" -ForegroundColor Cyan
        Start-ChildScript "run-web.ps1" | Out-Null
    } else {
        Write-Host "port 5173 in use, reuse existing frontend" -ForegroundColor Yellow
    }

    Write-Host "==> waiting API health..." -ForegroundColor Cyan
    if (Wait-HttpOk "http://127.0.0.1:8080/api/health" 90) {
        $health = Invoke-RestMethod "http://127.0.0.1:8080/api/health"
        Write-Host ("API OK  status={0}  db={1}" -f $health.data.status, $health.data.db) -ForegroundColor Green
        if ($health.data.db -ne "UP") {
            Write-Host "WARNING: db DOWN. password default is tarena; restart after fix." -ForegroundColor Yellow
        }
    } else {
        Write-Host "API health timeout." -ForegroundColor Yellow
    }

    Write-Host "==> waiting frontend port 5173..." -ForegroundColor Cyan
    if (Wait-PortOpen 5173 90) {
        Write-Host "frontend ready  http://127.0.0.1:5173/" -ForegroundColor Green
    } else {
        Write-Host "frontend not ready in time." -ForegroundColor Yellow
    }

    Write-Host ""
    Write-Host "Web : http://127.0.0.1:5173/" -ForegroundColor Green
    Write-Host "API : http://127.0.0.1:8080/api/health" -ForegroundColor Green

    if (-not $NoBrowser) {
        Write-Host "==> open browser" -ForegroundColor Cyan
        Start-Process "http://127.0.0.1:5173/"
    }

    Write-Host ""
    Write-Host "Press Enter to stop API/frontend (and MySQL if started by this script)..." -ForegroundColor Cyan
    [void][Console]::ReadLine()
}
finally {
    Write-Host "==> cleanup" -ForegroundColor Cyan
    Stop-ChildScripts
    if (-not $KeepMySql) {
        Stop-BuildingMySqlIfWeStarted
    }
}
