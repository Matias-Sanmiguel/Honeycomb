# Referencia de Endpoints del Backend

Esta es una guía de los endpoints disponibles en el backend y cómo usarlos desde el frontend.

## 1. Backtracking - Análisis de Red
**Endpoint:** `GET /api/forensic/network/{address}`
- **Parámetro:** `{address}` - Dirección de billetera
- **Ejemplo:** `/api/forensic/network/1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa`
- **Descripción:** Analiza la red de transacciones de una billetera
- **Respuesta:**
  - `walletAddress`: Dirección analizada
  - `totalConnections`: Número total de conexiones
  - `totalVolume`: Volumen total de transacciones
  - `walletNodes`: Array de nodos conectados

---

## 2. Greedy - Peel Chains
**Endpoint:** `GET /api/greedy/peel-chains`
- **Parámetros:**
  - `threshold` (default: 0.95)
  - `minChainLength` (default: 3)
  - `limit` (default: 20)
- **Ejemplo:** `/api/greedy/peel-chains?threshold=0.95&minChainLength=3&limit=20`
- **Descripción:** Detecta peel chains usando un enfoque greedy
- **Respuesta:**
  - `threshold`: Umbral utilizado
  - `minChainLength`: Longitud mínima
  - `chainsFound`: Número de cadenas encontradas
  - `chains`: Array de cadenas detectadas

---

## 3. Graph Algorithms
**Endpoint:** `GET /api/graph/{algorithm}`
- **Parámetros:**
  - `sourceAddress` (requerido)
  - `targetAddress` (opcional, para dijkstra y bellman-ford)
- **Algoritmos disponibles:**
  - `dijkstra` - Camino más corto
  - `bellman-ford` - Detección de ciclos negativos
  - `floyd-warshall` - Todas las distancias
  - `prim` - Árbol de expansión mínima
  - `kruskal` - Árbol de expansión mínima (alternativo)
- **Ejemplo:** `/api/graph/dijkstra?sourceAddress=1A1z...&targetAddress=1Bv...`

---

## 4. Pattern Matching
**Endpoint:** `GET /api/forensic/peel-chains/detailed`
- **Parámetros:**
  - `threshold` (default: 0.95)
  - `limit` (default: 100)
- **Ejemplo:** `/api/forensic/peel-chains/detailed?threshold=0.95&limit=20`
- **Descripción:** Detección detallada de peel chains

---

## 5. Wallet Analysis
**Endpoint:** `GET /api/wallet/analyze`
- **Parámetro:** `address` (requerido)
- **Ejemplo:** `/api/wallet/analyze?address=1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa`
- **Descripción:** Análisis completo de una billetera
- **Respuesta:**
  - `address`: Dirección analizada
  - `totalTransactions`: Número total de transacciones
  - `uniqueConnections`: Número de conexiones únicas
  - `totalVolume`: Volumen total
  - `riskScore`: Puntuación de riesgo (0-10)
  - `suspiciousPatterns`: Patrones sospechosos detectados
  - `topConnections`: Top 5 conexiones

---

## 6. Branch & Bound
**Endpoint:** `GET /api/branch-bound/optimal-path`
- **Parámetros:**
  - `sourceAddress` (requerido)
  - `targetAddress` (requerido)
  - `maxDepth` (default: 5)
- **Ejemplo:** `/api/branch-bound/optimal-path?sourceAddress=1A1z...&targetAddress=1Bv...&maxDepth=5`
- **Descripción:** Encuentra la ruta óptima entre dos direcciones
- **Respuesta:**
  - `sourceAddress`: Dirección de origen
  - `targetAddress`: Dirección de destino
  - `path`: Array con el camino
  - `totalCost`: Costo total del camino
  - `pathLength`: Longitud del camino

---

## Notas Importantes

1. **Encoding de URLs:** Todos los parámetros deben ser escapados con `encodeURIComponent()`
2. **CORS:** Todos los endpoints tienen CORS habilitado (`@CrossOrigin(origins = "*")`)
3. **Error Handling:** El backend siempre devuelve un mapa con `error` en caso de falla
4. **Timeouts:** Los análisis complejos pueden tardar, especialmente con profundidades altas

---

## Direcciones de Prueba (Bitcoin)

- `1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa` - Satoshi Nakamoto (Génesis)
- `1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2` - TideBit
- `1dice8EMCyqBb9gYZMn9CbL7F7eHs4R8x` - Dice

