#!/bin/bash

# Script para iniciar Honeycomb de manera segura
# Autor: GitHub Copilot
# Fecha: 2025-01-05

set -e  # Detener en caso de error

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BACKEND_DIR="$SCRIPT_DIR/demo"
FRONTEND_DIR="$SCRIPT_DIR/frontend"
LOG_DIR="$SCRIPT_DIR/logs"

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # Sin color

# Crear directorio de logs si no existe
mkdir -p "$LOG_DIR"

echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  🍯 Honeycomb - Inicio Seguro         ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
echo ""

# Función para verificar si un puerto está en uso
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        return 0  # Puerto en uso
    else
        return 1  # Puerto libre
    fi
}

# 2. Verificar puertos
echo -e "${YELLOW}🔍 Verificando puertos...${NC}"

if check_port 8080; then
    echo -e "${RED}✗ Puerto 8080 (Backend) está ocupado${NC}"
    echo -e "${YELLOW}  Liberando puerto...${NC}"
    fuser -k 8080/tcp 2>/dev/null || true
    sleep 2
fi

if check_port 3000; then
    echo -e "${RED}✗ Puerto 3000 (Frontend) está ocupado${NC}"
    echo -e "${YELLOW}  Liberando puerto...${NC}"
    fuser -k 3000/tcp 2>/dev/null || true
    sleep 2
fi

echo -e "${GREEN}✓ Puertos verificados${NC}"

# 7. Resumen
echo ""
echo -e "${GREEN}╔════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║  ✅ Honeycomb iniciado correctamente  ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════╝${NC}"
echo ""
echo -e "${BLUE}📍 URLs:${NC}"
echo -e "   Frontend:  ${GREEN}http://localhost:3000${NC}"
echo -e "   Backend:   ${GREEN}http://localhost:8080${NC}"
echo -e "   Neo4j:     ${GREEN}http://localhost:7474${NC}"
echo ""
echo -e "${BLUE}📝 Logs:${NC}"
echo -e "   Backend:   tail -f $LOG_DIR/backend.log"
echo -e "   Frontend:  tail -f $LOG_DIR/frontend.log"
echo ""
echo -e "${BLUE}🛑 Para detener:${NC}"
echo -e "   ./stop-honeycomb.sh"
echo ""
echo -e "${YELLOW}💡 Tip: Usa './start-honeycomb.sh --load-data' para cargar datos de prueba${NC}"
echo -e "${YELLOW}💡 Frontend configurado en puerto 3000 con proxy para evitar CORS${NC}"
