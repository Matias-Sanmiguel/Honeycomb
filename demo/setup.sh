#!/bin/bash

# 🚀 Script de inicio rápido para Crypto Forensic Analysis

set -e

echo "🔍 Crypto Forensic Analysis - Setup Script"
echo "=========================================="
echo ""

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verificar Docker
echo -e "${YELLOW}📦 Verificando Docker...${NC}"
if ! command -v docker &> /dev/null; then
    echo "❌ Docker no está instalado. Por favor instala Docker primero."
    exit 1
fi
echo -e "${GREEN}✅ Docker encontrado${NC}"
echo ""

# Verificar Maven
echo -e "${YELLOW}📦 Verificando Maven...${NC}"
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven no está instalado. Por favor instala Maven primero."
    exit 1
fi
echo -e "${GREEN}✅ Maven encontrado${NC}"
echo ""

# Levantar Neo4j con Docker Compose
echo -e "${YELLOW}🐳 Levantando Neo4j con Docker Compose...${NC}"
docker-compose up -d

echo "⏳ Esperando que Neo4j esté listo (30 segundos)..."
sleep 30

# Verificar que Neo4j esté corriendo
echo -e "${YELLOW}🔍 Verificando Neo4j...${NC}"
if curl -s http://localhost:7474 > /dev/null; then
    echo -e "${GREEN}✅ Neo4j está corriendo${NC}"
else
    echo "❌ Neo4j no responde. Verifica los logs con: docker-compose logs"
    exit 1
fi
echo ""

# Compilar el proyecto
echo -e "${YELLOW}🔨 Compilando el proyecto...${NC}"
mvn clean install -DskipTests

echo ""
echo -e "${GREEN}✅ Setup completado!${NC}"
echo ""
echo "=========================================="
echo "🎉 Todo listo para empezar!"
echo "=========================================="
echo ""
echo "📊 Neo4j Browser: http://localhost:7474"
echo "   Usuario: neo4j"
echo "   Password: password"
echo ""
echo "🚀 Para iniciar la aplicación, ejecuta:"
echo "   mvn spring-boot:run"
echo ""
echo "📖 Luego accede a:"
echo "   http://localhost:8080/api/forensic/..."
echo ""
echo "💡 Ver README.md para más detalles de los endpoints"
echo ""
