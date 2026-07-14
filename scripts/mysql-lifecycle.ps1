# Shared MySQL start/stop helpers for local Windows scripts.
# Dot-source: . "$PSScriptRoot\mysql-lifecycle.ps1"

$script:BuildingManosMysqlStartedByUs = $false
$script:BuildingManosMysqldProcessId = $null

function Test-BuildingPortOpen([int]$Port) {
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

function Find-BuildingMysqld {
    $svcInfo = Get-CimInstance Win32_Service -Filter "Name='MySQL'" -ErrorAction SilentlyContinue
    if ($svcInfo -and $svcInfo.PathName -match '"?([^"]*mysqld\.exe)') {
        return $Matches[1]
    }
    foreach ($candidate in @(
            "E:\mysql-9.7.1-winx64\bin\mysqld.exe",
            "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe",
            "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe"
        )) {
        if (Test-Path $candidate) { return $candidate }
    }
    return $null
}

function Start-BuildingMySqlIfNeeded {
    $script:BuildingManosMysqlStartedByUs = $false
    $script:BuildingManosMysqldProcessId = $null

    if (Test-BuildingPortOpen 3306) {
        Write-Host "MySQL already listening on 3306 (will NOT stop on exit)" -ForegroundColor Green
        return
    }

    Write-Host "==> starting MySQL" -ForegroundColor Cyan
    $svc = Get-Service -Name "MySQL" -ErrorAction SilentlyContinue
    if ($svc -and $svc.Status -ne "Running") {
        try {
            Start-Service -Name MySQL -ErrorAction Stop
            Start-Sleep -Seconds 3
            if (Test-BuildingPortOpen 3306) {
                $script:BuildingManosMysqlStartedByUs = $true
                Write-Host "MySQL Windows service started by this script" -ForegroundColor Green
                return
            }
        } catch {
            Write-Host "Start-Service MySQL failed (admin?), trying mysqld.exe..." -ForegroundColor Yellow
        }
    }

    $mysqld = Find-BuildingMysqld
    if (-not $mysqld) {
        throw "Port 3306 is down and mysqld.exe was not found. Start MySQL manually."
    }

    $defaults = Join-Path (Split-Path (Split-Path $mysqld -Parent) -Parent) "my.ini"
    $args = @()
    if (Test-Path $defaults) {
        $args += "--defaults-file=$defaults"
    }
    $proc = Start-Process -FilePath $mysqld -ArgumentList $args -WindowStyle Hidden -PassThru
    $script:BuildingManosMysqldProcessId = $proc.Id
    $script:BuildingManosMysqlStartedByUs = $true
    Start-Sleep -Seconds 5

    if (-not (Test-BuildingPortOpen 3306)) {
        throw "MySQL still not listening on 3306 after start attempt."
    }
    Write-Host ("MySQL mysqld started by this script (pid={0})" -f $proc.Id) -ForegroundColor Green
}

function Stop-BuildingMySqlIfWeStarted {
    if (-not $script:BuildingManosMysqlStartedByUs) {
        Write-Host "Skip MySQL stop (was already running before script)" -ForegroundColor DarkGray
        return
    }

    Write-Host "==> stopping MySQL started by this script" -ForegroundColor Cyan
    $svc = Get-Service -Name "MySQL" -ErrorAction SilentlyContinue
    if ($svc -and $svc.Status -eq "Running") {
        try {
            Stop-Service -Name MySQL -Force -ErrorAction Stop
            Write-Host "MySQL Windows service stopped" -ForegroundColor Green
            $script:BuildingManosMysqlStartedByUs = $false
            return
        } catch {
            Write-Host "Stop-Service failed, trying to kill mysqld..." -ForegroundColor Yellow
        }
    }

    if ($script:BuildingManosMysqldProcessId) {
        Stop-Process -Id $script:BuildingManosMysqldProcessId -Force -ErrorAction SilentlyContinue
    }
    Get-Process -Name "mysqld" -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
    $script:BuildingManosMysqlStartedByUs = $false
    Write-Host "mysqld stop attempted" -ForegroundColor Green
}

function Set-BuildingDbEnv {
    # Local course machine default; override before launch if needed.
    if (-not $env:DB_USER) { $env:DB_USER = "root" }
    if (-not $env:DB_PASSWORD) { $env:DB_PASSWORD = "tarena" }
    Write-Host ("DB env: user={0} password=***" -f $env:DB_USER) -ForegroundColor DarkGray
}
