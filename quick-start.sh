#!/bin/bash

echo "======================================"
echo "🍯 Iniciando Honeycomb"
echo "======================================"
echo ""

# Crear directorio de logs
mkdir -p logs

# 1. Limpiar procesos anteriores
echo "🛑 Limpiando procesos anteriores..."
pkill -9 -f "spring-boot:run" 2>/dev/null
pkill -9 -f "vite" 2>/dev/null
sleep 3

# 2. Verificar Neo4j
echo "🔍 Verificando Neo4j..."
if docker ps | grep -q honeycomb-neo4j; then
    echo "✓ Neo4j ya está corriendo"
else
    echo "Iniciando Neo4j..."
    docker start honeycomb-neo4j 2>/dev/null || \
    docker run -d --name honeycomb-neo4j \
        -p 7474:7474 -p 7687:7687 \
        -e NEO4J_AUTH=neo4j/password123 \
        neo4j:latest
    sleep 10
fi

# 3. Iniciar Backend
echo ""
echo "🚀 Iniciando Backend..."
cd demo
mvn spring-boot:run > ../logs/backend.log 2>&1 &
BACKEND_PID=$!
echo "Backend iniciado con PID: $BACKEND_PID"
echo $BACKEND_PID > ../logs/backend.pid

# Esperar a que el backend esté listo
echo "Esperando a que el backend inicie (esto puede tardar 30-60 segundos)..."
for i in {1..60}; do
    if curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
        echo "✓ Backend listo!"
        break
    fi
    echo -n "."
    sleep 1
done
echo ""

# 4. Iniciar Frontend
echo ""
echo "🎨 Iniciando Frontend..."
cd ../frontend
npm run dev > ../logs/frontend.log 2>&1 &
FRONTEND_PID=$!
echo "Frontend iniciado con PID: $FRONTEND_PID"
echo $FRONTEND_PID > ../logs/frontend.pid

# Esperar a que el frontend esté listo
sleep 5
echo ""
echo "======================================"
echo "✅ Servicios iniciados"
echo "======================================"
echo ""
echo "📍 URLs:"
echo "   Frontend:  http://localhost:3000"
echo "   Backend:   http://localhost:8080"
echo "   Neo4j:     http://localhost:7474"
echo ""
echo "📝 Para ver logs en tiempo real:"
echo "   Backend:  tail -f logs/backend.log"
echo "   Frontend: tail -f logs/frontend.log"
echo ""
echo "🛑 Para detener: ./quick-stop.sh"
echo ""

