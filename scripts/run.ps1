# Default local entry: MySQL + API + frontend + open browser
# Usage:
#   ./scripts/run.ps1
#   ./scripts/run.ps1 -InitDb        # wipe DB and load demo data (destructive)
#   ./scripts/run.ps1 -NoBrowser
# Console menu: ./scripts/run-cli.ps1
param(
    [switch]$InitDb,
    [switch]$SkipDbInit,
    [switch]$NoBrowser,
    [switch]$KeepMySql
)

$ErrorActionPreference = "Stop"
$here = $PSScriptRoot
$argsList = @()
if ($InitDb) { $argsList += "-InitDb" }
if ($SkipDbInit) { $argsList += "-SkipDbInit" }
if ($NoBrowser) { $argsList += "-NoBrowser" }
if ($KeepMySql) { $argsList += "-KeepMySql" }

Write-Host "==> ./scripts/run.ps1 -> run_building_os (API + Web + browser)" -ForegroundColor Cyan
Write-Host "    Java logs: watch the API PowerShell window" -ForegroundColor DarkGray
& (Join-Path $here "run_building_os.ps1") @argsList
