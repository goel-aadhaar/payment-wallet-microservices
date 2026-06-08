#requires -Version 5.1
<#
.SYNOPSIS
    Stop everything started by run-local.ps1: the Spring Boot services, the Next.js dev server,
    and (unless -KeepKafka) the Kafka/Zookeeper Docker containers.

.PARAMETER KeepKafka  Leave the Kafka/Zookeeper containers running.
.PARAMETER DownKafka   Use 'docker compose down' (remove containers) instead of 'stop'.
#>
[CmdletBinding()]
param(
    [switch]$KeepKafka,
    [switch]$DownKafka
)

$ErrorActionPreference = 'Continue'
$Root      = $PSScriptRoot
$StateFile = Join-Path $Root '.run-local.json'

# Canonical ports owned by the stack (matches run-local.ps1).
$Ports = 8080, 8081, 8082, 8083, 8084, 8088, 3000

function Write-Step { param([string]$Msg) Write-Host "`n==> $Msg" -ForegroundColor Cyan }
function Write-Ok   { param([string]$Msg) Write-Host "    [OK]   $Msg" -ForegroundColor Green }
function Write-Warn { param([string]$Msg) Write-Host "    [WARN] $Msg" -ForegroundColor Yellow }

function Stop-Tree {
    # Kill a process and its descendants (npm.cmd spawns node, etc.).
    param([int]$ProcId)
    try {
        Get-CimInstance Win32_Process -Filter "ParentProcessId=$ProcId" -ErrorAction SilentlyContinue |
            ForEach-Object { Stop-Tree -ProcId $_.ProcessId }
        Stop-Process -Id $ProcId -Force -ErrorAction Stop
        return $true
    } catch { return $false }
}

Write-Step "Stopping Payment Wallet stack"

# 1. Kill by recorded PID (and children) from the state file.
if (Test-Path $StateFile) {
    try {
        $state = Get-Content $StateFile -Raw | ConvertFrom-Json
        foreach ($s in $state) {
            if (Stop-Tree -ProcId $s.pid) { Write-Ok ("{0,-20} pid {1} stopped" -f $s.name, $s.pid) }
        }
    } catch { Write-Warn "Could not parse $StateFile : $($_.Exception.Message)" }
}

# 2. Belt-and-braces: free anything still listening on our ports.
Write-Step "Freeing ports"
foreach ($p in $Ports) {
    try {
        $owners = Get-NetTCPConnection -LocalPort $p -State Listen -ErrorAction SilentlyContinue |
                  Select-Object -ExpandProperty OwningProcess -Unique
        foreach ($procId in $owners) {
            if ($procId -and $procId -ne 0) {
                if (Stop-Tree -ProcId $procId) { Write-Ok "Port $p freed (pid $procId)" }
            }
        }
    } catch { Write-Warn "Port $p check failed: $($_.Exception.Message)" }
}

# 3. Infra: PostgreSQL + Kafka / Zookeeper.
if (-not $KeepKafka) {
    Write-Step "Stopping infra (PostgreSQL + Kafka + Zookeeper) (Docker)"
    if (Get-Command docker -ErrorAction SilentlyContinue) {
        Push-Location $Root
        try {
            # 'stop' preserves data (pgdata volume); 'down' removes containers (volume persists).
            $action = if ($DownKafka) { @('down') } else { @('stop', 'zookeeper', 'kafka', 'postgres') }
            & docker compose version *> $null
            if ($LASTEXITCODE -eq 0) { & docker compose @action } else { & docker-compose @action }
            Write-Ok "Infra containers handled."
        } catch { Write-Warn "Docker compose stop failed: $($_.Exception.Message)" }
        finally { Pop-Location }
    } else { Write-Warn "Docker not found - skipping infra shutdown." }
} else {
    Write-Warn "Leaving infra running (-KeepKafka)."
}

Remove-Item $StateFile -ErrorAction SilentlyContinue
Write-Step "Done."
