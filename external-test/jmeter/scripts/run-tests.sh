#!/usr/bin/env bash
# run-tests.sh  — Ejecutor de pruebas de rendimiento JMeter
# ms-notificaciones-util
#
# Uso:
#   AMBIENTE_PIPE=dev  ./scripts/run-tests.sh smoke
#   AMBIENTE_PIPE=qa   ./scripts/run-tests.sh load
#   AMBIENTE_PIPE=prod ./scripts/run-tests.sh concurrencia
#   AMBIENTE_PIPE=dev  ./scripts/run-tests.sh all
#
# Variables de entorno opcionales:
#   JWT_SECRET     — sobreescribe jwt.secret del properties
#   JMETER_HOME    — ruta al directorio de instalación de JMeter
#   QA_HOST        — host del servidor QA (requerido si AMBIENTE_PIPE=qa)
#   PROD_HOST      — host del servidor Producción (requerido si AMBIENTE_PIPE=prod)
#   PROD_PORT      — puerto del servidor Producción

set -euo pipefail

# ── Configuración base ─────────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BASE_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
PLANS_DIR="$BASE_DIR/plans"
CONFIG_DIR="$BASE_DIR/config"
RESULTS_DIR="$BASE_DIR/results"
REPORTS_DIR="$BASE_DIR/reports"

AMBIENTE="${AMBIENTE_PIPE:-dev}"
PERFIL="${1:-smoke}"
TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
JMETER_BIN="${JMETER_HOME:-jmeter}"

# ── Protección de Producción ───────────────────────────────────────────────────
if [ "$AMBIENTE" = "prod" ] && [ "$PERFIL" != "smoke" ]; then
    echo "PROTECCION PROD: En produccion solo se permite el perfil 'smoke' (GET/HEAD a endpoints públicos)."
    echo "Para ejecutar en prod: AMBIENTE_PIPE=prod ./run-tests.sh smoke"
    exit 1
fi

# ── Validaciones ───────────────────────────────────────────────────────────────
PROPS_FILE="$CONFIG_DIR/${AMBIENTE}.properties"
if [ ! -f "$PROPS_FILE" ]; then
    echo "ERROR: Archivo de propiedades no encontrado: $PROPS_FILE"
    echo "Ambientes válidos: dev | qa | prod"
    exit 1
fi

if ! command -v jmeter &> /dev/null && [ -z "${JMETER_HOME:-}" ]; then
    echo "ERROR: JMeter no encontrado. Instalar JMeter o definir JMETER_HOME."
    echo "Descarga: https://jmeter.apache.org/download_jmeter.cgi"
    exit 1
fi

mkdir -p "$RESULTS_DIR" "$REPORTS_DIR"

# ── Override de propiedades desde variables de entorno ────────────────────────
EXTRA_PROPS=""
[ -n "${JWT_SECRET:-}" ]  && EXTRA_PROPS="$EXTRA_PROPS -Djwt.secret=$JWT_SECRET"
[ -n "${QA_HOST:-}" ]     && EXTRA_PROPS="$EXTRA_PROPS -Dhost=$QA_HOST"
[ -n "${PROD_HOST:-}" ]   && EXTRA_PROPS="$EXTRA_PROPS -Dhost=$PROD_HOST"
[ -n "${PROD_PORT:-}" ]   && EXTRA_PROPS="$EXTRA_PROPS -Dport=$PROD_PORT"

# ── Función de ejecución ───────────────────────────────────────────────────────
run_plan() {
    local plan_name="$1"
    local plan_file="$PLANS_DIR/${plan_name}.jmx"
    local jtl_file="$RESULTS_DIR/${plan_name}-${AMBIENTE}-${TIMESTAMP}.jtl"
    local report_dir="$REPORTS_DIR/${plan_name}-${AMBIENTE}-${TIMESTAMP}"

    if [ ! -f "$plan_file" ]; then
        echo "ERROR: Plan no encontrado: $plan_file"
        return 1
    fi

    echo "══════════════════════════════════════════════════════════════════"
    echo "  Ejecutando: $plan_name"
    echo "  Ambiente:   $AMBIENTE"
    echo "  Timestamp:  $TIMESTAMP"
    echo "══════════════════════════════════════════════════════════════════"

    jmeter -n \
        -t "$plan_file" \
        -q "$PROPS_FILE" \
        -Dambiante="$AMBIENTE" \
        -l "$jtl_file" \
        -e -o "$report_dir" \
        $EXTRA_PROPS

    echo ""
    echo "Resultados JTL: $jtl_file"
    echo "Reporte HTML:   $report_dir/index.html"
    echo ""
}

# ── Ejecución según perfil ─────────────────────────────────────────────────────
case "$PERFIL" in
    smoke)
        run_plan "ms-notificaciones-smoke"
        ;;
    load|carga)
        run_plan "ms-notificaciones-carga"
        ;;
    concurrencia|concurrent|stress)
        run_plan "ms-notificaciones-concurrencia"
        ;;
    all|todos)
        echo "Ejecutando todos los perfiles..."
        run_plan "ms-notificaciones-smoke"
        run_plan "ms-notificaciones-carga"
        run_plan "ms-notificaciones-concurrencia"
        ;;
    *)
        echo "ERROR: Perfil desconocido: $PERFIL"
        echo "Perfiles válidos: smoke | load | concurrencia | all"
        exit 1
        ;;
esac

echo "Pruebas de rendimiento completadas."