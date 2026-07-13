param(
    [ValidateSet("install", "dev", "build")]
    [string]$Action = "dev"
)

$ErrorActionPreference = "Stop"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$frontendRoot = Join-Path $repoRoot "frontend"
$codexRuntime = Join-Path $env:USERPROFILE ".cache/codex-runtimes/codex-primary-runtime/dependencies"
$codexNode = Join-Path $codexRuntime "node/bin/node.exe"
$codexPnpm = Join-Path $codexRuntime "bin/fallback/pnpm.cmd"

function Resolve-NodeExecutable {
    if (Test-Path -LiteralPath $codexNode) {
        return $codexNode
    }

    $systemNode = Get-Command node -ErrorAction SilentlyContinue
    if ($null -eq $systemNode) {
        throw "未找到 Node.js。请安装 Node.js 22.12+，或在 Codex 环境中运行本脚本。"
    }
    return $systemNode.Source
}

function Assert-NodeVersion([string]$nodeExecutable) {
    $versionText = (& $nodeExecutable --version).TrimStart("v")
    $version = [System.Version]::Parse($versionText)
    $supported = ($version.Major -eq 20 -and $version.Minor -ge 19) -or $version.Major -ge 22
    if (-not $supported) {
        throw "当前 Node.js 为 $versionText；Vite 需要 Node.js 20.19+ 或 22.12+。"
    }
    Write-Host ">>> Node.js $versionText" -ForegroundColor DarkGray
}

function Resolve-PnpmExecutable {
    if (Test-Path -LiteralPath $codexPnpm) {
        return $codexPnpm
    }

    $systemPnpm = Get-Command pnpm -ErrorAction SilentlyContinue
    if ($null -eq $systemPnpm) {
        throw "未找到 pnpm。可安装 Node.js 22.12+ 后运行 corepack enable，或在 Codex 环境中运行本脚本。"
    }
    return $systemPnpm.Source
}

if (-not (Test-Path -LiteralPath $frontendRoot)) {
    throw "未找到前端目录：$frontendRoot"
}

$node = Resolve-NodeExecutable
Assert-NodeVersion $node
Set-Location -LiteralPath $frontendRoot

switch ($Action) {
    "install" {
        $pnpm = Resolve-PnpmExecutable
        Write-Host ">>> 安装前端依赖" -ForegroundColor Cyan
        & $pnpm install
        if ($LASTEXITCODE -ne 0) { throw "前端依赖安装失败，退出码：$LASTEXITCODE" }
    }
    "dev" {
        $vite = Join-Path $frontendRoot "node_modules/vite/bin/vite.js"
        if (-not (Test-Path -LiteralPath $vite)) {
            throw "前端依赖尚未安装。请先运行：.\scripts\frontend.ps1 install"
        }
        Write-Host ">>> 启动 Building ManOS 前端 P0" -ForegroundColor Cyan
        Write-Host ">>> 浏览器访问 http://127.0.0.1:5173" -ForegroundColor Green
        & $node $vite --host 127.0.0.1 --port 5173
        if ($LASTEXITCODE -ne 0) { throw "前端开发服务器异常退出，退出码：$LASTEXITCODE" }
    }
    "build" {
        $vueTsc = Join-Path $frontendRoot "node_modules/vue-tsc/bin/vue-tsc.js"
        $vite = Join-Path $frontendRoot "node_modules/vite/bin/vite.js"
        if (-not (Test-Path -LiteralPath $vueTsc) -or -not (Test-Path -LiteralPath $vite)) {
            throw "前端依赖尚未安装。请先运行：.\scripts\frontend.ps1 install"
        }
        Write-Host ">>> TypeScript 检查" -ForegroundColor Cyan
        & $node $vueTsc -b
        if ($LASTEXITCODE -ne 0) { throw "TypeScript 检查失败，退出码：$LASTEXITCODE" }

        Write-Host ">>> Vite 生产构建" -ForegroundColor Cyan
        & $node $vite build
        if ($LASTEXITCODE -ne 0) { throw "前端生产构建失败，退出码：$LASTEXITCODE" }
    }
}
