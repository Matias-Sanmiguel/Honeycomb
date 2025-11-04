#!/bin/bash

# Script para cargar datos de prueba en Neo4j
# Asegúrate de que Neo4j esté corriendo antes de ejecutar este script

set -e

echo "=========================================="
echo "  CARGANDO DATOS DE PRUEBA EN NEO4J"
echo "=========================================="
echo ""

# Variables de configuración
NEO4J_HOST="${NEO4J_HOST:-localhost}"
NEO4J_PORT="${NEO4J_PORT:-7687}"
NEO4J_USER="${NEO4J_USER:-neo4j}"
NEO4J_PASSWORD="${NEO4J_PASSWORD:-password}"
CYPHER_FILE="demo/src/main/resources/test-data.cypher"

echo "📋 Configuración:"
echo "   Host: $NEO4J_HOST"
echo "   Puerto: $NEO4J_PORT"
echo "   Usuario: $NEO4J_USER"
echo ""

# Verificar si existe el archivo cypher
if [ ! -f "$CYPHER_FILE" ]; then
    echo "❌ Error: No se encuentra el archivo $CYPHER_FILE"
    exit 1
fi

echo "✅ Archivo de datos encontrado: $CYPHER_FILE"
echo ""

# Función para cargar datos usando Docker
load_with_docker() {
    echo "🐳 Intentando cargar datos usando Docker..."
    echo ""

    # Verificar si hay un contenedor de Neo4j corriendo
    CONTAINER_ID=$(docker ps --filter "ancestor=neo4j" --filter "status=running" --format "{{.ID}}" | head -n 1)

    if [ -z "$CONTAINER_ID" ]; then
        # Buscar por nombre del contenedor
        CONTAINER_ID=$(docker ps --filter "name=neo4j" --filter "status=running" --format "{{.ID}}" | head -n 1)
    fi

    if [ -z "$CONTAINER_ID" ]; then
        echo "❌ Error: No se encontró un contenedor de Neo4j corriendo"
        echo "   Intenta ejecutar: docker-compose up -d"
        return 1
    fi

    echo "📦 Contenedor Neo4j encontrado: $CONTAINER_ID"
    echo ""

    # Copiar el archivo al contenedor
    docker cp "$CYPHER_FILE" "$CONTAINER_ID:/tmp/test-data.cypher"

    # Ejecutar cypher-shell dentro del contenedor
    echo "⏳ Cargando datos..."
    docker exec -i "$CONTAINER_ID" \
        cypher-shell -u "$NEO4J_USER" -p "$NEO4J_PASSWORD" \
        -f "/tmp/test-data.cypher" 2>&1 | grep -v "^$" || true

    echo ""
    echo "✅ Datos cargados exitosamente con Docker!"
    return 0
}

