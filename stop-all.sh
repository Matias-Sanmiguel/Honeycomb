#!/bin/bash

# Script para detener todos los servicios de Honeycomb
set -euo pipefail

echo "=========================================="
echo "  🛑 DETENIENDO HONEYCOMB"
echo "=========================================="
echo ""

# Detener procesos registrados en .pids (si existe)
if [ -f .pids ]; then
    echo "📋 Deteniendo procesos listados en .pids..."
    while IFS= read -r line; do
        PID=$(echo "$line" | awk '{print $2}')
        SERVICE=$(echo "$line" | awk '{print $1}' | tr -d ':')
        if [ -n "${PID:-}" ] && ps -p "$PID" > /dev/null 2>&1; then
            echo "  Deteniendo $SERVICE (PID: $PID)..."
            kill "$PID" 2>/dev/null || true
        fi
    done < .pids
    rm -f .pids
fi

# Intentar detener backend lanzado vía Maven o java -jar
echo "🔍 Buscando procesos de backend..."
# spring-boot:run (mvn)
PIDS=$(ps aux | grep -E '[m]vn .*spring-boot:run' | awk '{print $2}')
if [ -n "${PIDS:-}" ]; then
  echo "$PIDS" | xargs -r kill 2>/dev/null || true
fi
# Java JarLauncher o el jar específico
PIDS=$(ps aux | grep -E '[o]rg.springframework.boot.loader.JarLauncher|crypto-forensic-1.0-SNAPSHOT.jar' | awk '{print $2}')
if [ -n "${PIDS:-}" ]; then
  echo "$PIDS" | xargs -r kill 2>/dev/null || true
fi

# Detener procesos del frontend (Vite)
echo "🔍 Buscando procesos de Vite..."
PIDS=$(ps aux | grep -E '[v]ite|node .*vite' | awk '{print $2}')
if [ -n "${PIDS:-}" ]; then
  echo "$PIDS" | xargs -r kill 2>/dev/null || true
fi

# Liberar puertos comunes
for PORT in 8080 3000; do
  if lsof -Pi :$PORT -sTCP:LISTEN -t >/dev/null 2>&1 ; then
      echo "🔧 Puerto $PORT ocupado, liberando..."
      lsof -ti:$PORT | xargs -r kill -9 2>/dev/null || true
      sleep 1
  fi
done

# Detener contenedores Docker
echo "🐳 Deteniendo contenedores Docker..."
(docker compose down || docker-compose down) 2>/dev/null || true

echo ""
echo "✅ Todos los servicios han sido detenidos"
echo ""
