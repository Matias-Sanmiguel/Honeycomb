#!/bin/bash

# Script para verificar el estado de Honeycomb
# Autor: GitHub Copilot
# Fecha: 2025-01-05

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  📊 Honeycomb - Estado del Sistema    ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
echo ""

# Función para verificar servicio
check_service() {
    local url=$1
    local name=$2
    local port=$3

    echo -n -e "${BLUE}$name (puerto $port):${NC} "

    if curl -s "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Corriendo${NC}"
        return 0
    else
        echo -e "${RED}✗ No responde${NC}"
        return 1
    fi
}

# Verificar Neo4j
check_service "http://localhost:7474" "Neo4j      " "7474/7687"

# Verificar Backend
check_service "http://localhost:8080/api/health" "Backend    " "8080"

# Verificar Frontend (PUERTO 3000)
check_service "http://localhost:3000" "Frontend   " "3000"

echo ""

# Verificar procesos
echo -e "${BLUE}📋 Procesos en ejecución:${NC}"
echo ""

if pgrep -f "spring-boot:run" > /dev/null; then
    echo -e "${GREEN}✓${NC} Backend Spring Boot"
    ps aux | grep "spring-boot:run" | grep -v grep | awk '{print "  PID: " $2 " | CPU: " $3 "% | MEM: " $4 "%"}'
else
    echo -e "${RED}✗${NC} Backend no está corriendo"
fi

if pgrep -f "vite" > /dev/null; then
    echo -e "${GREEN}✓${NC} Frontend Vite"
    ps aux | grep "vite" | grep -v grep | head -1 | awk '{print "  PID: " $2 " | CPU: " $3 "% | MEM: " $4 "%"}'
else
    echo -e "${RED}✗${NC} Frontend no está corriendo"
fi

if docker ps | grep -q neo4j; then
    echo -e "${GREEN}✓${NC} Neo4j (Docker)"
    docker ps | grep neo4j | awk '{print "  Container: " $1 " | Status: " $7}'
else
    echo -e "${RED}✗${NC} Neo4j no está corriendo"
fi

echo ""

# Mostrar URLs
echo -e "${BLUE}🌐 URLs de acceso:${NC}"
echo -e "   Frontend:  ${GREEN}http://localhost:3000${NC}"
echo -e "   Backend:   ${GREEN}http://localhost:8080${NC}"
echo -e "   Neo4j:     ${GREEN}http://localhost:7474${NC} (neo4j/password123)"
echo ""
echo -e "${YELLOW}💡 Frontend en puerto 3000 con proxy configurado para /api${NC}"
echo ""

# Mostrar logs recientes si hay errores
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
LOG_DIR="$SCRIPT_DIR/logs"

if [ -d "$LOG_DIR" ]; then
    echo -e "${BLUE}📝 Últimas líneas de logs:${NC}"

    if [ -f "$LOG_DIR/backend.log" ]; then
        echo -e "\n${YELLOW}Backend:${NC}"
        tail -3 "$LOG_DIR/backend.log" 2>/dev/null | sed 's/^/  /'
    fi

    if [ -f "$LOG_DIR/frontend.log" ]; then
        echo -e "\n${YELLOW}Frontend:${NC}"
        tail -3 "$LOG_DIR/frontend.log" 2>/dev/null | sed 's/^/  /'
    fi
fi

echo ""
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

# Función para esperar que un servicio esté disponible
wait_for_service() {
    local url=$1
    local name=$2
    local max_attempts=30
    local attempt=1

    echo -e "${YELLOW}⏳ Esperando a que $name esté disponible...${NC}"

    while [ $attempt -le $max_attempts ]; do
        if curl -s "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}✓ $name está listo!${NC}"
            return 0
        fi
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done

    echo -e "${RED}✗ Timeout esperando a $name${NC}"
    return 1
}

