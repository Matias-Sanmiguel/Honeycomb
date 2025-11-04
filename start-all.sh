#!/bin/bash

echo "🐝 Iniciando Honeycomb - Sistema Completo"
echo "=========================================="

# 1. Iniciar Neo4j con Docker Compose
echo ""
echo "📦 Paso 1: Iniciando Neo4j..."
docker-compose up -d

# Esperar a que Neo4j esté listo
echo "⏳ Esperando a que Neo4j esté listo..."
sleep 15

# 2. Cargar datos de prueba
echo ""
echo "📊 Paso 2: Cargando datos de prueba..."
if [ -f "./LOAD_TEST_DATA.sh" ]; then
    chmod +x ./LOAD_TEST_DATA.sh
    ./LOAD_TEST_DATA.sh
else
    echo "⚠️  Advertencia: LOAD_TEST_DATA.sh no encontrado"
fi

# 3. Iniciar Backend
echo ""
echo "⚙️  Paso 3: Iniciando Backend (Spring Boot)..."
cd demo
mvn spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
echo "Backend iniciado con PID: $BACKEND_PID"
cd ..

# Esperar a que el backend esté listo
echo "⏳ Esperando a que el backend esté listo..."
sleep 20

# 4. Iniciar Frontend
echo ""
echo "🎨 Paso 4: Iniciando Frontend (React + Vite)..."
cd frontend
npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
echo "Frontend iniciado con PID: $FRONTEND_PID"
cd ..

echo ""
echo "✅ ¡Sistema Honeycomb iniciado!"
echo "================================"
echo ""
echo "🌐 URLs de acceso:"
echo "  - Frontend:    http://localhost:5173"
echo "  - Backend API: http://localhost:8080"
echo "  - Neo4j:       http://localhost:7474"
echo ""
echo "📝 Logs:"
echo "  - Backend:  tail -f backend.log"
echo "  - Frontend: tail -f frontend.log"
echo ""
echo "🛑 Para detener: ./stop-all.sh"
echo ""

