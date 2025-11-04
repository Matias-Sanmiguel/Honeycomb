#!/bin/bash

# Script simplificado para iniciar solo el backend
# Asume que Neo4j ya está corriendo
set -euo pipefail

echo "🚀 Iniciando Backend de Honeycomb..."

# Cambiar al directorio del proyecto
dcd() { cd "$(dirname "$0")/demo"; }
dcd

# Configurar JAVA_HOME/PATH automáticamente (Arch Linux o en general)
if command -v archlinux-java >/dev/null 2>&1; then
  JENV=$(archlinux-java get)
  export JAVA_HOME="/usr/lib/jvm/${JENV}"
elif command -v javac >/dev/null 2>&1; then
  export JAVA_HOME="$(dirname "$(readlink -f "$(command -v javac)")")/.."
fi
if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
  export PATH="$JAVA_HOME/bin:$PATH"
fi

# Mostrar versiones detectadas
echo "🧩 Java: $(java -version 2>&1 | head -n1 || echo 'no disponible')"
echo "🧰 Maven: $(mvn -v 2>&1 | head -n1 || echo 'no disponible')"

# Verificar que Neo4j esté disponible
echo "⏳ Verificando conexión a Neo4j..."
timeout 5 bash -c 'until nc -z localhost 7687; do sleep 1; done' 2>/dev/null || true
if nc -z localhost 7687 2>/dev/null; then
    echo "✅ Neo4j está disponible"
else
    echo "⚠️  Advertencia: No se puede conectar a Neo4j en el puerto 7687"
    echo "   Asegúrate de que Neo4j esté corriendo: docker compose up -d (o docker-compose)"
fi

# Limpiar puerto 8080 si está ocupado
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1 ; then
    echo "🔧 Puerto 8080 ocupado, liberando..."
    lsof -ti:8080 | xargs -r kill -9 2>/dev/null || true
    sleep 2
fi

# Función para registrar PID
guardar_pid() {
  local name="$1" pid="$2"
  echo "$name: $pid" >> ../.pids
}
: > ../.pids

# Intentar iniciar backend con Maven, si está disponible y Java también
STARTED=false
if command -v mvn >/dev/null 2>&1 && command -v java >/dev/null 2>&1; then
  echo "⚙️  Iniciando Spring Boot con Maven..."
  set +e
  nohup mvn -q -Dmaven.test.skip=true spring-boot:run > ../backend.log 2>&1 &
  BACKEND_PID=$!
  disown $BACKEND_PID 2>/dev/null || true
  set -e
  sleep 5
  if ps -p "$BACKEND_PID" >/dev/null 2>&1; then
    echo "✅ Backend iniciado con Maven (PID: $BACKEND_PID)"
    guardar_pid "Backend" "$BACKEND_PID"
    STARTED=true
  else
    echo "❌ Falló inicio con Maven, se intentará con java -jar (ver backend.log)"
  fi
else
  echo "ℹ️  Maven o Java no disponibles en PATH, se intentará con java -jar"
fi

# Fallback: iniciar con el JAR preconstruido
if [ "$STARTED" = false ]; then
  JAR_PATH="target/crypto-forensic-1.0-SNAPSHOT.jar"
  if [ ! -f "$JAR_PATH" ] && command -v mvn >/dev/null 2>&1; then
    echo "📦 JAR no encontrado, construyendo con Maven (skip tests)..."
    mvn -q -Dmaven.test.skip=true package || true
  fi
  if [ -f "$JAR_PATH" ] && command -v java >/dev/null 2>&1; then
    echo "⚙️  Iniciando Spring Boot con java -jar..."
    nohup java -jar "$JAR_PATH" > ../backend.log 2>&1 &
    BACKEND_PID=$!
    disown $BACKEND_PID 2>/dev/null || true
    sleep 5
    if ps -p "$BACKEND_PID" >/dev/null 2>&1; then
      echo "✅ Backend iniciado con java -jar (PID: $BACKEND_PID)"
      guardar_pid "Backend" "$BACKEND_PID"
      STARTED=true
    else
      echo "❌ No se pudo iniciar el backend. Revisá ../backend.log"
      exit 1
    fi
  else
    echo "❌ No hay JAR disponible ni Java en PATH. Instalá Java/Maven o construí el JAR."
    exit 1
  fi
fi

# Mensajes finales
echo ""
echo "📝 Para ver logs en tiempo real:"
echo "   tail -f ../backend.log"
echo ""
echo "🔗 API (si está arriba) en: http://localhost:8080"
echo ""
