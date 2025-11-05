#!/bin/bash
echo "=========================================="
echo "  PRUEBA DE ENDPOINTS DEL BACKEND"
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
test_endpoint "1. Backtracking - Network Analysis" \
    "$BASE_URL/api/forensic/network/1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
# 2. Test Greedy - Peel Chains
test_endpoint "2. Greedy - Peel Chains" \
    "$BASE_URL/api/greedy/peel-chains?threshold=0.95&minChainLength=3&limit=20"
# 3. Test Graph Algorithms - Dijkstra
test_endpoint "3. Graph - Dijkstra" \
    "$BASE_URL/api/graph/dijkstra?sourceAddress=1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa&targetAddress=1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2"
# 4. Test Graph Algorithms - Bellman-Ford
test_endpoint "4. Graph - Bellman-Ford" \
    "$BASE_URL/api/graph/bellman-ford?sourceAddress=1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa&targetAddress=3J98t1WpEZ73CNmYviecrnyiWrnqRhWNLy"
# 5. Test Graph Algorithms - Floyd-Warshall
test_endpoint "5. Graph - Floyd-Warshall" \
    "$BASE_URL/api/graph/floyd-warshall?sourceAddress=1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
# 6. Test Graph Algorithms - Prim
test_endpoint "6. Graph - Prim" \
    "$BASE_URL/api/graph/prim?sourceAddress=1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
# 7. Test Graph Algorithms - Kruskal
test_endpoint "7. Graph - Kruskal" \
    "$BASE_URL/api/graph/kruskal?sourceAddress=1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
# 8. Test Pattern Matching - Peel Chains Detailed
test_endpoint "8. Pattern Matching - Peel Chains Detailed" \
    "$BASE_URL/api/forensic/peel-chains/detailed?threshold=0.95&limit=20"
# 9. Test Wallet Analysis
test_endpoint "9. Wallet Analysis" \
    "$BASE_URL/api/wallet/analyze?address=1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
# 10. Test Branch & Bound - Optimal Path
test_endpoint "10. Branch & Bound - Optimal Path" \
    "$BASE_URL/api/branch-bound/optimal-path?sourceAddress=1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa&targetAddress=bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh&maxDepth=5"
echo "=========================================="
echo "  PRUEBA COMPLETADA"
echo "=========================================="
