#!/bin/bash

# ====================================================================
# Script de Pruebas: Backtracking y Branch & Bound
# Análisis Forense de Criptomonedas
# ====================================================================

BASE_URL="http://localhost:8080"

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║   🔍 Pruebas: BACKTRACKING y BRANCH & BOUND                    ║"
echo "║   Sistema de Análisis Forense de Criptomonedas                 ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# ====================================================================
# 1. HEALTH CHECK
# ====================================================================

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}1. HEALTH CHECK${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

curl -X GET "${BASE_URL}/api/algorithms/health" \
  -H "Content-Type: application/json" \
  | jq '.'

echo ""
read -p "Presiona Enter para continuar..."
echo ""

# ====================================================================
# 2. BACKTRACKING - Búsqueda desde wallet específica
# ====================================================================

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}2. BACKTRACKING - Detección de Cadenas Sospechosas${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

echo -e "${YELLOW}Endpoint:${NC} GET /api/forensic/backtrack/suspicious-chains/{depth}?wallet={address}"
echo -e "${YELLOW}Descripción:${NC} Explora TODOS los caminos posibles para detectar ciclos y peel chains"
echo -e "${YELLOW}Complejidad:${NC} O(b^d) - Exponencial con poda"
echo ""

# Ejemplo con wallet (reemplaza con una wallet real de tu base de datos)
WALLET_ADDRESS="1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
DEPTH=5

echo -e "${YELLOW}Parámetros:${NC}"
echo "  - Wallet: ${WALLET_ADDRESS}"
echo "  - Depth: ${DEPTH}"
echo ""

curl -X GET "${BASE_URL}/api/forensic/backtrack/suspicious-chains/${DEPTH}?wallet=${WALLET_ADDRESS}" \
  -H "Content-Type: application/json" \
  | jq '.'

echo ""
read -p "Presiona Enter para continuar..."
echo ""

# ====================================================================
# 3. BACKTRACKING - Búsqueda global de ciclos
# ====================================================================

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}3. BACKTRACKING - Búsqueda Global de Ciclos${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

echo -e "${YELLOW}Endpoint:${NC} GET /api/forensic/backtrack/suspicious-chains/{depth}"
echo -e "${YELLOW}Descripción:${NC} Busca ciclos en toda la red (sin wallet específica)"
echo ""

DEPTH=4

echo -e "${YELLOW}Parámetros:${NC}"
echo "  - Depth: ${DEPTH}"
echo "  - Wallet: GLOBAL_SEARCH"
echo ""

curl -X GET "${BASE_URL}/api/forensic/backtrack/suspicious-chains/${DEPTH}" \
  -H "Content-Type: application/json" \
  | jq '.'

echo ""
read -p "Presiona Enter para continuar..."
echo ""

# ====================================================================
# 4. BRANCH & BOUND - Camino óptimo con restricción de costo
# ====================================================================

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}4. BRANCH & BOUND - Camino Óptimo con Restricción${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

echo -e "${YELLOW}Endpoint:${NC} GET /api/path/branch-bound/{addr1}/{addr2}/{maxCost}"
echo -e "${YELLOW}Descripción:${NC} Encuentra el camino MÁS CORTO con restricción de costo"
echo -e "${YELLOW}Complejidad:${NC} O(b^d) con poda → O(V log V + E)"
echo ""

# Reemplaza con wallets reales
ADDR1="1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
ADDR2="1dice8EMCQAqQSN7ufuN6Ent9Qc1q5W9z"
MAX_COST=100.0

echo -e "${YELLOW}Parámetros:${NC}"
echo "  - Origen: ${ADDR1}"
echo "  - Destino: ${ADDR2}"
echo "  - Max Cost: ${MAX_COST} satoshis"
echo ""

curl -X GET "${BASE_URL}/api/path/branch-bound/${ADDR1}/${ADDR2}/${MAX_COST}" \
  -H "Content-Type: application/json" \
  | jq '.'

echo ""
read -p "Presiona Enter para continuar..."
echo ""

# ====================================================================
# 5. BRANCH & BOUND - Análisis de múltiples escenarios
# ====================================================================

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}5. BRANCH & BOUND - Análisis Multi-Escenario${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

echo -e "${YELLOW}Endpoint:${NC} GET /api/path/branch-bound/analyze/{addr1}/{addr2}"
echo -e "${YELLOW}Descripción:${NC} Prueba múltiples límites de costo automáticamente"
echo ""

echo -e "${YELLOW}Parámetros:${NC}"
echo "  - Origen: ${ADDR1}"
echo "  - Destino: ${ADDR2}"
echo "  - Límites: 50, 100, 200, 500, 1000 satoshis"
echo ""

curl -X GET "${BASE_URL}/api/path/branch-bound/analyze/${ADDR1}/${ADDR2}" \
  -H "Content-Type: application/json" \
  | jq '.'

echo ""
read -p "Presiona Enter para continuar..."
echo ""

# ====================================================================
# 6. COMPARACIÓN: Backtracking vs Branch & Bound
# ====================================================================

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}6. COMPARACIÓN DE ALGORITMOS${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

echo -e "${YELLOW}┌─────────────────────────────────────────────────────────────┐${NC}"
echo -e "${YELLOW}│ BACKTRACKING vs BRANCH & BOUND                              │${NC}"
echo -e "${YELLOW}├─────────────────────────────────────────────────────────────┤${NC}"
echo -e "${YELLOW}│                                                             │${NC}"
echo -e "${YELLOW}│ BACKTRACKING:                                               │${NC}"
echo -e "│   ✅ Explora TODAS las soluciones posibles                  │"
echo -e "│   ✅ Detecta patrones complejos (ciclos, redistribuciones)  │"
echo -e "│   ✅ Ideal para análisis exhaustivo                         │"
echo -e "│   ⚠️  Complejidad O(b^d) - Exponencial                      │"
echo -e "${YELLOW}│                                                             │${NC}"
echo -e "${YELLOW}│ BRANCH & BOUND:                                             │${NC}"
echo -e "│   ✅ Encuentra el camino ÓPTIMO                             │"
echo -e "│   ✅ PODA ramas que no mejoran la solución                  │"
echo -e "│   ✅ Maneja restricciones de costo                          │"
echo -e "│   ✅ Más eficiente con poda efectiva                        │"
echo -e "${YELLOW}│                                                             │${NC}"
echo -e "${YELLOW}└─────────────────────────────────────────────────────────────┘${NC}"
echo ""

# ====================================================================
# 7. TESTS ADICIONALES
# ====================================================================

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}7. TESTS ADICIONALES${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

# Test con diferentes profundidades
echo -e "${YELLOW}Test 1: Backtracking con diferentes profundidades${NC}"
for depth in 3 4 5 6; do
    echo "  Profundidad ${depth}..."
    curl -s -X GET "${BASE_URL}/api/forensic/backtrack/suspicious-chains/${depth}" \
      -H "Content-Type: application/json" \
      | jq '.totalChainsFound, .statistics'
    echo ""
done

echo ""

# Test con diferentes costos máximos
echo -e "${YELLOW}Test 2: Branch & Bound con diferentes costos${NC}"
for cost in 50 100 200 500; do
    echo "  Max Cost: ${cost} satoshis..."
    curl -s -X GET "${BASE_URL}/api/path/branch-bound/${ADDR1}/${ADDR2}/${cost}" \
      -H "Content-Type: application/json" \
      | jq '.pathFound, .totalCost, .pathLength'
    echo ""
done

echo ""


# RESUMEN

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ TESTS COMPLETADOS${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

echo -e "${YELLOW}Endpoints disponibles:${NC}"
echo ""
echo "  1. GET  /api/forensic/backtrack/suspicious-chains/{depth}?wallet={address}"
echo "  2. GET  /api/path/branch-bound/{addr1}/{addr2}/{maxCost}"
echo "  3. GET  /api/path/branch-bound/analyze/{addr1}/{addr2}"
echo "  4. GET  /api/algorithms/health"
echo ""

echo -e "${YELLOW}Documentación:${NC}"
echo "  - Guía completa: BACKTRACKING_BRANCH_BOUND_GUIDE.md"
echo "  - Documentación general: DOCUMENTACION_ALGORITMOS.md"
echo ""

echo -e "${GREEN}¡Feliz análisis forense! 🔍🚀${NC}"
echo ""

