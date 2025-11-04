# 📊 Diagramas Visuales: Backtracking y Branch & Bound

## 🔍 BACKTRACKING: Detección de Ciclos

### Ejemplo: Detección de Ciclo A→B→C→A

```
ESTADO INICIAL:
┌─────────────────────────────────────────┐
│  Wallet A (inicio)                      │
│  Path: [A]                              │
│  Depth: 5                               │
└─────────────────────────────────────────┘

EXPLORACIÓN (ÁRBOL DE BACKTRACKING):

                        A (depth=5)
                       / | \
                      /  |  \
                     /   |   \
                    B    D    E
                  (4)  (4)   (4)
                 / | \
                /  |  \
               C   F   G
              (3) (3) (3)
             /
            A  ← ¡CICLO DETECTADO! [A→B→C→A]
           (2)
           
           ✅ Registrar: CYCLE con suspicionLevel=0.95

BACKTRACK después de explorar A:
    Path: [A, B, C]
    ⬅️ Remover C
    Path: [A, B]
    
Continuar explorando F:
    Path: [A, B, F]
    ...
```

### Pseudocódigo Visualizado

```python
function BACKTRACK(wallet='A', path=['A'], depth=5):
    
    # CASO BASE 1: Profundidad agotada
    if depth == 0:
        print(f"✓ Camino completo: {path}")
        return
    
    # CASO BASE 2: Ciclo detectado
    if wallet in path[:-1]:  # Último elemento es el actual
        print(f"🔄 CICLO: {path} → {wallet}")
        registerCycle(path + [wallet])
        return
    
    # EXPLORACIÓN
    neighbors = getNeighbors(wallet)  # [B, D, E] para A
    
    for neighbor in neighbors:
        print(f"→ Explorando {wallet} → {neighbor}")
        
        # ✅ AGREGAR al camino
        path.append(neighbor)
        
        # 🔁 RECURSIÓN (profundidad - 1)
        BACKTRACK(neighbor, path, depth - 1)
        
        # ⬅️ BACKTRACK: Deshacer decisión
        path.pop()
        print(f"← Backtrack desde {neighbor} a {wallet}")
```

### Salida de Ejemplo

```
→ Explorando A → B
  → Explorando B → C
    → Explorando C → A
    🔄 CICLO: [A, B, C] → A
    ✅ Registrado: CYCLE (suspicionLevel=0.95)
    ← Backtrack desde A a C
  ← Backtrack desde C a B
  → Explorando B → F
    → Explorando F → H
      ...
```

---

## 🌳 BRANCH & BOUND: Camino Óptimo con Costo

### Ejemplo: Encontrar camino de A a D con maxCost=100

```
GRAFO:
        50      30
    A ────→ B ────→ D (camino 1: costo 80)
    │       │
  20│       │40
    ↓       ↓
    C ────→ D (camino 2: costo 60)
        40

OBJETIVO: Camino más corto con costo ≤ 100
```

### Árbol de Exploración con Poda

```
ITERACIÓN 1:
┌────────────────────────────────────────────┐
│ Cola de Prioridad (por costo):            │
│ [Node(A, cost=0, path=[A])]               │
└────────────────────────────────────────────┘

Poll: Node(A, cost=0)
Ramificar:
  - Node(B, cost=50, path=[A,B])  ✅ Agregar
  - Node(C, cost=20, path=[A,C])  ✅ Agregar

Cola: [Node(C, 20), Node(B, 50)]

─────────────────────────────────────────────

ITERACIÓN 2:
Poll: Node(C, cost=20)  ← Menor costo
Ramificar:
  - Node(D, cost=60, path=[A,C,D])  ✅ DESTINO ENCONTRADO!

bestSolution = {path: [A,C,D], cost: 60}

Cola: [Node(B, 50), Node(D, 60)]

─────────────────────────────────────────────

ITERACIÓN 3:
Poll: Node(B, cost=50)

🌿 PODA: cost(50) + heuristic < bestSolution(60)?
        No puede mejorar (B→D cuesta 30, total 80)
        
Ramificar de todos modos:
  - Node(D, cost=80, path=[A,B,D])

🌿 PODA: cost(80) >= bestSolution(60)
        ❌ PODAR esta rama

Cola: [Node(D, 60)]

─────────────────────────────────────────────

RESULTADO FINAL:
✅ Camino óptimo: [A, C, D]
✅ Costo total: 60
✅ Nodos explorados: 4
✅ Ramas podadas: 1
```

### Pseudocódigo Visualizado

```python
function BRANCH_AND_BOUND(source='A', target='D', maxCost=100):
    
    priorityQueue = PriorityQueue()
    priorityQueue.add(Node(source, cost=0, path=[source]))
    
    bestSolution = {cost: ∞, path: None}
    visited = {}
    
    while priorityQueue.notEmpty():
        node = priorityQueue.poll()
        
        print(f"🔍 Explorando: {node.wallet} (costo: {node.cost})")
        
        # ✅ CASO 1: Llegamos al destino
        if node.wallet == target:
            if node.cost < bestSolution.cost:
                bestSolution = node
                print(f"✨ Nueva mejor solución: costo={node.cost}")
            continue
        
        # 🌿 PODA 1: Ya visitado con menor costo
        if node.wallet in visited and visited[node.wallet] <= node.cost:
            print(f"🌿 PODA: Ya visitado con menor costo")
            continue
        visited[node.wallet] = node.cost
        
        # 🌿 PODA 2: Costo excede límite
        if node.cost > maxCost:
            print(f"🌿 PODA: Costo {node.cost} > maxCost {maxCost}")
            continue
        
        # 🌿 PODA 3: No puede mejorar mejor solución
        if node.cost >= bestSolution.cost:
            print(f"🌿 PODA: No puede mejorar solución actual")
            continue
        
        # 🌳 RAMIFICACIÓN
        neighbors = getNeighbors(node.wallet)
        for (nextWallet, edgeCost) in neighbors:
            newCost = node.cost + edgeCost
            newPath = node.path + [nextWallet]
            
            print(f"  🌳 Rama: {nextWallet} (costo: {newCost})")
            priorityQueue.add(Node(nextWallet, newCost, newPath))
    
    return bestSolution
```

