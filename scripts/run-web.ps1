# 启动 Vue 前端（Vite）
$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..\frontend")

if (-not (Test-Path "node_modules")) {
    Write-Host ">>> installing dependencies" -ForegroundColor Cyan
    if (Get-Command pnpm -ErrorAction SilentlyContinue) {
        pnpm install
    } else {
        npm install
    }
}

Write-Host ">>> vite dev (http://0.0.0.0:5173)" -ForegroundColor Cyan
if (Get-Command pnpm -ErrorAction SilentlyContinue) {
    pnpm dev
} else {
    npm run dev
}
