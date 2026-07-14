# Building ManOS one-click: MySQL -> API + frontend -> browser
# Default: DO NOT wipe MySQL (preserves frontend CRUD). Use -InitDb to reseed demo data.
# Exit (Enter / Ctrl+C): stop API/frontend windows we started; stop MySQL only if we started it.
param(
    [switch]$InitDb,
    [switch]$SkipDbInit,
    [switch]$NoBrowser,
    [switch]$KeepMySql
)

$ErrorActionPreference = "Stop"
$Root = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $Root
. (Join-Path $PSScriptRoot "mysql-lifecycle.ps1")

$script:ChildProcs = @()

function Write-Step([string]$Message) {
    Write-Host ""
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Resolve-Maven {
    $cmd = Get-Command mvn -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
    $portable = Join-Path $Root ".tools\apache-maven-3.9.6\bin\mvn.cmd"
    if (Test-Path $portable) { return (Resolve-Path $portable).Path }
    throw "Maven not found."
}

function Get-MysqlConnectorJar {
    Get-ChildItem "$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\*\mysql-connector-j-*.jar" |
        Sort-Object FullName -Descending |
        Select-Object -First 1 -ExpandProperty FullName
}

function Invoke-DbBootstrap {
    Write-Step "init database (schema + init-data)"
    $jar = Get-MysqlConnectorJar
    if (-not $jar) { throw "mysql-connector-j not found; compile project first" }
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
    Write-Host "database ready" -ForegroundColor Green
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

function Invoke-Cleanup {
    Write-Host ""
    Write-Step "cleanup"
    Stop-ChildScripts
    if (-not $KeepMySql) {
        Stop-BuildingMySqlIfWeStarted
    } else {
        Write-Host "KeepMySql: leave MySQL running" -ForegroundColor Yellow
    }
}

Write-Host "========================================" -ForegroundColor Green
Write-Host "  Building ManOS one-click start" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

try {
    Set-BuildingDbEnv
    Start-BuildingMySqlIfNeeded

    $mvn = Resolve-Maven
    Write-Step "compile backend"
    & $mvn -q compile

    # IMPORTANT: init-data.sql deletes all rows then re-inserts demo data.
    # Only run when explicitly requested, otherwise user edits appear "lost" after restart.
    if ($InitDb -and -not $SkipDbInit) {
        Write-Host "InitDb: reseeding demo data (will DELETE existing rows)" -ForegroundColor Yellow
        Invoke-DbBootstrap
    } else {
        Write-Host "Skip DB reseed (pass -InitDb to wipe & load sql/init-data.sql)" -ForegroundColor DarkGray
    }

    $apiAlready = Test-BuildingPortOpen 8080
    $webAlready = Test-BuildingPortOpen 5173

    if (-not $apiAlready) {
        Write-Step "start API window (8080)"
        # Must use -ApiOnly to avoid nested double-launch
        Start-ChildScript "run-api.ps1" @("-ApiOnly") | Out-Null
    } else {
        Write-Host "8080 busy, reuse API" -ForegroundColor Yellow
    }

    if (-not $webAlready) {
        Write-Step "start frontend window (5173)"
        Start-ChildScript "run-web.ps1" | Out-Null
    } else {
        Write-Host "5173 busy, reuse frontend" -ForegroundColor Yellow
    }

    Write-Step "wait API health"
    if (Wait-HttpOk "http://127.0.0.1:8080/api/health" 90) {
        $health = Invoke-RestMethod "http://127.0.0.1:8080/api/health"
        Write-Host ("API OK  status={0}  db={1}" -f $health.data.status, $health.data.db) -ForegroundColor Green
        if ($health.data.db -ne "UP") {
            Write-Host "WARNING: db is DOWN. Check password (default tarena) / MySQL." -ForegroundColor Yellow
        }
    } else {
        Write-Host "API health timeout" -ForegroundColor Yellow
    }

    Write-Host ""
    Write-Host "Web : http://127.0.0.1:5173/" -ForegroundColor Green
    Write-Host "API : http://127.0.0.1:8080/api/health" -ForegroundColor Green

    if (-not $NoBrowser) {
        Start-Process "http://127.0.0.1:5173/"
    }

    Write-Host ""
    Write-Host "Press Enter to stop API/frontend (and MySQL if this script started it)..." -ForegroundColor Cyan
    [void][Console]::ReadLine()
}
finally {
    Invoke-Cleanup
}
