#!/bin/bash

# Script simplificado para iniciar solo el backend
# Asume que Neo4j ya está corriendo

echo "🚀 Iniciando Backend de Honeycomb..."

# Cambiar al directorio del proyecto
cd "$(dirname "$0")/demo"

# Verificar que Neo4j esté disponible
echo "⏳ Verificando conexión a Neo4j..."
timeout 5 bash -c 'until nc -z localhost 7687; do sleep 1; done' 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✅ Neo4j está disponible"
else
    echo "⚠️  Advertencia: No se puede conectar a Neo4j en el puerto 7687"
    echo "   Asegúrate de que Neo4j esté corriendo: docker-compose up -d"
fi

# Limpiar puerto 8080 si está ocupado
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1 ; then
    echo "🔧 Puerto 8080 ocupado, liberando..."
    lsof -ti:8080 | xargs kill -9 2>/dev/null || true
    sleep 2
fi

# Iniciar backend
echo "⚙️  Iniciando Spring Boot..."
nohup mvn spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!

echo "✅ Backend iniciado con PID: $BACKEND_PID"
echo ""
echo "📝 Para ver logs en tiempo real:"
echo "   tail -f backend.log"
echo ""
echo "🔗 API disponible en: http://localhost:8080"
echo "🔗 Health check: http://localhost:8080/api/algorithms/health"
echo ""