# 1. Detener procesos existentes
echo -e "${YELLOW}🛑 Deteniendo procesos existentes...${NC}"
pkill -f "spring-boot:run" 2>/dev/null || true
pkill -f "vite" 2>/dev/null || true
sleep 2

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

# 3. Verificar Neo4j
echo -e "${YELLOW}🔍 Verificando Neo4j...${NC}"
if ! docker ps | grep -q neo4j; then
    echo -e "${YELLOW}⚠️  Neo4j no está corriendo${NC}"
    echo -e "${YELLOW}  Iniciando Neo4j...${NC}"
    docker start honeycomb-neo4j 2>/dev/null || \
    docker run -d \
        --name honeycomb-neo4j \
        -p 7474:7474 -p 7687:7687 \
        -e NEO4J_AUTH=neo4j/password123 \
        -e NEO4J_dbms_memory_heap_max__size=2G \
        -e NEO4J_dbms_memory_pagecache_size=512M \
        neo4j:latest

    # Esperar a que Neo4j esté listo
    sleep 10
fi

if wait_for_service "http://localhost:7474" "Neo4j"; then
    echo -e "${GREEN}✓ Neo4j está corriendo${NC}"
else
    echo -e "${RED}✗ No se pudo conectar a Neo4j${NC}"
    exit 1
fi

# 4. Cargar datos de prueba (opcional)
if [ "$1" == "--load-data" ] || [ "$1" == "-l" ]; then
    echo -e "${YELLOW}📊 Cargando datos de prueba...${NC}"
    if [ -f "$BACKEND_DIR/src/main/resources/new-test-data.cypher" ]; then
        docker exec -i honeycomb-neo4j cypher-shell -u neo4j -p password123 \
            < "$BACKEND_DIR/src/main/resources/new-test-data.cypher" 2>/dev/null || \
            echo -e "${YELLOW}⚠️  No se pudieron cargar los datos (esto es normal si ya existen)${NC}"
    fi
fi

# 5. Iniciar Backend
echo -e "${YELLOW}🚀 Iniciando Backend (Spring Boot)...${NC}"
cd "$BACKEND_DIR"

# Verificar que Maven esté disponible
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}✗ Maven no está instalado${NC}"
    exit 1
fi

# Iniciar backend en background
nohup mvn spring-boot:run > "$LOG_DIR/backend.log" 2>&1 &
BACKEND_PID=$!
echo $BACKEND_PID > "$LOG_DIR/backend.pid"
echo -e "${GREEN}✓ Backend iniciado (PID: $BACKEND_PID)${NC}"

# Esperar a que el backend esté listo
if wait_for_service "http://localhost:8080/api/health" "Backend"; then
    echo -e "${GREEN}✓ Backend respondiendo correctamente${NC}"
else
    echo -e "${YELLOW}⚠️  Backend tardando en responder, pero continuando...${NC}"
fi

# 6. Iniciar Frontend
echo -e "${YELLOW}🎨 Iniciando Frontend (Vite)...${NC}"
cd "$FRONTEND_DIR"

# Verificar que npm esté disponible
if ! command -v npm &> /dev/null; then
    echo -e "${RED}✗ npm no está instalado${NC}"
    exit 1
fi

# Verificar que node_modules exista
if [ ! -d "node_modules" ]; then
    echo -e "${YELLOW}📦 Instalando dependencias...${NC}"
    npm install
fi

# Iniciar frontend en background
nohup npm run dev > "$LOG_DIR/frontend.log" 2>&1 &
FRONTEND_PID=$!
echo $FRONTEND_PID > "$LOG_DIR/frontend.pid"
echo -e "${GREEN}✓ Frontend iniciado (PID: $FRONTEND_PID)${NC}"

# Esperar a que el frontend esté listo
sleep 5
if wait_for_service "http://localhost:3000" "Frontend"; then
    echo -e "${GREEN}✓ Frontend respondiendo correctamente${NC}"
else
    echo -e "${YELLOW}⚠️  Frontend tardando en responder${NC}"
fi

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
