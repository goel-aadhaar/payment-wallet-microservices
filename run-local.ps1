#requires -Version 5.1
<#
.SYNOPSIS
    Build and run the entire Payment Wallet stack locally:
    Kafka + Zookeeper (via Docker), the 6 Spring Boot microservices, and the Next.js frontend.

.DESCRIPTION
    Mirrors docker-compose.yml but runs the Java services and the frontend natively on the host.

    IMPORTANT port handling:
      The application.yaml defaults for wallet/notification/reward (8083/8084/8085) do NOT match
      the scheme the API gateway and README expect. This script forces the canonical scheme via
      SERVER_PORT, exactly like docker-compose does:

        api-gateway          8080
        user-service         8081
        transaction-service  8082
        notification-service 8083
        reward-service       8084
        wallet-service       8088
        frontend             3000

      It also points the gateway's *_SERVICE_URL vars at localhost (their defaults are Docker hostnames).

.PARAMETER SkipBuild     Reuse existing target/*.jar instead of running 'mvn clean package'.
.PARAMETER SkipKafka     Do not start Kafka/Zookeeper via Docker.
.PARAMETER SkipFrontend  Do not start the Next.js frontend.
.PARAMETER NoInstall     Do not run 'npm install' even if frontend/node_modules is missing.
.PARAMETER ReadyTimeoutSec  How long to wait for each service port to open (default 120).

.EXAMPLE
    .\run-local.ps1
.EXAMPLE
    .\run-local.ps1 -SkipBuild -SkipKafka
#>
[CmdletBinding()]
param(
    [switch]$SkipBuild,
    [switch]$SkipKafka,
    [switch]$SkipFrontend,
    [switch]$NoInstall,
    [int]$ReadyTimeoutSec = 120,
    # Per-service JVM max heap. This box has limited RAM and Spring Boot defaults
    # max heap to ~25% of physical RAM, so 6 services at once over-commit and thrash.
    # Capping keeps the whole stack within memory. Override if you have more RAM.
    [string]$Xmx = '384m'
)

$ErrorActionPreference = 'Stop'
$Root      = $PSScriptRoot
$LogDir    = Join-Path $Root 'logs'
$StateFile = Join-Path $Root '.run-local.json'

# ---------------------------------------------------------------------------
# Service topology (start order matters: wallet first, gateway last)
# ---------------------------------------------------------------------------
$Services = @(
    @{ Name = 'wallet-service';       Port = 8088; Env = @{} }
    # user-service's WalletClient appends /api/v1/wallets itself -> bare host here.
    @{ Name = 'user-service';         Port = 8081; Env = @{ WALLET_SERVICE_URL = 'http://localhost:8088' } }
    # transaction-service's Feign client uses WALLET_SERVICE_URL as the full base
    # (relaxed binding overrides wallet.service.url), so it MUST include the path.
    @{ Name = 'transaction-service';  Port = 8082; Env = @{ WALLET_SERVICE_URL = 'http://localhost:8088/api/v1/wallets' } }
    @{ Name = 'notification-service'; Port = 8083; Env = @{} }
    @{ Name = 'reward-service';       Port = 8084; Env = @{} }
    @{ Name = 'api-gateway';          Port = 8080; Env = @{
            USER_SERVICE_URL         = 'http://localhost:8081'
            WALLET_SERVICE_URL       = 'http://localhost:8088'
            TRANSACTION_SERVICE_URL  = 'http://localhost:8082'
            NOTIFICATION_SERVICE_URL = 'http://localhost:8083'
            REWARD_SERVICE_URL       = 'http://localhost:8084'
        } }
)

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------
function Write-Step { param([string]$Msg) Write-Host "`n==> $Msg" -ForegroundColor Cyan }
function Write-Ok   { param([string]$Msg) Write-Host "    [OK]   $Msg" -ForegroundColor Green }
function Write-Warn { param([string]$Msg) Write-Host "    [WARN] $Msg" -ForegroundColor Yellow }

function Resolve-JavaHome {
    # The machine's JAVA_HOME is often stale (e.g. points at jdk-25 instead of jdk-25.0.3).
    # Maven needs a valid one. Return a usable JDK home or $null.
    if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME 'bin\java.exe'))) {
        return $env:JAVA_HOME
    }
    $bases = @(
        "$env:ProgramFiles\Java",
        "${env:ProgramFiles(x86)}\Java",
        "$env:ProgramFiles\Eclipse Adoptium",
        "$env:LOCALAPPDATA\Programs\Eclipse Adoptium",
        "$env:ProgramFiles\Microsoft\jdk"
    )
    $found = foreach ($b in $bases) {
        if (Test-Path $b) {
            Get-ChildItem $b -Directory -ErrorAction SilentlyContinue |
                Where-Object { Test-Path (Join-Path $_.FullName 'bin\java.exe') }
        }
    }
    if ($found) { return ($found | Sort-Object Name -Descending | Select-Object -First 1).FullName }
    return $null
}

function Test-PortOpen {
    param([int]$Port)
    $c = New-Object System.Net.Sockets.TcpClient
    try {
        $iar = $c.BeginConnect('127.0.0.1', $Port, $null, $null)
        if ($iar.AsyncWaitHandle.WaitOne(400) -and $c.Connected) { return $true }
        return $false
    } catch { return $false } finally { $c.Close() }
}

function Invoke-DockerCompose {
    param([string[]]$ComposeArgs)
    # Prefer the modern 'docker compose'; fall back to legacy 'docker-compose'.
    & docker compose version *> $null
    if ($LASTEXITCODE -eq 0) { & docker compose @ComposeArgs; return $LASTEXITCODE }
    & docker-compose @ComposeArgs
    return $LASTEXITCODE
}

# ---------------------------------------------------------------------------
# Preflight
# ---------------------------------------------------------------------------
Write-Step "Payment Wallet - local runner"
New-Item -ItemType Directory -Force -Path $LogDir | Out-Null

$JavaHome = Resolve-JavaHome
if ($JavaHome) {
    $env:JAVA_HOME = $JavaHome
    Write-Ok "JAVA_HOME = $JavaHome"
    $JavaExe = Join-Path $JavaHome 'bin\java.exe'
} else {
    Write-Warn "No JDK home found; relying on 'java' from PATH."
    $JavaExe = 'java'
}

$HaveMvn    = [bool](Get-Command mvn       -ErrorAction SilentlyContinue)
$HaveDocker = [bool](Get-Command docker    -ErrorAction SilentlyContinue)

# ---------------------------------------------------------------------------
# 1. Infra (Docker): PostgreSQL (required) + Kafka/Zookeeper
# ---------------------------------------------------------------------------
if (-not $HaveDocker) {
    Write-Warn "Docker not found. PostgreSQL is required - services will fail to start without it."
    Write-Warn "Install Docker Desktop, or point SPRING_DATASOURCE_URL at an external Postgres."
} else {
    Write-Step "Starting PostgreSQL (Docker)"
    Push-Location $Root
    try {
        Invoke-DockerCompose @('up', '-d', 'postgres') | Out-Null
    } catch {
        Write-Warn "Could not start PostgreSQL: $($_.Exception.Message)"
    } finally { Pop-Location }

    # Flyway runs at boot, so the DB must be reachable before services start.
    $pgDeadline = (Get-Date).AddSeconds(60); $pgUp = $false
    while ((Get-Date) -lt $pgDeadline) {
        if (Test-PortOpen -Port 5432) { $pgUp = $true; break }
        Start-Sleep -Milliseconds 1000
    }
    if ($pgUp) { Write-Ok "PostgreSQL on localhost:5432" }
    else { Write-Warn "PostgreSQL not reachable on 5432 - services may fail to migrate." }

    if (-not $SkipKafka) {
        Write-Step "Starting Kafka + Zookeeper (Docker)"
        Push-Location $Root
        try {
            Invoke-DockerCompose @('up', '-d', 'zookeeper', 'kafka') | Out-Null
            if ($LASTEXITCODE -eq 0) { Write-Ok "Kafka broker on localhost:9092" }
            else { Write-Warn "docker compose exited with $LASTEXITCODE - continuing without Kafka." }
        } catch {
            Write-Warn "Could not start Kafka: $($_.Exception.Message) - continuing."
        } finally { Pop-Location }
    } else {
        Write-Warn "Skipping Kafka (-SkipKafka)."
    }
}

# ---------------------------------------------------------------------------
# 2. Build the Spring Boot jars
# ---------------------------------------------------------------------------
if (-not $SkipBuild) {
    Write-Step "Building backend (mvn clean package -DskipTests)"
    if ($HaveMvn) {
        Push-Location $Root
        try {
            & mvn -B -DskipTests clean package
            if ($LASTEXITCODE -ne 0) { throw "Maven reactor build failed (exit $LASTEXITCODE)." }
        } finally { Pop-Location }
    } else {
        Write-Warn "'mvn' not on PATH - building each module with its mvnw wrapper."
        foreach ($svc in $Services) {
            $dir = Join-Path $Root $svc.Name
            Push-Location $dir
            try {
                & (Join-Path $dir 'mvnw.cmd') -B -DskipTests clean package
                if ($LASTEXITCODE -ne 0) { throw "Build failed for $($svc.Name) (exit $LASTEXITCODE)." }
            } finally { Pop-Location }
        }
    }
    Write-Ok "Backend build complete."
} else {
    Write-Warn "Skipping build (-SkipBuild) - using existing target/*.jar."
}

# ---------------------------------------------------------------------------
# 3. Launch the services
# ---------------------------------------------------------------------------
Write-Step "Launching microservices"
$Started = @()

foreach ($svc in $Services) {
    $dir = Join-Path $Root $svc.Name
    $jar = Get-ChildItem -Path (Join-Path $dir 'target') -Filter '*.jar' -ErrorAction SilentlyContinue |
           Where-Object { $_.Name -notlike '*.original' } | Select-Object -First 1
    if (-not $jar) {
        Write-Warn "No jar for $($svc.Name) in target/ - did the build run? Skipping."
        continue
    }

    # Per-service environment. SERVER_PORT must be reset every iteration.
    $env:SERVER_PORT = "$($svc.Port)"
    $extraKeys = @($svc.Env.Keys)
    foreach ($k in $extraKeys) { Set-Item -Path "Env:$k" -Value $svc.Env[$k] }

    $outLog = Join-Path $LogDir "$($svc.Name).out.log"
    $errLog = Join-Path $LogDir "$($svc.Name).err.log"
    # Bounded heap + faster startup; keeps 6 simultaneous JVMs within physical RAM.
    $jvmArgs = @("-Xmx$Xmx", '-XX:MaxMetaspaceSize=192m', '-XX:TieredStopAtLevel=1', '-jar', $jar.FullName)
    $proc = Start-Process -FilePath $JavaExe `
        -ArgumentList $jvmArgs `
        -WorkingDirectory $dir `
        -RedirectStandardOutput $outLog `
        -RedirectStandardError  $errLog `
        -WindowStyle Hidden -PassThru

    Write-Ok ("{0,-20} -> http://localhost:{1}  (pid {2})" -f $svc.Name, $svc.Port, $proc.Id)
    $Started += [pscustomobject]@{ name = $svc.Name; port = $svc.Port; pid = $proc.Id }

    # Clean up per-service vars so they don't leak to the next iteration.
    Remove-Item Env:SERVER_PORT -ErrorAction SilentlyContinue
    foreach ($k in $extraKeys) { Remove-Item "Env:$k" -ErrorAction SilentlyContinue }

    # Stagger startups so 6 JVMs don't class-load/JIT simultaneously and thrash.
    Start-Sleep -Seconds 3
}

# ---------------------------------------------------------------------------
# 4. Frontend (Next.js dev server)
# ---------------------------------------------------------------------------
if (-not $SkipFrontend) {
    Write-Step "Launching frontend (Next.js)"
    $feDir = Join-Path $Root 'frontend'
    if (-not (Test-Path (Join-Path $feDir 'node_modules')) -and -not $NoInstall) {
        Write-Host "    Running npm install (first run)..." -ForegroundColor DarkGray
        Push-Location $feDir
        try {
            & npm install
            if ($LASTEXITCODE -ne 0) { throw "npm install failed (exit $LASTEXITCODE)." }
        } finally { Pop-Location }
    }
    $env:NEXT_PUBLIC_API_URL = 'http://localhost:8080'
    $feOut = Join-Path $LogDir 'frontend.out.log'
    $feErr = Join-Path $LogDir 'frontend.err.log'
    $feProc = Start-Process -FilePath 'npm.cmd' `
        -ArgumentList @('run', 'dev') `
        -WorkingDirectory $feDir `
        -RedirectStandardOutput $feOut `
        -RedirectStandardError  $feErr `
        -WindowStyle Hidden -PassThru
    Remove-Item Env:NEXT_PUBLIC_API_URL -ErrorAction SilentlyContinue
    Write-Ok ("{0,-20} -> http://localhost:3000  (pid {1})" -f 'frontend', $feProc.Id)
    $Started += [pscustomobject]@{ name = 'frontend'; port = 3000; pid = $feProc.Id }
} else {
    Write-Warn "Skipping frontend (-SkipFrontend)."
}

# Persist state so stop-local.ps1 can clean up.
$Started | ConvertTo-Json -Depth 3 | Set-Content -Path $StateFile -Encoding utf8

# ---------------------------------------------------------------------------
# 5. Wait for ports to come up
# ---------------------------------------------------------------------------
Write-Step "Waiting for services to become reachable (timeout ${ReadyTimeoutSec}s)"
$deadline = (Get-Date).AddSeconds($ReadyTimeoutSec)
$pending  = [System.Collections.ArrayList]@($Started)
while ($pending.Count -gt 0 -and (Get-Date) -lt $deadline) {
    foreach ($s in @($pending)) {
        if (Test-PortOpen -Port $s.port) {
            Write-Ok ("{0,-20} listening on {1}" -f $s.name, $s.port)
            $pending.Remove($s) | Out-Null
        }
    }
    if ($pending.Count -gt 0) { Start-Sleep -Milliseconds 1500 }
}
foreach ($s in $pending) {
    Write-Warn ("{0,-20} not up yet on {1} - check logs\{0}.out.log" -f $s.name, $s.port)
}

# ---------------------------------------------------------------------------
# Summary
# ---------------------------------------------------------------------------
Write-Step "Stack is up"
Write-Host "  Frontend      : http://localhost:3000"          -ForegroundColor White
Write-Host "  API Gateway   : http://localhost:8080"          -ForegroundColor White
Write-Host "  Swagger (user): http://localhost:8081/swagger-ui.html"
Write-Host "  Swagger (wlt) : http://localhost:8088/swagger-ui.html"
Write-Host ""
Write-Host "  Logs    : $LogDir\<service>.out.log"             -ForegroundColor DarkGray
Write-Host "  Tail one: Get-Content '$LogDir\api-gateway.out.log' -Wait -Tail 40" -ForegroundColor DarkGray
Write-Host "  Stop all: .\stop-local.ps1"                      -ForegroundColor DarkGray
Write-Host ""
