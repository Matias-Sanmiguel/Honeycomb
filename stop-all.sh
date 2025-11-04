#!/bin/bash

# Script para detener todos los servicios de Honeycomb

echo "=========================================="
echo "  🛑 DETENIENDO HONEYCOMB"
echo "=========================================="
echo ""

# Leer PIDs si existen
if [ -f .pids ]; then
    echo "📋 Deteniendo procesos..."
    while IFS= read -r line; do
        PID=$(echo $line | awk '{print $2}')
        SERVICE=$(echo $line | awk '{print $1}' | tr -d ':')
        if ps -p $PID > /dev/null 2>&1; then
            echo "  Deteniendo $SERVICE (PID: $PID)..."
            kill $PID 2>/dev/null || true
        fi
    done < .pids
    rm .pids
fi

# Buscar y matar procesos de Spring Boot
echo "🔍 Buscando procesos de Spring Boot..."
SPRING_PIDS=$(ps aux | grep '[s]pring-boot:run' | awk '{print $2}')
if [ ! -z "$SPRING_PIDS" ]; then
    echo "$SPRING_PIDS" | xargs kill 2>/dev/null || true
fi

# Buscar y matar procesos de Vite
echo "🔍 Buscando procesos de Vite..."
VITE_PIDS=$(ps aux | grep '[v]ite' | awk '{print $2}')
if [ ! -z "$VITE_PIDS" ]; then
    echo "$VITE_PIDS" | xargs kill 2>/dev/null || true
fi

# Detener Docker
echo "🐳 Deteniendo contenedores Docker..."
docker-compose down

echo ""
echo "✅ Todos los servicios han sido detenidos"
echo ""
#!/bin/bash

# Script para iniciar el proyecto Honeycomb completo

set -e

echo "=========================================="
echo "  🐝 HONEYCOMB - INICIO COMPLETO"
echo "=========================================="
echo ""

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verificar Docker
if ! command -v docker &> /dev/null; then
    echo -e "${YELLOW}⚠️  Docker no está instalado${NC}"
    exit 1
fi

# Verificar Node.js
if ! command -v node &> /dev/null; then
    echo -e "${YELLOW}⚠️  Node.js no está instalado${NC}"
    exit 1
fi

echo -e "${BLUE}1. Iniciando servicios Docker (Neo4j)...${NC}"
docker-compose up -d

echo ""
echo -e "${BLUE}2. Esperando a que Neo4j esté listo...${NC}"
sleep 10

echo ""
echo -e "${BLUE}3. Cargando datos de prueba...${NC}"
./LOAD_TEST_DATA.sh

echo ""
echo -e "${BLUE}4. Iniciando Backend (Spring Boot)...${NC}"
cd demo
mvn spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
echo "Backend PID: $BACKEND_PID"
cd ..

echo ""
echo -e "${BLUE}5. Esperando a que el backend esté listo...${NC}"
sleep 15

echo ""
echo -e "${BLUE}6. Iniciando Frontend (React)...${NC}"
cd frontend
npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
echo "Frontend PID: $FRONTEND_PID"
cd ..

echo ""
echo "=========================================="
echo -e "${GREEN}  ✅ HONEYCOMB INICIADO${NC}"
echo "=========================================="
echo ""
echo "📊 Servicios disponibles:"
echo ""
echo -e "  ${GREEN}Frontend:${NC}     http://localhost:3000"
echo -e "  ${GREEN}Backend API:${NC}  http://localhost:8080"
echo -e "  ${GREEN}Neo4j Browser:${NC} http://localhost:7474"
echo ""
echo "📝 Logs:"
echo -e "  Backend:  tail -f backend.log"
echo -e "  Frontend: tail -f frontend.log"
echo ""
echo "🛑 Para detener todos los servicios:"
echo "   ./stop-all.sh"
echo ""
echo "PIDs guardados:"
echo "Backend: $BACKEND_PID" > .pids
echo "Frontend: $FRONTEND_PID" >> .pids