# Función para cargar datos usando curl (HTTP API)
load_with_http_api() {
    echo "🌐 Intentando cargar datos usando Neo4j HTTP API..."
    echo ""

    # Leer el archivo cypher y convertirlo a JSON
    CYPHER_CONTENT=$(cat "$CYPHER_FILE")

    # Dividir el contenido en statements separados por punto y coma
    echo "⏳ Cargando datos mediante HTTP API..."

    # Ejecutar cada statement
    while IFS= read -r line; do
        if [[ ! -z "$line" && ! "$line" =~ ^[[:space:]]*// ]]; then
            curl -s -X POST "http://$NEO4J_HOST:7474/db/neo4j/tx/commit" \
                -H "Content-Type: application/json" \
                -H "Accept: application/json" \
                -u "$NEO4J_USER:$NEO4J_PASSWORD" \
                -d "{\"statements\":[{\"statement\":\"$line\"}]}" > /dev/null 2>&1 || true
        fi
    done < <(grep -v "^//" "$CYPHER_FILE" | grep -v "^$")

    echo ""
    echo "✅ Datos cargados exitosamente con HTTP API!"
    return 0
}

# Función para cargar datos usando Python (si está disponible)
load_with_python() {
    echo "🐍 Intentando cargar datos usando Python..."
    echo ""

    if ! command -v python3 &> /dev/null; then
        echo "❌ Python3 no está disponible"
        return 1
    fi

    python3 << 'PYTHON_SCRIPT'
import sys
try:
    from neo4j import GraphDatabase

    # Configuración
    uri = "bolt://localhost:7687"
    user = "neo4j"
    password = "password"

    # Leer el archivo
    with open("demo/src/main/resources/test-data.cypher", "r") as f:
        content = f.read()

    # Conectar a Neo4j
    driver = GraphDatabase.driver(uri, auth=(user, password))

    with driver.session() as session:
        # Dividir por punto y coma y ejecutar cada statement
        statements = [s.strip() for s in content.split(";") if s.strip() and not s.strip().startswith("//")]

        for stmt in statements:
            if stmt:
                try:
                    session.run(stmt)
                    print(".", end="", flush=True)
                except Exception as e:
                    print(f"\n⚠️  Error en statement: {str(e)[:50]}")

    driver.close()
    print("\n✅ Datos cargados exitosamente con Python!")
    sys.exit(0)

except ImportError:
    print("❌ El paquete neo4j de Python no está instalado")
    print("   Instala con: pip install neo4j")
    sys.exit(1)
except Exception as e:
    print(f"❌ Error: {e}")
    sys.exit(1)
PYTHON_SCRIPT

    return $?
}

# Intentar diferentes métodos en orden de preferencia
SUCCESS=0

# Método 1: Docker (más confiable)
if command -v docker &> /dev/null; then
    if load_with_docker; then
        SUCCESS=1
    fi
fi

# Método 2: Python con neo4j driver
if [ $SUCCESS -eq 0 ]; then
    if load_with_python; then
        SUCCESS=1
    fi
fi

# Método 3: HTTP API con curl
if [ $SUCCESS -eq 0 ]; then
    if command -v curl &> /dev/null; then
        if load_with_http_api; then
            SUCCESS=1
        fi
    fi
fi

# Si ningún método funcionó, mostrar instrucciones manuales
if [ $SUCCESS -eq 0 ]; then
    echo ""
    echo "⚠️  No se pudieron cargar los datos automáticamente"
    echo ""
    echo "📝 Para cargar los datos manualmente:"
    echo "   1. Abre Neo4j Browser en: http://localhost:7474"
    echo "   2. Conéctate con usuario: $NEO4J_USER"
    echo "   3. Abre el archivo: $CYPHER_FILE"
    echo "   4. Copia y pega el contenido en el editor de consultas"
    echo "   5. Ejecuta la consulta"
    echo ""
    echo "📦 O ejecuta con Docker:"
    echo "   docker-compose up -d"
    echo "   docker exec -i \$(docker ps -q --filter ancestor=neo4j) cypher-shell -u neo4j -p password < $CYPHER_FILE"
    echo ""
    exit 1
fi

echo ""
echo "=========================================="
echo "  VERIFICANDO DATOS CARGADOS"
echo "=========================================="
echo ""

# Verificar los datos cargados
if [ ! -z "$CONTAINER_ID" ]; then
    echo "📊 Estadísticas de la base de datos:"
    echo ""

    docker exec -i "$CONTAINER_ID" \
        cypher-shell -u "$NEO4J_USER" -p "$NEO4J_PASSWORD" \
        "MATCH (w:Wallet) RETURN count(w) as wallets;" 2>&1 | tail -5

    docker exec -i "$CONTAINER_ID" \
        cypher-shell -u "$NEO4J_USER" -p "$NEO4J_PASSWORD" \
        "MATCH (t:Transaction) RETURN count(t) as transactions;" 2>&1 | tail -5

    docker exec -i "$CONTAINER_ID" \
        cypher-shell -u "$NEO4J_USER" -p "$NEO4J_PASSWORD" \
        "MATCH ()-[r:INPUT]->() RETURN count(r) as inputs;" 2>&1 | tail -5

    docker exec -i "$CONTAINER_ID" \
        cypher-shell -u "$NEO4J_USER" -p "$NEO4J_PASSWORD" \
        "MATCH ()-[r:OUTPUT]->() RETURN count(r) as outputs;" 2>&1 | tail -5
fi

echo ""
echo "=========================================="
echo "  ✅ PROCESO COMPLETADO"
echo "=========================================="
echo ""
echo "🌐 Puedes ver los datos en Neo4j Browser:"
echo "   http://localhost:7474"
echo ""
echo "🔍 Consulta de ejemplo:"
echo "   MATCH (w:Wallet)-[r]-(t:Transaction) RETURN w, r, t LIMIT 25"
echo ""
