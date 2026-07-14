# Console menu (CLI). Not the web UI.
$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..")

function Resolve-Maven {
    $cmd = Get-Command mvn -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
    $portable = Join-Path $PSScriptRoot "..\.tools\apache-maven-3.9.6\bin\mvn.cmd"
    if (Test-Path $portable) { return (Resolve-Path $portable).Path }
    throw "Maven not found."
}

. (Join-Path $PSScriptRoot "mysql-lifecycle.ps1")
Set-BuildingDbEnv
Start-BuildingMySqlIfNeeded

$mvn = Resolve-Maven
Write-Host "==> mvn compile" -ForegroundColor Cyan
& $mvn -q compile

# Avoid PowerShell eating -D: launch via cmd
Write-Host "==> start console MenuController (CLI)" -ForegroundColor Cyan
$mvnCmd = $mvn
if ($mvnCmd -notmatch '\.cmd$') { $mvnCmd = "$mvnCmd.cmd" }
cmd /c "`"$mvnCmd`" -q exec:java -Dexec.mainClass=com.building.manos.Main"
