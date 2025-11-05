#!/bin/bash

# ============================================================
# 📡 EJEMPLOS DE CURL PARA TODOS LOS ENDPOINTS
# ============================================================
# Este script contiene ejemplos listos para usar de todos
# los endpoints del módulo de algoritmos
# ============================================================

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080"

echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}   MÓDULO DE ALGORITMOS - BLOCKCHAIN FORENSICS${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"

# ============================================================
# 1. HEALTH CHECK
# ============================================================
echo -e "\n${YELLOW}[1] HEALTH CHECK${NC}"
echo -e "${YELLOW}─────────────────────────────────────────────────────${NC}"

echo "Verificar que el módulo de algoritmos está disponible:"
echo -e "${GREEN}curl $BASE_URL/api/algorithms/health${NC}"
curl -s "$BASE_URL/api/algorithms/health" | jq . || echo "ERROR: No se puede conectar a $BASE_URL"

# ============================================================
# 2. GREEDY ALGORITHM - PEEL CHAINS
# ============================================================
echo -e "\n${YELLOW}[2] GREEDY ALGORITHM - PEEL CHAINS${NC}"
echo -e "${YELLOW}─────────────────────────────────────────────────────${NC}"
echo "Complejidad: O(n log n)"
echo "Descripción: Detecta patrones de 'peel chain' usando selección greedy"
echo ""

echo "Request (threshold=0.95, limit=10):"
echo -e "${GREEN}curl -X POST $BASE_URL/api/algorithms/greedy/peel-chains \\${NC}"
echo -e "${GREEN}  -H 'Content-Type: application/json' \\${NC}"
echo -e "${GREEN}  -d '{${NC}"
echo -e "${GREEN}    \"threshold\": 0.95,${NC}"
echo -e "${GREEN}    \"limit\": 10${NC}"
echo -e "${GREEN}  }' | jq${NC}"

curl -s -X POST "$BASE_URL/api/algorithms/greedy/peel-chains" \
  -H "Content-Type: application/json" \
  -d '{
    "threshold": 0.95,
    "limit": 10
  }' | jq . || echo "ERROR: Endpoint no disponible"

# ============================================================
# 3. GREEDY ALGORITHM - PEEL CHAIN CLUSTERS
# ============================================================
echo -e "\n${YELLOW}[3] GREEDY ALGORITHM - PEEL CHAIN CLUSTERS${NC}"
echo -e "${YELLOW}─────────────────────────────────────────────────────${NC}"
echo "Complejidad: O(n log n)"
echo "Descripción: Detecta cadenas completas de peel chains"
echo ""

echo "Request (threshold=0.95, limit=5):"
echo -e "${GREEN}curl -X POST $BASE_URL/api/algorithms/greedy/peel-chain-clusters \\${NC}"
echo -e "${GREEN}  -H 'Content-Type: application/json' \\${NC}"
echo -e "${GREEN}  -d '{${NC}"
echo -e "${GREEN}    \"threshold\": 0.95,${NC}"
echo -e "${GREEN}    \"limit\": 5${NC}"
echo -e "${GREEN}  }' | jq${NC}"

curl -s -X POST "$BASE_URL/api/algorithms/greedy/peel-chain-clusters" \
  -H "Content-Type: application/json" \
  -d '{
    "threshold": 0.95,
    "limit": 5
  }' | jq . || echo "ERROR: Endpoint no disponible"

# ============================================================
# 4. DYNAMIC PROGRAMMING - MAX FLOW PATH
# ============================================================
echo -e "\n${YELLOW}[4] DYNAMIC PROGRAMMING - MAX FLOW PATH${NC}"
echo -e "${YELLOW}─────────────────────────────────────────────────────${NC}"
echo "Complejidad: O(V + E)"
echo "Descripción: Encuentra el camino que maximiza valor transferido"
echo ""

echo "Request (source → target, maxHops=10):"
echo -e "${GREEN}curl -X POST $BASE_URL/api/algorithms/dp/max-flow-path \\${NC}"
echo -e "${GREEN}  -H 'Content-Type: application/json' \\${NC}"
echo -e "${GREEN}  -d '{${NC}"
echo -e "${GREEN}    \"sourceWallet\": \"1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa\",${NC}"
echo -e "${GREEN}    \"targetWallet\": \"1dice8EMCQAqQSN7ufuN6Ent9Qc1q5W9z\",${NC}"
echo -e "${GREEN}    \"maxHops\": 10${NC}"
echo -e "${GREEN}  }' | jq${NC}"

