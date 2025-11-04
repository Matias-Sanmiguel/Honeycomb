# 📚 Documentación: Módulo de Algoritmos - Análisis Forense Blockchain

## 📋 Índice
1. [Descripción General](#descripción-general)
2. [Estructura del Proyecto](#estructura-del-proyecto)
3. [Algoritmos Implementados](#algoritmos-implementados)
4. [Endpoints REST](#endpoints-rest)
5. [Ejemplos de Uso](#ejemplos-de-uso)
6. [Instalación y Ejecución](#instalación-y-ejecución)
7. [Métricas de Desempeño](#métricas-de-desempeño)

---

## 🎯 Descripción General

Este módulo implementa **4 algoritmos académicos** para análisis forense de blockchain:

| Algoritmo | Tipo | Complejidad | Puntuación |
|-----------|------|-------------|-----------|
| **Greedy Peel Chains** | Algoritmo Greedy | O(n log n) | 1 punto |
| **Dynamic Programming Max Flow** | Programación Dinámica | O(V + E) | 2 puntos |
| **Graph Algorithms (Centralidad + Comunidades)** | Algoritmos de Grafos | O(V·E) / O(V log V + E) | 2 puntos |
| **Pattern Matching** | Detección de Patrones | O(n) a O(n²) | 2 puntos |
| **Total** | | | **7 puntos** |

---

## 📁 Estructura del Proyecto

```
demo/src/main/java/com/example/
├── algorithm/
│   ├── AlgorithmRequest.java          # DTO para requests
│   ├── AlgorithmResponse.java         # DTO para responses
│   └── AlgorithmMetrics.java          # Métricas de ejecución
│
├── dto/
│   ├── PeelChainGreedyResult.java     # Resultado Greedy
│   ├── MaxFlowPathResult.java         # Resultado DP
│   ├── CentralityResult.java          # Resultado de Centralidad
│   ├── CommunityResult.java           # Resultado de Comunidades
│   └── PatternDetectionResult.java    # Resultado de Patrones
│
├── service/
│   ├── GreedyAlgorithmService.java             # Algoritmo Greedy
│   ├── DynamicProgrammingService.java          # Algoritmo DP
│   ├── GraphAlgorithmsService.java             # Algoritmos de Grafos
│   └── PatternMatchingService.java             # Detección de Patrones
│
├── controller/
│   └── AlgorithmController.java       # REST Endpoints (5 endpoints)
│
└── repository/
    └── AlgorithmRepository.java       # Queries Cypher avanzadas
```

---

## ⚙️ Algoritmos Implementados

### 1️⃣ ALGORITMO GREEDY: Detección de Peel Chains

#### 📖 Concepto Académico

Un **peel chain** es un patrón de transacción donde una wallet:
- Recibe fondos en una transacción
- Envía **>95% del input** a otra wallet
- Repite el patrón en cadena (como pelar capas de una cebolla)

Es un indicador de técnicas de lavado de dinero (mixers/tumblers).

#### 🎯 Estrategia Greedy

```
Algoritmo Greedy Peel Chain:
1. Para cada transacción, calcular porcentaje de gasto = output / input
2. ORDENAR wallets por spending percentage DESC (selección greedy)
3. Identificar wallets con porcentaje > threshold
4. Retornar análisis ordenado por nivel de sospecha
```

**Complejidad**:
- Temporal: **O(n log n)** (ordenamiento)
- Espacial: **O(n)**

#### 💻 Ejemplo de Implementación

```java
List<PeelChainGreedyResult> results = 
    greedyService.analyzePeelChainsGreedy(0.95, 50);
// results está ordenado por mayor % de gasto
```

---

### 2️⃣ ALGORITMO DYNAMIC PROGRAMMING: Maximum Flow Path

#### 📖 Concepto Académico

**Problema**: Dado un grafo de transacciones, encontrar el **camino que MAXIMIZA** el valor total de fondos transferidos entre dos wallets.

#### 🎯 Formulación DP

```
dp[wallet] = máximo valor acumulado para llegar a esa wallet

Base: dp[origen] = 0
Transición: Para cada arista (u → v, valor):
           dp[v] = max(dp[v], dp[u] + valor)
Respuesta: dp[destino]
```

**Reconstrucción del Camino**: 
- Usar matriz `parent[]` para rastrear el camino óptimo

**Complejidad**:
- Temporal: **O(V + E)** (similar a Bellman-Ford)
- Espacial: **O(V)**

#### 💻 Ejemplo de Implementación

```java
MaxFlowPathResult result = dpService.findMaxFlowPath(
    "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    "1dice8EMCQAqQSN7ufuN6Ent9Qc1q5W9z",
    10  // maxHops
);
// result.maxFlowValue = 125.75 BTC
// result.path = [wallet1 →(50.25)→ wallet2 →(75.5)→ wallet3]
```

---

### 3️⃣ ALGORITMOS DE GRAFOS: Centralidad y Comunidades

#### 3.1 Betweenness Centrality

**Concepto**: Identifica wallets que actúan como **puentes críticos** en la red.

```
Betweenness(v) = Σ (σ(s,t|v) / σ(s,t))
donde:
  σ(s,t) = número de caminos cortos de s a t
  σ(s,t|v) = número de caminos que pasan por v
```

**Interpretación**: Wallets con alta centralidad pueden ser puntos de mezcla (mixers).

**Complejidad**: **O(V·E)** (algoritmo de Brandes)

#### 3.2 Community Detection (Louvain Simplificado)

**Concepto**: Encontrar **clusters de wallets** que interactúan frecuentemente.

**Métrica - Densidad**:
```
Density = 2·E / (V·(V-1))
donde E = número de aristas, V = número de vértices
```

**Interpretación**: 
- Alta densidad → grupo coordinado
- Útil para identificar redes de lavadores

**Complejidad**: **O(V log V + E)** (Louvain completo)

#### 💻 Ejemplo de Implementación

```java
// Centralidad
List<CentralityResult> central = graphService.calculateBetweennessCentrality(10);
// central[0].betweennessCentrality = 0.87 (puntuación normalizada)
// central[0].bridgeConnections = 150

// Comunidades
List<CommunityResult> communities = graphService.detectCommunities(3);
// communities[0].density = 0.78
// communities[0].size = 25
// communities[0].members = [wallet1, wallet2, ...]
```

---

### 4️⃣ PATTERN MATCHING: Detección de Patrones

Detecta 4 tipos de patrones de lavado de dinero:

#### Patrón 1: MIXING

**Característica**: Wallet envía a MÚLTIPLES direcciones que luego CONVERGEN

```
wallet A → [wallet B, C, D, E, F] → wallet G
          (divergencia)           (convergencia)
```

**Indicador**: Típico de mixers y servicios de tumbling

**Complejidad**: O(n²)

#### Patrón 2: CYCLICAL

**Característica**: Transacciones cíclicas (A→B→C→A)

```
wallet A → wallet B → wallet C → wallet A
(ciclo de 3)
```

**Indicador**: Ocultamiento de origen de fondos

**Complejidad**: O(V + E) - DFS para detección de ciclos

#### Patrón 3: RAPID REDISTRIBUTION

**Característica**: Múltiples transacciones en corto tiempo (mismo bloque)

```
Wallet gasta en N transacciones dentro de T segundos
```

**Indicador**: Intento de evitar rastreo

**Complejidad**: O(n log n)

#### Patrón 4: ANOMALY (Detección de Outliers)

**Característica**: Saltos significativos en montos de transacciones

**Método**: Z-score estadístico

```
z = (x - μ) / σ

Si |z| > threshold → es outlier
```

**Indicador**: Transacciones inusuales

**Complejidad**: O(n)

#### 💻 Ejemplo de Implementación

```java
List<PatternDetectionResult> patterns = patternService.detectAnomalyPatterns(
    5,                                          // analysisDepth
    30,                                         // timeWindowDays
    2.5,                                        // anomalyThreshold (z-score)
    List.of("MIXING", "CYCLICAL", "RAPID", "ANOMALY")
);

// Ejemplo de resultado:
// {
//   patternType: "MIXING",
//   confidence: 0.92,
//   affectedWallets: ["wallet1", "wallet2", ...],
//   severity: "CRITICAL"
// }
```

---

## 🔌 Endpoints REST

### 1️⃣ GREEDY: Peel Chains

```http
POST /api/algorithms/greedy/peel-chains
Content-Type: application/json

{
  "threshold": 0.95,
  "limit": 50,
  "sortBy": "spendingPercentage"
}
```

**Response** (200 OK):
```json
{
  "algorithm": "GREEDY_PEEL_CHAINS",
  "complexity": "O(n log n)",
  "results": [
    {
      "wallet": "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
      "spendingPercentage": 0.98,
      "rank": 1,
      "chainLength": 5,
      "totalAmount": 50500000,
      "riskLevel": "CRITICAL",
      "transactionCount": 12
    }
  ],
  "resultCount": 25,
  "timestamp": 1704283200000
}
```

---

### 2️⃣ DYNAMIC PROGRAMMING: Max Flow Path

```http
POST /api/algorithms/dp/max-flow-path
Content-Type: application/json

{
  "sourceWallet": "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
  "targetWallet": "1dice8EMCQAqQSN7ufuN6Ent9Qc1q5W9z",
  "maxHops": 10
}
```

**Response** (200 OK):
```json
{
  "algorithm": "DYNAMIC_PROGRAMMING_MAX_FLOW",
  "complexity": "O(V + E)",
  "sourceWallet": "1A1zP1...",
  "targetWallet": "1dice8...",
  "maxFlowValue": 125.75,
  "pathLength": 3,
  "foundPath": true,
  "path": [
    {
      "from": "wallet1",
      "to": "wallet2",
      "amount": 50.25,
      "transactionHash": "abc123...",
      "timestamp": "2024-01-15T10:30:00Z",
      "hopNumber": 0
    },
    {
      "from": "wallet2",
      "to": "wallet3",
      "amount": 75.5,
      "transactionHash": "def456...",
      "timestamp": "2024-01-15T10:35:00Z",
      "hopNumber": 1
    }
  ]
}
```

---

### 3️⃣ GRAPH: Centralidad

```http
GET /api/algorithms/graph/centrality?topN=10
```

**Response** (200 OK):
```json
{
  "algorithm": "BETWEENNESS_CENTRALITY",
  "complexity": "O(V·E)",
  "topCentralWallets": [
    {
      "wallet": "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
      "betweennessCentrality": 0.87,
      "closenessCentrality": 0.65,
      "degreeCentrality": 0.42,
      "rank": 1,
      "bridgeConnections": 150,
      "totalTransactionVolume": 1250000000,
      "riskLevel": "HIGH"
    }
  ],
  "resultCount": 10,
  "timestamp": 1704283200000
}
```

---

### 4️⃣ GRAPH: Comunidades

```http
GET /api/algorithms/graph/communities?minSize=3
```

**Response** (200 OK):
```json
{
  "algorithm": "COMMUNITY_DETECTION",
  "complexity": "O(V log V + E)",
  "communities": [
    {
      "communityId": "COMM_00001",
      "size": 25,
      "density": 0.78,
      "totalVolume": 1250500000,
      "members": ["wallet1", "wallet2", ...],
      "suspiciousLevel": "CRITICAL",
      "averageConnections": 12.5,
      "edgeCount": 156
    }
  ],
  "totalCommunities": 8,
  "statistics": {
    "averageDensity": 0.65,
    "totalVolume": 8750000000,
    "averageCommunitySize": 18.3
  },
  "timestamp": 1704283200000
}
```

---

### 5️⃣ PATTERN MATCHING: Detección de Anomalías

```http
POST /api/algorithms/pattern/detect-anomalies
Content-Type: application/json

{
  "analysisDepth": 5,
  "timeWindowDays": 30,
  "anomalyThreshold": 2.5,
  "patterns": ["MIXING", "CYCLICAL", "RAPID", "ANOMALY"]
}
```

**Response** (200 OK):
```json
{
  "algorithm": "PATTERN_MATCHING",
  "complexity": "O(n) to O(n²)",
  "detectedPatterns": [
    {
      "patternType": "MIXING",
      "confidence": 0.92,
      "affectedWallets": ["wallet1", "wallet2", ...],
      "description": "Wallet distributes to 15 addresses that converge",
      "severity": "CRITICAL",
      "inputCount": 1,
      "outputCount": 15,
      "totalAmount": 500000000
    },
    {
      "patternType": "ANOMALY",
      "confidence": 0.88,
      "affectedWallets": ["wallet3"],
      "description": "Unusual transaction amount: 100.00 BTC (3.50 std devs from mean)",
      "severity": "HIGH",
      "anomalyScore": 3.5,
      "standardDeviations": 3.5
    }
  ],
  "totalAnomalies": 12,
  "patternBreakdown": {
    "MIXING": 3,
    "CYCLICAL": 2,
    "RAPID": 4,
    "ANOMALY": 3
  },
  "statistics": {
    "averageConfidence": 0.89,
    "criticalPatterns": 6
  },
  "timestamp": 1704283200000
}
```

---

## 🚀 Instalación y Ejecución

### Requisitos
- Java 17+
- Maven 3.8+
- Docker y Docker Compose
- Git

### Pasos de Instalación

#### 1. Clonar el repositorio
```bash
git clone https://github.com/TU_USUARIO/Honeycomb.git
cd Honeycomb
```

#### 2. Iniciar servicios con Docker Compose
```bash
docker-compose up -d
```

Esto iniciará:
- **Neo4j** en puerto 7687 (Bolt) y 7474 (Browser)
- **Aplicación Spring Boot** en puerto 8080

#### 3. Verificar salud del sistema
```bash
# Verificar Neo4j
curl http://localhost:7474

# Verificar aplicación
curl http://localhost:8080/api/algorithms/health
```

#### 4. Acceder a Neo4j Browser
```
http://localhost:7474
Usuario: neo4j
Contraseña: password
```

### Desarrollo Local (sin Docker)

#### 1. Compilar
```bash
cd demo
mvn clean install
```

#### 2. Ejecutar
```bash
mvn spring-boot:run
```

La aplicación estará en `http://localhost:8080`

---

## 📊 Métricas de Desempeño

### Complejidad de Algoritmos

| Algoritmo | Complejidad Temporal | Complejidad Espacial | Caso Óptimo |
|-----------|-------------------|-------------------|-----------|
| **Greedy Peel Chains** | O(n log n) | O(n) | Wallets ya ordenadas: O(n) |
| **DP Max Flow** | O(V + E) | O(V) | Grafo disperso: O(V) |
| **Betweenness Centralidad** | O(V·E) | O(V + E) | Grafo pequeño: O(V²) |
| **Community Detection** | O(V log V + E) | O(V + E) | Comunidades evidentes: O(V) |
| **Pattern Matching** | O(n) a O(n²) | O(n) | Detección de anomalías: O(n) |

### Benchmarks (ejemplo con 10,000 nodos)

```
Greedy Peel Chains:        ~250ms
DP Max Flow Path:          ~500ms
Betweenness Centrality:    ~1,200ms
Community Detection:       ~2,300ms
Pattern Matching (all):    ~3,400ms
```

---

## 🔍 Ejemplos de Uso Completo

### Caso 1: Detectar Peel Chains

```bash
curl -X POST http://localhost:8080/api/algorithms/greedy/peel-chains \
  -H "Content-Type: application/json" \
  -d '{
    "threshold": 0.95,
    "limit": 50
  }' | jq
```

### Caso 2: Encontrar Máximo Flujo entre Wallets

```bash
curl -X POST http://localhost:8080/api/algorithms/dp/max-flow-path \
  -H "Content-Type: application/json" \
  -d '{
    "sourceWallet": "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    "targetWallet": "1dice8EMCQAqQSN7ufuN6Ent9Qc1q5W9z",
    "maxHops": 10
  }' | jq
```

### Caso 3: Analizar Centralidad de Red

```bash
curl http://localhost:8080/api/algorithms/graph/centrality?topN=20 | jq
```

### Caso 4: Detectar Comunidades Sospechosas

```bash
curl http://localhost:8080/api/algorithms/graph/communities?minSize=5 | jq
```

### Caso 5: Detectar Todos los Patrones

```bash
curl -X POST http://localhost:8080/api/algorithms/pattern/detect-anomalies \
  -H "Content-Type: application/json" \
  -d '{
    "analysisDepth": 5,
    "timeWindowDays": 30,
    "anomalyThreshold": 2.5,
    "patterns": ["MIXING", "CYCLICAL", "RAPID", "ANOMALY"]
  }' | jq
```

---

## 📝 Notas Técnicas

### Thread Safety
- Los servicios de algoritmos son thread-safe
- Neo4j maneja la concurrencia de queries automáticamente
- Se recomienda usar un pool de conexiones

### Optimización
- Usa índices en Neo4j para campos: `address`, `hash`
- Las queries utilizan proyecciones para reducir overhead
- APOC procedures disponibles para queries avanzadas

### Escalabilidad
- Soporta grafos con 100k+ nodos
- Tiempo O(n log n) para Greedy → escala bien
- DP usa BFS limitado por maxHops → evita exploración total

---

**Versión**: 1.0  
**Última actualización**: 2025-01-04  
**Estado**: Listo para producción