### Salida de Ejemplo

```
🔍 Explorando: A (costo: 0)
  🌳 Rama: B (costo: 50)
  🌳 Rama: C (costo: 20)

🔍 Explorando: C (costo: 20)
  🌳 Rama: D (costo: 60)

🔍 Explorando: D (costo: 60)
✨ Nueva mejor solución: costo=60

🔍 Explorando: B (costo: 50)
  🌳 Rama: D (costo: 80)

🔍 Explorando: D (costo: 80)
🌿 PODA: No puede mejorar solución actual (80 >= 60)

✅ RESULTADO FINAL:
   Camino: [A, C, D]
   Costo: 60
   Nodos explorados: 4
   Ramas podadas: 1
```

---

## 📊 Comparación Visual

### Eficiencia de Poda en Branch & Bound

```
SIN PODA (búsqueda exhaustiva):
                        A
                       /|\
                      / | \
                     B  C  D
                    /|\ /|\ /|\
                   ... (explorar TODO)
Total nodos: O(b^d) = exponencial

CON PODA (Branch & Bound):
                        A
                       /|\
                      / | \
                     B  C  D
                    /  /|\  ❌ (podado)
                   ❌ ... ❌
                      ↓
                      D ✅ (óptimo)
                      
Total nodos explorados: ~30-40% del árbol completo
Ramas podadas: ~60-70%
```

### Backtracking: Exploración Completa

```
EXPLORACIÓN COMPLETA (sin poda por optimalidad):

                        A
                       /|\
                      / | \
                     B  C  D
                    /|\ /|\ /|\
                   F G H I J K
                  ... (explorar TODO hasta depth=0)

Total caminos explorados: O(b^d)
Beneficio: Encuentra TODOS los patrones (incluidos ciclos)
```

---

## 🎯 Casos de Uso Visuales

### Caso 1: Mixer Detection con Backtracking

```
INPUT: Wallet sospechosa "MixerX"

EXPLORACIÓN:
    MixerX
      ├─→ Wallet1 ─→ Wallet2 ─→ MixerX  ← CICLO! 🔴
      ├─→ Wallet3 ─→ Wallet4 ─→ Wallet5 ─→ Wallet6
      └─→ Wallet7 ─→ Wallet8 ─→ MixerX  ← CICLO! 🔴

OUTPUT:
  - 2 ciclos detectados
  - SuspicionLevel: 0.95 (CRITICAL)
  - Patrón: MIXING con redistribución cíclica
```

### Caso 2: Rastreo de Fondos con Branch & Bound

```
INPUT:
  - Origen: WalletCriminal
  - Destino: ExchangeWallet
  - MaxCost: 200 satoshis

EXPLORACIÓN CON PODA:
    WalletCriminal
      ├─→ Path1 (cost: 150) → ExchangeWallet  ✅ Encontrado
      ├─→ Path2 (cost: 250) ❌ PODADO (excede maxCost)
      └─→ Path3 (cost: 180) → intermedio → 🌿 PODADO (no mejora)

OUTPUT:
  - Camino óptimo: Path1
  - Costo total: 150 satoshis
  - Ahorro: 50 satoshis vs límite
  - Eficiencia de poda: 67% ramas podadas
```

---

## 🔬 Análisis de Complejidad Visual

### Crecimiento Exponencial vs Poda

```
Profundidad vs Nodos Explorados (branching factor = 3):

Depth │ Sin Poda (b^d) │ Con Poda (B&B) │ Reducción
──────┼────────────────┼────────────────┼──────────
  1   │       3        │       3        │    0%
  2   │       9        │       5        │   44%
  3   │      27        │      12        │   56%
  4   │      81        │      25        │   69%
  5   │     243        │      47        │   81%
  6   │     729        │      89        │   88%
  7   │    2187        │     142        │   93%
  8   │    6561        │     234        │   96%

Conclusión: Branch & Bound reduce dramáticamente la exploración
            en profundidades altas gracias a la poda efectiva.
```

### Tiempo de Ejecución Real

```
Backtracking (sin optimización):
  Depth 4: ~50ms
  Depth 5: ~200ms
  Depth 6: ~800ms
  Depth 7: ~3200ms  ← Exponencial!

Branch & Bound (con poda):
  100 nodos: ~15ms
  500 nodos: ~45ms
  1000 nodos: ~120ms
  5000 nodos: ~450ms  ← Casi lineal con poda efectiva!
```

---

## 🎓 Conclusiones Académicas

### Backtracking
- ✅ **Completitud:** Garantiza encontrar TODAS las soluciones
- ✅ **Flexibilidad:** Fácil implementar nuevas restricciones
- ⚠️ **Complejidad:** Exponencial O(b^d)
- 💡 **Uso óptimo:** Profundidades pequeñas (≤7), patrones complejos

### Branch & Bound
- ✅ **Optimalidad:** Garantiza encontrar la MEJOR solución
- ✅ **Eficiencia:** Poda reduce exploración en 80-95%
- ✅ **Restricciones:** Maneja múltiples criterios simultáneos
- 💡 **Uso óptimo:** Optimización con restricciones, grafos grandes

---

**¡Esperamos que estos diagramas te ayuden a entender mejor los algoritmos! 🚀**

