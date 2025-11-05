#!/bin/bash
echo "=========================================="
echo "  PRUEBA DE ENDPOINTS (DATOS CORRECTOS)"
echo "=========================================="
echo ""
BASE_URL="http://localhost:8080"

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

test_endpoint() {
    local name="$1"
    local url="$2"
    echo -e "${YELLOW}Probando: $name${NC}"
    echo "URL: $url"
    response=$(curl -s -w "\n%{http_code}" "$url" 2>&1)
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n-1)

    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✓ OK (200)${NC}"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    else
        echo -e "${RED}✗ FALLO (HTTP $http_code)${NC}"
        echo "$body"
    fi
    echo ""
    echo "---"
    echo ""
}

# 1. Test Backtracking - Network Analysis
test_endpoint "1. Backtracking - Network Analysis (wallet_alice)" \
    "$BASE_URL/api/forensic/network/wallet_alice"

# 2. Test Greedy - Peel Chains
test_endpoint "2. Greedy - Peel Chains" \
    "$BASE_URL/api/greedy/peel-chains?threshold=0.95&minChainLength=3&limit=20"

# 3. Test Graph Algorithms - Dijkstra (con wallets que existen)
test_endpoint "3. Graph - Dijkstra (wallet_alice → wallet_diana)" \
    "$BASE_URL/api/graph/dijkstra?sourceAddress=wallet_alice&targetAddress=wallet_diana"

# 4. Test Graph Algorithms - Bellman-Ford
test_endpoint "4. Graph - Bellman-Ford (wallet_bob → wallet_eve)" \
    "$BASE_URL/api/graph/bellman-ford?sourceAddress=wallet_bob&targetAddress=wallet_eve"

# 5. Test Graph Algorithms - Floyd-Warshall
test_endpoint "5. Graph - Floyd-Warshall" \
    "$BASE_URL/api/graph/floyd-warshall?sourceAddress=wallet_alice"

# 6. Test Graph Algorithms - Prim
test_endpoint "6. Graph - Prim" \
    "$BASE_URL/api/graph/prim?sourceAddress=wallet_charlie"

# 7. Test Graph Algorithms - Kruskal
test_endpoint "7. Graph - Kruskal" \
    "$BASE_URL/api/graph/kruskal?sourceAddress=wallet_diana"

# 8. Test Pattern Matching - Peel Chains Detailed
test_endpoint "8. Pattern Matching - Peel Chains Detailed" \
    "$BASE_URL/api/forensic/peel-chains/detailed?threshold=0.95&limit=20"

# 9. Test Wallet Analysis
test_endpoint "9. Wallet Analysis (wallet_eve)" \
    "$BASE_URL/api/wallet/analyze?address=wallet_eve"

# 10. Test Branch & Bound - Optimal Path
test_endpoint "10. Branch & Bound - Optimal Path (wallet_alice → wallet_eve)" \
    "$BASE_URL/api/branch-bound/optimal-path?sourceAddress=wallet_alice&targetAddress=wallet_eve&maxDepth=5"

echo "=========================================="
echo "  PRUEBA COMPLETADA"
echo "=========================================="