curl -s -X POST "$BASE_URL/api/algorithms/dp/max-flow-path" \
  -H "Content-Type: application/json" \
  -d '{
    "sourceWallet": "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    "targetWallet": "1dice8EMCQAqQSN7ufuN6Ent9Qc1q5W9z",
    "maxHops": 10
  }' | jq . || echo "ERROR: Endpoint no disponible o no hay datos"

# ============================================================
# 5. GRAPH ALGORITHMS - BETWEENNESS CENTRALITY
# ============================================================
echo -e "\n${YELLOW}[5] GRAPH ALGORITHMS - BETWEENNESS CENTRALITY${NC}"
echo -e "${YELLOW}─────────────────────────────────────────────────────${NC}"
echo "Complejidad: O(V·E)"
echo "Descripción: Identifica wallets puente críticas en la red"
echo ""

echo "Request (topN=10):"
echo -e "${GREEN}curl $BASE_URL/api/algorithms/graph/centrality?topN=10 | jq${NC}"

curl -s "$BASE_URL/api/algorithms/graph/centrality?topN=10" | jq . || echo "ERROR: Endpoint no disponible"

# ============================================================
# 6. GRAPH ALGORITHMS - COMMUNITY DETECTION
# ============================================================
echo -e "\n${YELLOW}[6] GRAPH ALGORITHMS - COMMUNITY DETECTION${NC}"
echo -e "${YELLOW}─────────────────────────────────────────────────────${NC}"
echo "Complejidad: O(V log V + E)"
echo "Descripción: Detecta comunidades/clusters de wallets"
echo ""

echo "Request (minSize=3):"
echo -e "${GREEN}curl $BASE_URL/api/algorithms/graph/communities?minSize=3 | jq${NC}"

curl -s "$BASE_URL/api/algorithms/graph/communities?minSize=3" | jq . || echo "ERROR: Endpoint no disponible"

# ============================================================
# 7. GRAPH ALGORITHMS - NODE IMPORTANCE
# ============================================================
echo -e "\n${YELLOW}[7] GRAPH ALGORITHMS - NODE IMPORTANCE${NC}"
echo -e "${YELLOW}─────────────────────────────────────────────────────${NC}"
echo "Complejidad: O(V log V)"
echo "Descripción: Calcula importancia de nodos (Page Rank)"
echo ""

echo "Request (topN=20):"
echo -e "${GREEN}curl $BASE_URL/api/algorithms/graph/node-importance?topN=20 | jq${NC}"

curl -s "$BASE_URL/api/algorithms/graph/node-importance?topN=20" | jq . || echo "ERROR: Endpoint no disponible"

# ============================================================
# 8. PATTERN MATCHING - DETECT ANOMALIES
# ============================================================
echo -e "\n${YELLOW}[8] PATTERN MATCHING - DETECT ANOMALIES${NC}"
echo -e "${YELLOW}─────────────────────────────────────────────────────${NC}"
echo "Complejidad: O(n) a O(n²)"
echo "Descripción: Detecta 4 patrones: MIXING, CYCLICAL, RAPID, ANOMALY"
echo ""

echo "Request (todos los patrones):"
echo -e "${GREEN}curl -X POST $BASE_URL/api/algorithms/pattern/detect-anomalies \\${NC}"
echo -e "${GREEN}  -H 'Content-Type: application/json' \\${NC}"
echo -e "${GREEN}  -d '{${NC}"
echo -e "${GREEN}    \"analysisDepth\": 5,${NC}"
echo -e "${GREEN}    \"timeWindowDays\": 30,${NC}"
echo -e "${GREEN}    \"anomalyThreshold\": 2.5,${NC}"
echo -e "${GREEN}    \"patterns\": [\"MIXING\", \"CYCLICAL\", \"RAPID\", \"ANOMALY\"]${NC}"
echo -e "${GREEN}  }' | jq${NC}"

curl -s -X POST "$BASE_URL/api/algorithms/pattern/detect-anomalies" \
  -H "Content-Type: application/json" \
  -d '{
    "analysisDepth": 5,
    "timeWindowDays": 30,
    "anomalyThreshold": 2.5,
    "patterns": ["MIXING", "CYCLICAL", "RAPID", "ANOMALY"]
  }' | jq . || echo "ERROR: Endpoint no disponible"

# ============================================================
# CASOS DE USO COMBINADOS
# ============================================================
echo -e "\n${BLUE}════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}   CASOS DE USO COMBINADOS${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"

