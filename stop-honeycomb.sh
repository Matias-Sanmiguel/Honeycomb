#!/bin/bash

# Script para detener Honeycomb de manera segura
# Autor: GitHub Copilot
# Fecha: 2025-01-05

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
LOG_DIR="$SCRIPT_DIR/logs"

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  🛑 Honeycomb - Detener Servicios     ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
echo ""

# Función para detener un proceso por PID
stop_process() {
    local pid_file=$1
    local name=$2

    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "${YELLOW}🛑 Deteniendo $name (PID: $pid)...${NC}"
            kill $pid 2>/dev/null
            sleep 2

            # Si aún está corriendo, forzar
            if ps -p $pid > /dev/null 2>&1; then
                kill -9 $pid 2>/dev/null
            fi
            echo -e "${GREEN}✓ $name detenido${NC}"
        else
            echo -e "${YELLOW}⚠️  $name no estaba corriendo${NC}"
        fi
        rm -f "$pid_file"
    else
        echo -e "${YELLOW}⚠️  No se encontró archivo PID para $name${NC}"
    fi
}

# Detener Frontend
stop_process "$LOG_DIR/frontend.pid" "Frontend"

# Detener Backend
stop_process "$LOG_DIR/backend.pid" "Backend"

# Detener procesos por nombre (backup)
echo -e "${YELLOW}🔍 Verificando procesos remanentes...${NC}"
pkill -f "vite" 2>/dev/null && echo -e "${GREEN}✓ Procesos Vite detenidos${NC}" || true
pkill -f "spring-boot:run" 2>/dev/null && echo -e "${GREEN}✓ Procesos Spring Boot detenidos${NC}" || true

# Liberar puertos si es necesario
echo -e "${YELLOW}🔍 Verificando puertos...${NC}"
fuser -k 8080/tcp 2>/dev/null && echo -e "${GREEN}✓ Puerto 8080 liberado${NC}" || true
fuser -k 5173/tcp 2>/dev/null && echo -e "${GREEN}✓ Puerto 5173 liberado${NC}" || true

# Opción para detener Neo4j también
if [ "$1" == "--all" ] || [ "$1" == "-a" ]; then
    echo -e "${YELLOW}🛑 Deteniendo Neo4j...${NC}"
    docker stop honeycomb-neo4j 2>/dev/null && echo -e "${GREEN}✓ Neo4j detenido${NC}" || \
        echo -e "${YELLOW}⚠️  Neo4j no estaba corriendo${NC}"
fi

echo ""
echo -e "${GREEN}╔════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║  ✅ Servicios detenidos correctamente ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════╝${NC}"
echo ""

if [ "$1" != "--all" ] && [ "$1" != "-a" ]; then
    echo -e "${YELLOW}💡 Tip: Usa './stop-honeycomb.sh --all' para detener Neo4j también${NC}"
fi

