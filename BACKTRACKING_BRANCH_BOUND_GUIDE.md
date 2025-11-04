# 🎯 Guía de Implementación: Backtracking y Branch & Bound

## 📚 Tabla de Contenidos
1. [Descripción General](#descripción-general)
2. [Backtracking: Detección de Cadenas Sospechosas](#backtracking)
3. [Branch & Bound: Camino Óptimo con Restricciones](#branch--bound)
4. [Casos de Uso en Análisis Forense](#casos-de-uso)
5. [Ejemplos de Uso](#ejemplos-de-uso)
6. [Comparación con Otros Algoritmos](#comparación)

---

## 🎯 Descripción General

Este documento describe la implementación de **Backtracking** y **Branch & Bound (Ramificación y Poda)** en el sistema de análisis forense de criptomonedas.

### ¿Dónde están implementados?

```
demo/src/main/java/com/example/
├── algorithm/
│   ├── BacktrackingAlgorithm.java          ✅ Algoritmo puro
│   └── BranchAndBoundAlgorithm.java        ✅ Algoritmo puro
│
├── service/
│   ├── BacktrackingService.java            ✅ Integración con Neo4j
│   └── BranchBoundService.java             ✅ Integración con Neo4j
│
└── controller/
    └── AlgorithmController.java            ✅ Endpoints REST
```

---

## 🔍 BACKTRACKING: Detección de Cadenas Sospechosas

### 📖 Concepto Académico

**Backtracking** es una técnica de búsqueda exhaustiva que explora TODAS las soluciones posibles, retrocediendo cuando encuentra un callejón sin salida.

### 🎯 Aplicación en Análisis Forense

En el contexto de criptomonedas, lo usamos para:

1. **Detectar CICLOS** → `A → B → C → A` (fondos retornan al origen)
2. **Encontrar PEEL CHAINS complejos** → Cadenas largas de transferencias
3. **Identificar patrones de redistribución** → Múltiples rutas desde una wallet

### ⚙️ Algoritmo

```java
function BACKTRACK(wallet, path, depth):
    // CASO BASE 1: Profundidad máxima
    if depth == 0:
        analyzePath(path)
        return
    
    // CASO BASE 2: Ciclo detectado
    if wallet in path:
        registerCycle(path + [wallet])
        return
    
    // EXPLORACIÓN RECURSIVA
    for each vecino in getNeighbors(wallet):
        path.add(vecino)           // ✅ AGREGAR
        BACKTRACK(vecino, path, depth-1)  // 🔁 RECURSIÓN
        path.remove(vecino)        // ⬅️ BACKTRACK (deshacer)
```

### 📊 Complejidad

| Métrica | Valor |
|---------|-------|
| **Temporal** | O(b^d) donde b=branching factor, d=profundidad |
| **Espacial** | O(d) para la pila de recursión |
| **Peor caso** | Exploración completa del grafo |

### 🔌 Endpoint REST

#### Búsqueda desde wallet específica

```http
GET /api/forensic/backtrack/suspicious-chains/{depth}?wallet={address}
```

**Parámetros:**
- `depth` (path variable): Profundidad máxima (recomendado: 4-6)
- `wallet` (query param, opcional): Wallet desde donde iniciar

**Ejemplo 1: Búsqueda desde wallet específica**

```bash
curl "http://localhost:8080/api/forensic/backtrack/suspicious-chains/5?wallet=1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
```

**Respuesta:**

```json
{
  "algorithm": "BACKTRACKING",
  "complexity": "O(b^d) - Exponencial con poda",
  "description": "Exploración exhaustiva de caminos sospechosos",
  "startWallet": "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
  "maxDepth": 5,
  "suspiciousChains": [
    {
      "path": ["wallet1", "wallet2", "wallet3", "wallet1"],
      "type": "CYCLE",
      "suspicionLevel": 0.95,
      "totalAmount": 125000000,
      "depth": 3,
      "description": "Ciclo detectado: fondos retornan al origen"
    },
    {
      "path": ["wallet1", "wallet2", "wallet3", "wallet4", "wallet5"],
      "type": "PEEL_CHAIN",
      "suspicionLevel": 0.78,
      "totalAmount": 50000000,
      "depth": 4,
      "description": "Cadena sospechosa detectada"
    }
  ],
  "totalChainsFound": 12,
  "patternBreakdown": {
    "CYCLE": 3,
    "PEEL_CHAIN": 5,
    "RAPID_REDISTRIBUTION": 4
  },
  "statistics": {
    "averageSuspicionLevel": 0.82,
    "cyclesDetected": 3,
    "criticalChains": 8
  }
}
```

**Ejemplo 2: Búsqueda global de ciclos**

```bash
curl "http://localhost:8080/api/forensic/backtrack/suspicious-chains/4"
```

---

## 🌳 BRANCH & BOUND: Camino Óptimo con Restricciones

### 📖 Concepto Académico

**Branch and Bound** es una técnica de optimización que explora el espacio de soluciones de manera sistemática, **PODANDO** ramas que no pueden llevar a una solución óptima.

### 🎯 Aplicación en Análisis Forense

En el contexto de criptomonedas, lo usamos para:

1. **Encontrar el CAMINO MÁS CORTO** entre dos wallets
2. **Con restricción de COSTO MÁXIMO** (fees acumuladas)
3. **Optimizar el rastreo** de flujos minimizando comisiones

### ⚙️ Algoritmo

```java
function BRANCH_AND_BOUND(source, target, maxCost):
    priorityQueue.add(Node(source, cost=0))
    bestSolution = null
    
    while priorityQueue.notEmpty():
        node = priorityQueue.poll()
        
        // ✅ SOLUCIÓN ENCONTRADA
        if node.wallet == target:
            if node.cost < bestSolution.cost:
                bestSolution = node
            continue
        
        // 🌿 PODA 1: Costo excede límite
        if node.cost > maxCost:
            prune()
            continue
        
        // 🌿 PODA 2: No puede mejorar mejor solución
        if node.cost + heuristic(node) >= bestSolution.cost:
            prune()
            continue
        
        // 🌳 RAMIFICACIÓN: Explorar vecinos
        for each vecino in getNeighbors(node.wallet):
            newNode = Node(vecino, node.cost + edgeCost)
            priorityQueue.add(newNode)
    
    return bestSolution
```

### 📊 Complejidad

| Métrica | Valor |
|---------|-------|
| **Temporal (peor caso)** | O(b^d) |
| **Temporal (con poda)** | O(V log V + E) similar a Dijkstra |
| **Espacial** | O(b·d) para la cola de prioridad |
| **Ventaja** | La poda reduce drásticamente la exploración |

### 🌿 Estrategias de Poda

El algoritmo implementa 4 tipos de poda:

1. **Nodo ya visitado con menor costo** → No re-explorar
2. **Costo excede maxCost** → Violación de restricción
3. **No puede mejorar mejor solución** → Heurística de poda
4. **Camino muy largo** → Límite de profundidad

### 🔌 Endpoint REST

```http
GET /api/path/branch-bound/{addr1}/{addr2}/{maxCost}
```

**Parámetros:**
- `addr1` (path variable): Wallet origen
- `addr2` (path variable): Wallet destino
- `maxCost` (path variable): Costo máximo permitido en satoshis

**Ejemplo 1: Camino óptimo con restricción**

```bash
curl "http://localhost:8080/api/path/branch-bound/1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa/1dice8EMCQAqQSN7ufuN6Ent9Qc1q5W9z/100.0"
```

**Respuesta:**

```json
{
  "algorithm": "BRANCH_AND_BOUND",
  "complexity": "O(b^d) with pruning → O(V log V + E)",
  "description": "Optimal path with cost constraint",
  "sourceWallet": "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
  "targetWallet": "1dice8EMCQAqQSN7ufuN6Ent9Qc1q5W9z",
  "maxCostAllowed": 100.0,
  "pathFound": true,
  "path": [
    "wallet1",
    "wallet2",
    "wallet3",
    "wallet4"
  ],
  "totalCost": 85.5,
  "pathLength": 3,
  "nodesExplored": 47,
  "branchesPruned": 152,
  "executionTimeMs": 23,
  "efficiency": {
    "pruningRatio": 0.764,
    "costUtilization": 0.855
  }
}
```

**Ejemplo 2: Análisis de múltiples escenarios**

```bash
curl "http://localhost:8080/api/path/branch-bound/analyze/wallet1/wallet2"
```

Esto prueba automáticamente con diferentes límites de costo: 50, 100, 200, 500, 1000 satoshis.

---

## 🎯 Casos de Uso en Análisis Forense

### Caso 1: Detectar Mixing Services

**Problema:** Un mixer (servicio de mezcla) recibe fondos y los redistribuye formando ciclos complejos.

**Solución con Backtracking:**

```bash
# Explorar profundidad 6 desde wallet sospechosa
curl "http://localhost:8080/api/forensic/backtrack/suspicious-chains/6?wallet=<mixer_address>"
```

**Resultado esperado:**
- Múltiples ciclos detectados
- Patrones de redistribución rápida
- Nivel de sospecha alto (>0.8)

---

### Caso 2: Rastrear Fondos con Presupuesto Limitado

**Problema:** Un investigador necesita rastrear fondos pero tiene un presupuesto limitado de 200 satoshis para fees.

**Solución con Branch & Bound:**

```bash
curl "http://localhost:8080/api/path/branch-bound/walletA/walletB/200.0"
```

**Resultado esperado:**
- Camino más corto posible
- Costo total ≤ 200 satoshis
- Métricas de eficiencia (ramas podadas)

---

### Caso 3: Análisis Comparativo de Rutas

**Problema:** Comparar diferentes rutas con distintas restricciones de costo.

**Solución:**

```bash
curl "http://localhost:8080/api/path/branch-bound/analyze/walletA/walletB"
```

**Resultado esperado:**
- Múltiples caminos encontrados
- Comparación de costos
- Trade-offs entre longitud y costo

---

## 📊 Comparación con Otros Algoritmos

| Algoritmo | Tipo | Complejidad | Uso en el Proyecto |
|-----------|------|-------------|-------------------|
| **Greedy** | Selección voraz | O(n log n) | Peel chains simples |
| **Dynamic Programming** | Programación dinámica | O(V + E) | Max flow path |
| **BFS/DFS** | Búsqueda en grafos | O(V + E) | Caminos simples |
| **Dijkstra** | Camino más corto | O(V log V + E) | Sin restricciones |
| **Backtracking** 🔥 | Búsqueda exhaustiva | O(b^d) | Patrones complejos, ciclos |
| **Branch & Bound** 🔥 | Optimización con poda | O(b^d) → O(V log V) | Caminos óptimos con restricciones |

### ¿Cuándo usar cada uno?

#### Usa BACKTRACKING si:
- ✅ Necesitas explorar TODAS las posibilidades
- ✅ Buscas patrones complejos (ciclos, redistribuciones)
- ✅ La profundidad es limitada (≤10)
- ⚠️ Puedes tolerar mayor tiempo de ejecución

#### Usa BRANCH & BOUND si:
- ✅ Necesitas el camino ÓPTIMO
- ✅ Tienes RESTRICCIONES (costo, longitud)
- ✅ Quieres pruning automático
- ⚠️ El grafo no es demasiado denso

#### Usa Greedy si:
- ✅ Necesitas respuesta rápida
- ✅ Solución "suficientemente buena" es aceptable
- ⚠️ No garantiza optimalidad

---

## 🧪 Testing

### Probar Backtracking

```bash
# Test 1: Ciclos en wallet conocida
curl "http://localhost:8080/api/forensic/backtrack/suspicious-chains/4?wallet=testWallet1"

# Test 2: Búsqueda global
curl "http://localhost:8080/api/forensic/backtrack/suspicious-chains/5"
```

### Probar Branch & Bound

```bash
# Test 1: Camino simple
curl "http://localhost:8080/api/path/branch-bound/walletA/walletB/100.0"

# Test 2: Sin camino disponible (costo muy bajo)
curl "http://localhost:8080/api/path/branch-bound/walletA/walletB/1.0"

# Test 3: Análisis multi-escenario
curl "http://localhost:8080/api/path/branch-bound/analyze/walletA/walletB"
```

---

## 📈 Métricas de Desempeño

### Backtracking

```json
{
  "pathsExplored": 237,
  "backtrackCount": 189,
  "cyclesDetected": 5,
  "executionTimeMs": 45
}
```

### Branch & Bound

```json
{
  "nodesExplored": 47,
  "branchesPruned": 152,
  "pruningRatio": 0.764,
  "executionTimeMs": 23
}
```

---

## 🎓 Consideraciones Académicas

### Ventajas del Backtracking

1. ✅ **Completitud:** Encuentra TODAS las soluciones
2. ✅ **Flexibilidad:** Fácil agregar nuevas restricciones
3. ✅ **Patrones complejos:** Detecta lo que otros algoritmos no ven

### Desventajas

1. ⚠️ **Complejidad exponencial** → Solo para profundidades pequeñas
2. ⚠️ **Memoria:** Stack overflow si profundidad muy alta

### Ventajas del Branch & Bound

1. ✅ **Optimalidad:** Garantiza mejor solución
2. ✅ **Eficiencia:** Poda reduce exploración dramáticamente
3. ✅ **Restricciones:** Maneja múltiples criterios

### Desventajas

1. ⚠️ **Complejidad de implementación:** Más difícil que Greedy/DP
2. ⚠️ **Heurística:** Requiere buena función de estimación

---

## 🚀 Próximos Pasos

1. **Optimizaciones:**
   - Implementar memoización en Backtracking
   - Mejorar heurística en Branch & Bound
   - Paralelizar exploración de ramas

2. **Nuevas funcionalidades:**
   - Detección de patrones más complejos
   - Visualización de árboles de exploración
   - Comparación automática de algoritmos

3. **Integración:**
   - Dashboard web para visualizar resultados
   - Alertas automáticas de patrones críticos
   - Exportación de reportes

---

## 📚 Referencias

- **Backtracking:** Cormen, T. H., et al. (2009). *Introduction to Algorithms*
- **Branch & Bound:** Lawler, E. L., & Wood, D. E. (1966). *Branch-and-Bound Methods*
- **Graph Algorithms:** Sedgewick, R., & Wayne, K. (2011). *Algorithms*

---

## 👥 Contribuciones

Si tienes ideas para mejorar estos algoritmos o encontraste un bug:

1. Crea un issue en GitHub
2. Propone una pull request
3. Contacta al equipo de desarrollo

---

**¡Feliz análisis forense! 🔍🚀**

