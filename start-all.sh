#!/bin/bash

echo "🐝 Iniciando Honeycomb - Sistema Completo"
echo "=========================================="

set -euo pipefail

# 1. Iniciar Neo4j con Docker Compose
echo ""
echo "📦 Paso 1: Iniciando Neo4j..."
(docker compose up -d || docker-compose up -d)

# Esperar a que Neo4j esté listo
echo "⏳ Esperando a que Neo4j esté listo..."
sleep 12

# 2. Cargar datos de prueba
echo ""
echo "📊 Paso 2: Cargando datos de prueba..."
if [ -f "./LOAD_TEST_DATA.sh" ]; then
    chmod +x ./LOAD_TEST_DATA.sh
    ./LOAD_TEST_DATA.sh
else
    echo "⚠️  Advertencia: LOAD_TEST_DATA.sh no encontrado"
fi

# 3. Iniciar Backend usando el script robusto
echo ""
echo "⚙️  Paso 3: Iniciando Backend (Spring Boot)..."
chmod +x ./start-backend.sh
./start-backend.sh || true

# Esperar health del backend
echo "⏳ Esperando a que el backend esté listo..."
ATTEMPTS=0
until curl -s http://localhost:8080/api/algorithms/health >/dev/null 2>&1 || [ $ATTEMPTS -ge 30 ]; do
  ATTEMPTS=$((ATTEMPTS+1))
  sleep 1
  echo -n "."
done
echo ""

# 4. Iniciar Frontend (React + Vite) puerto 3000
echo ""
echo "🎨 Paso 4: Iniciando Frontend (React + Vite)..."
cd frontend
# Forzar puerto 3000 por consistencia con vite.config.js
npm run dev -- --port 3000 > ../frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..

# Guardar PID del frontend
echo "Frontend: $FRONTEND_PID" >> .pids

echo "Frontend iniciado con PID: $FRONTEND_PID"

echo ""
echo "✅ ¡Sistema Honeycomb iniciado!"
echo "================================"
echo ""
echo "🌐 URLs de acceso:"
echo "  - Frontend:    http://localhost:3000"
echo "  - Backend API: http://localhost:8080"
echo "  - Neo4j:       http://localhost:7474"
echo ""
echo "📝 Logs:"
echo "  - Backend:  tail -f backend.log"
echo "  - Frontend: tail -f frontend.log"
echo ""
echo "🛑 Para detener: ./stop-all.sh"
echo ""