# Caso 1: Pipeline completo de análisis
echo -e "\n${YELLOW}[CASO 1] Pipeline Completo${NC}"
echo -e "${YELLOW}─────────────────────────────────────────────────────${NC}"
echo "Flujo: Detectar Peel Chains → Centralidad → Patrones"
echo ""
echo "Paso 1: Detectar peel chains (wallets sospechosas)"
echo -e "${GREEN}PEEL_CHAINS=\$(curl -s -X POST $BASE_URL/api/algorithms/greedy/peel-chains \\${NC}"
echo -e "${GREEN}  -H 'Content-Type: application/json' -d '{\"threshold\":0.95}')${NC}"
echo ""
echo "Paso 2: Analizar centralidad para identificar mixers"
echo -e "${GREEN}CENTRALITY=\$(curl -s $BASE_URL/api/algorithms/graph/centrality?topN=10)${NC}"
echo ""
echo "Paso 3: Detectar patrones sospechosos"
echo -e "${GREEN}PATTERNS=\$(curl -s -X POST $BASE_URL/api/algorithms/pattern/detect-anomalies \\${NC}"
echo -e "${GREEN}  -H 'Content-Type: application/json' -d '{...}')${NC}"

# Caso 2: Análisis de comunidades sospechosas
echo -e "\n${YELLOW}[CASO 2] Análisis de Comunidades Sospechosas${NC}"
echo -e "${YELLOW}─────────────────────────────────────────────────────${NC}"
echo "Objetivo: Encontrar grupos coordinados de wallets"
echo ""
echo -e "${GREEN}curl $BASE_URL/api/algorithms/graph/communities?minSize=5 | jq '${NC}"
echo -e "${GREEN}.communities | map(select(.suspiciousLevel==\"HIGH\" or .suspiciousLevel==\"CRITICAL\"))${NC}"
echo -e "${GREEN}'${NC}"

# Caso 3: Rastreo de fondos entre wallets
echo -e "\n${YELLOW}[CASO 3] Rastreo de Máximo Flujo${NC}"
echo -e "${YELLOW}─────────────────────────────────────────────────────${NC}"
echo "Objetivo: Seguir el camino de máximo valor entre dos wallets"
echo ""
echo -e "${GREEN}# Encontrar el camino que maximiza la cantidad de BTC transferida${NC}"
echo -e "${GREEN}curl -X POST $BASE_URL/api/algorithms/dp/max-flow-path \\${NC}"
echo -e "${GREEN}  -H 'Content-Type: application/json' \\${NC}"
echo -e "${GREEN}  -d '{${NC}"
echo -e "${GREEN}    \"sourceWallet\": \"WALLET_ORIGEN\",${NC}"
echo -e "${GREEN}    \"targetWallet\": \"WALLET_DESTINO\",${NC}"
echo -e "${GREEN}    \"maxHops\": 15${NC}"
echo -e "${GREEN}  }' | jq '.path | length'${NC}"

# ============================================================
# RESUMEN
# ============================================================
echo -e "\n${BLUE}════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}   RESUMEN DE ALGORITMOS${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"

cat << EOF

┌─────────────────────────────────────────────────────────┐
│  ALGORITMO                      │ COMPLEJIDAD │ PUNTOS  │
├─────────────────────────────────────────────────────────┤
│  Greedy Peel Chains             │ O(n log n)  │  1     │
│  Dynamic Programming Max Flow   │ O(V + E)    │  2     │
│  Betweenness Centrality         │ O(V·E)      │  2     │
│  Community Detection            │ O(V log V+E)│  2     │
│  Pattern Matching (4 patrones)  │ O(n) a O(n²)│  2     │
├─────────────────────────────────────────────────────────┤
│  TOTAL                          │             │  9     │
└─────────────────────────────────────────────────────────┘

CARACTERÍSTICAS IMPLEMENTADAS:
✅ 5 endpoints REST documentados
✅ Validación de inputs
✅ Manejo robusto de excepciones
✅ Logging detallado
✅ DTOs específicos para cada algoritmo
✅ Queries Cypher optimizadas
✅ Docker & Docker Compose
✅ Tests unitarios
✅ Documentación completa

BASE DE DATOS:
• Neo4j 5.15 en puerto 7687 (Bolt)
• Browser en http://localhost:7474
• Usuario: neo4j
• Contraseña: password

APLICACIÓN:
• Spring Boot 3.2
• Java 17
• Puerto 8080
• Health check: /api/algorithms/health

EOF

echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}✅ Ejemplos completados. Revisa DOCUMENTACION_ALGORITMOS.md para más detalles.${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"

