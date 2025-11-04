#!/bin/bash

# Script para gestionar el proyecto Honeycomb con Docker

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Funciones de utilidad
info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar que Docker está instalado
check_docker() {
    if ! command -v docker &> /dev/null; then
        error "Docker no está instalado"
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        error "Docker Compose no está instalado"
        exit 1
    fi

    info "Docker y Docker Compose están instalados ✓"
}

# Construir las imágenes
build() {
    info "Construyendo imágenes Docker..."
    docker-compose build --no-cache
    info "Imágenes construidas exitosamente ✓"
}

# Iniciar los servicios
start() {
    info "Iniciando servicios..."
    docker-compose up -d
    info "Servicios iniciados ✓"
    info "API disponible en: http://localhost:8080"
    info "Neo4j Browser: http://localhost:7474 (usuario: neo4j, contraseña: password)"
}

# Detener los servicios
stop() {
    info "Deteniendo servicios..."
    docker-compose down
    info "Servicios detenidos ✓"
}

# Reiniciar los servicios
restart() {
    stop
    start
}

# Ver logs
logs() {
    if [ -z "$1" ]; then
        docker-compose logs -f
    else
        docker-compose logs -f "$1"
    fi
}

# Ver estado de los servicios
status() {
    info "Estado de los servicios:"
    docker-compose ps
}

# Limpiar todo (incluyendo volúmenes)
clean() {
    warn "¿Estás seguro de que quieres eliminar todos los datos? (y/n)"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        info "Limpiando contenedores, imágenes y volúmenes..."
        docker-compose down -v --rmi local
        info "Limpieza completada ✓"
    else
        info "Operación cancelada"
    fi
}

# Ejecutar tests
test() {
    info "Ejecutando tests..."
    cd demo
    mvn test
    cd ..
}

# Compilar el proyecto
compile() {
    info "Compilando proyecto..."
    cd demo
    mvn clean compile
    cd ..
    info "Compilación exitosa ✓"
}

# Empaquetar el proyecto
package() {
    info "Empaquetando proyecto..."
    cd demo
    mvn clean package -DskipTests
    cd ..
    info "Empaquetado exitoso ✓"
}

# Mostrar ayuda
help() {
    echo "Uso: ./docker-manager.sh [comando]"
    echo ""
    echo "Comandos disponibles:"
    echo "  build       - Construir las imágenes Docker"
    echo "  start       - Iniciar los servicios"
    echo "  stop        - Detener los servicios"
    echo "  restart     - Reiniciar los servicios"
    echo "  logs [srv]  - Ver logs (opcional: especificar servicio 'app' o 'neo4j')"
    echo "  status      - Ver estado de los servicios"
    echo "  clean       - Limpiar todo (contenedores, imágenes, volúmenes)"
    echo "  test        - Ejecutar tests"
    echo "  compile     - Compilar el proyecto"
    echo "  package     - Empaquetar el proyecto"
    echo "  help        - Mostrar esta ayuda"
}

# Main
check_docker

case "$1" in
    build)
        build
        ;;
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    logs)
        logs "$2"
        ;;
    status)
        status
        ;;
    clean)
        clean
        ;;
    test)
        test
        ;;
    compile)
        compile
        ;;
    package)
        package
        ;;
    help|--help|-h)
        help
        ;;
    *)
        error "Comando desconocido: $1"
        help
        exit 1
        ;;
esac

