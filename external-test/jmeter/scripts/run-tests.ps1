# run-tests.ps1 — Ejecutor de pruebas de rendimiento JMeter (Windows PowerShell)
# ms-notificaciones-util
#
# Uso:
#   $env:AMBIENTE_PIPE="dev";  .\scripts\run-tests.ps1 -Perfil smoke
#   $env:AMBIENTE_PIPE="qa";   .\scripts\run-tests.ps1 -Perfil load
#   $env:AMBIENTE_PIPE="prod"; .\scripts\run-tests.ps1 -Perfil smoke
#   $env:AMBIENTE_PIPE="dev";  .\scripts\run-tests.ps1 -Perfil all
#
# Variables de entorno opcionales:
#   $env:JWT_SECRET   — sobreescribe jwt.secret del properties
#   $env:JMETER_HOME  — ruta a la instalacion de JMeter (si no está en PATH)
#   $env:QA_HOST      — host QA (requerido si AMBIENTE_PIPE=qa)
#   $env:PROD_HOST    — host produccion (requerido si AMBIENTE_PIPE=prod)
#   $env:PROD_PORT    — puerto produccion

param(
    [Parameter(Position=0)]
    [ValidateSet("smoke","load","carga","concurrencia","concurrent","stress","all","todos")]
    [string]$Perfil = "smoke"
)

$ErrorActionPreference = "Stop"

# ── Configuración base ─────────────────────────────────────────────────────────
$ScriptDir  = Split-Path -Parent $MyInvocation.MyCommand.Path
$BaseDir    = Split-Path -Parent $ScriptDir
$PlansDir   = Join-Path $BaseDir "plans"
$ConfigDir  = Join-Path $BaseDir "config"
$ResultsDir = Join-Path $BaseDir "results"
$ReportsDir = Join-Path $BaseDir "reports"

$Ambiente   = $env:AMBIENTE_PIPE ?? "dev"
$Timestamp  = Get-Date -Format "yyyyMMdd-HHmmss"
$JMeterBin  = if ($env:JMETER_HOME) { Join-Path $env:JMETER_HOME "bin\jmeter.bat" } else { "jmeter" }

# ── Protección de Producción ───────────────────────────────────────────────────
if ($Ambiente -eq "prod" -and $Perfil -ne "smoke") {
    Write-Error "PROTECCION PROD: En produccion solo se permite el perfil 'smoke' (endpoints publicos GET/HEAD).`nUso: `$env:AMBIENTE_PIPE='prod'; .\run-tests.ps1 -Perfil smoke"
    exit 1
}

# ── Validaciones ───────────────────────────────────────────────────────────────
$PropsFile = Join-Path $ConfigDir "${Ambiente}.properties"
if (-not (Test-Path $PropsFile)) {
    Write-Error "Archivo de propiedades no encontrado: $PropsFile`nAmbientes validos: dev | qa | prod"
    exit 1
}

if (-not (Get-Command jmeter -ErrorAction SilentlyContinue) -and -not $env:JMETER_HOME) {
    Write-Error "JMeter no encontrado. Instalar JMeter o definir `$env:JMETER_HOME.`nDescarga: https://jmeter.apache.org/download_jmeter.cgi"
    exit 1
}

New-Item -ItemType Directory -Force -Path $ResultsDir | Out-Null
New-Item -ItemType Directory -Force -Path $ReportsDir | Out-Null

# ── Override de propiedades desde variables de entorno ────────────────────────
$ExtraProps = @()
if ($env:JWT_SECRET)  { $ExtraProps += "-Djwt.secret=$($env:JWT_SECRET)" }
if ($env:QA_HOST)     { $ExtraProps += "-Dhost=$($env:QA_HOST)" }
if ($env:PROD_HOST)   { $ExtraProps += "-Dhost=$($env:PROD_HOST)" }
if ($env:PROD_PORT)   { $ExtraProps += "-Dport=$($env:PROD_PORT)" }

# ── Función de ejecución ───────────────────────────────────────────────────────
function Invoke-JMeterPlan {
    param([string]$PlanName)

    $PlanFile   = Join-Path $PlansDir "${PlanName}.jmx"
    $JtlFile    = Join-Path $ResultsDir "${PlanName}-${Ambiente}-${Timestamp}.jtl"
    $ReportDir  = Join-Path $ReportsDir "${PlanName}-${Ambiente}-${Timestamp}"

    if (-not (Test-Path $PlanFile)) {
        Write-Error "Plan no encontrado: $PlanFile"
        return
    }

    Write-Host ""
    Write-Host "══════════════════════════════════════════════════════════════════" -ForegroundColor Cyan
    Write-Host "  Ejecutando: $PlanName" -ForegroundColor Cyan
    Write-Host "  Ambiente:   $Ambiente" -ForegroundColor Cyan
    Write-Host "  Timestamp:  $Timestamp" -ForegroundColor Cyan
    Write-Host "══════════════════════════════════════════════════════════════════" -ForegroundColor Cyan

    $Args = @(
        "-n",
        "-t", $PlanFile,
        "-q", $PropsFile,
        "-Dambiente=$Ambiente",
        "-l", $JtlFile,
        "-e", "-o", $ReportDir
    ) + $ExtraProps

    & $JMeterBin @Args

    if ($LASTEXITCODE -ne 0) {
        Write-Warning "JMeter terminó con código de salida: $LASTEXITCODE"
    }

    Write-Host ""
    Write-Host "Resultados JTL: $JtlFile" -ForegroundColor Green
    Write-Host "Reporte HTML:   $ReportDir\index.html" -ForegroundColor Green
    Write-Host ""
}

# ── Ejecución según perfil ─────────────────────────────────────────────────────
switch ($Perfil) {
    "smoke" {
        Invoke-JMeterPlan "ms-notificaciones-smoke"
    }
    { $_ -in "load","carga" } {
        Invoke-JMeterPlan "ms-notificaciones-carga"
    }
    { $_ -in "concurrencia","concurrent","stress" } {
        Invoke-JMeterPlan "ms-notificaciones-concurrencia"
    }
    { $_ -in "all","todos" } {
        Write-Host "Ejecutando todos los perfiles..." -ForegroundColor Yellow
        Invoke-JMeterPlan "ms-notificaciones-smoke"
        Invoke-JMeterPlan "ms-notificaciones-carga"
        Invoke-JMeterPlan "ms-notificaciones-concurrencia"
    }
}

Write-Host "Pruebas de rendimiento completadas." -ForegroundColor Green