# 📊 Datos de Prueba para Neo4j

Este archivo contiene instrucciones para cargar datos de prueba en la base de datos Neo4j.

## 🎯 Contenido de los Datos

Los datos de prueba incluyen:

- **8 Wallets** de Bitcoin con diferentes características
- **10 Transacciones** que conectan las wallets
- **Relaciones INPUT/OUTPUT** que forman un grafo de transacciones
- **Patrones interesantes**:
  - Peel chains (cadenas de pelado)
  - Ciclos de transacciones
  - Patrones de mixing
  - Diferentes niveles de riesgo

## 🚀 Cómo Cargar los Datos

### Opción 1: Script Automático (Recomendado)

```bash
./LOAD_TEST_DATA.sh
```

El script detectará automáticamente si tienes `cypher-shell` o Docker y cargará los datos.

### Opción 2: Docker Compose

Si estás usando Docker Compose:

```bash
# Asegúrate de que Neo4j esté corriendo
docker-compose up -d

# Cargar los datos
./LOAD_TEST_DATA.sh
```

### Opción 3: Neo4j Browser (Manual)

1. Abre Neo4j Browser en: http://localhost:7474
2. Conéctate con:
   - Usuario: `neo4j`
   - Contraseña: `password` (o la que hayas configurado)
3. Abre el archivo `demo/src/main/resources/test-data.cypher`
4. Copia y pega el contenido en el editor de consultas
5. Ejecuta la consulta

### Opción 4: cypher-shell

Si tienes `cypher-shell` instalado:

```bash
cypher-shell -u neo4j -p password -f demo/src/main/resources/test-data.cypher
```

## 📋 Verificar los Datos Cargados

### Ver estadísticas

```cypher
// Contar wallets
MATCH (w:Wallet) RETURN count(w) as totalWallets;

// Contar transacciones
MATCH (t:Transaction) RETURN count(t) as totalTransactions;

// Contar relaciones
MATCH ()-[r:INPUT]->() RETURN count(r) as totalInputs;
MATCH ()-[r:OUTPUT]->() RETURN count(r) as totalOutputs;
```

### Ver el grafo completo

```cypher
MATCH (w:Wallet)-[r]-(t:Transaction) 
RETURN w, r, t 
LIMIT 100;
```

### Consultas de ejemplo

```cypher
// Ver wallets con alto riesgo
MATCH (w:Wallet) 
WHERE w.risk_score > 0.7 
RETURN w.address, w.balance, w.risk_score 
ORDER BY w.risk_score DESC;

// Ver cadenas de transacciones
MATCH path = (w1:Wallet)-[:INPUT]->(:Transaction)-[:OUTPUT]->(w2:Wallet)
RETURN path LIMIT 10;

// Encontrar ciclos
MATCH cycle = (w:Wallet)-[*2..4]-(w)
RETURN cycle LIMIT 5;
```

## 🧪 Probar los Algoritmos

Una vez cargados los datos, puedes probar los endpoints de la API:

### Backtracking - Detectar cadenas sospechosas

```bash
curl -X POST http://localhost:8080/api/forensic/backtracking/suspicious-chains \
  -H "Content-Type: application/json" \
  -d '{
    "startWallet": "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    "maxDepth": 5
  }'
```

### Branch & Bound - Encontrar ruta óptima

```bash
curl -X POST http://localhost:8080/api/forensic/branch-bound/optimal-path \
  -H "Content-Type: application/json" \
  -d '{
    "sourceWallet": "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    "targetWallet": "3J98t1WpEZ73CNmYviecrnyiWrnqRhWNLy",
    "maxCost": 100.0
  }'
```

### Greedy - Detectar peel chains

```bash
curl -X GET "http://localhost:8080/api/algorithms/greedy/peel-chains?threshold=0.7&minChainLength=3&limit=10"
```

### Graph Algorithms - Análisis de centralidad

```bash
curl -X GET "http://localhost:8080/api/network/betweenness-centrality?topN=5"
```

### Pattern Matching - Detectar patrones de mixing

```bash
curl -X GET "http://localhost:8080/api/network/mixing-patterns?depth=3"
```

## 🗑️ Limpiar los Datos

Para eliminar todos los datos de prueba:

```cypher
MATCH (n) DETACH DELETE n;
```

**⚠️ ADVERTENCIA**: Esto eliminará TODOS los datos de la base de datos, no solo los de prueba.

## 📝 Estructura de los Datos

### Wallets

Cada wallet tiene:
- `address`: Dirección Bitcoin
- `balance`: Balance actual
- `totalReceived`: Total recibido
- `totalSent`: Total enviado
- `txCount`: Número de transacciones
- `firstSeen`: Primera vez vista
- `lastSeen`: Última vez vista
- `risk_score`: Puntuación de riesgo (0-1)

### Transacciones

Cada transacción tiene:
- `hash`: Hash de la transacción
- `confirmed`: Fecha de confirmación
- `fee`: Tarifa de transacción
- `size`: Tamaño en bytes
- `blockHeight`: Altura del bloque
- `confirmations`: Número de confirmaciones

### Relaciones

- **INPUT**: Wallet → Transaction
  - `amount`: Cantidad enviada
  - `txHash`: Hash de la transacción
  - `timestamp`: Momento de la transacción
  - `index`: Índice del input

- **OUTPUT**: Transaction → Wallet
  - `amount`: Cantidad recibida
  - `txHash`: Hash de la transacción
  - `timestamp`: Momento de la transacción
  - `index`: Índice del output

## 🎯 Patrones de Prueba Incluidos

1. **Peel Chain**: Wallet 3 (3J98t1WpEZ73CNmYviecrnyiWrnqRhWNLy)
   - Realiza múltiples transacciones consecutivas
   - Alto risk_score (0.8)

2. **Ciclo**: Wallet 1 → Wallet 4 → Wallet 1
   - Demuestra flujo circular de fondos

3. **Mixing**: Wallet 7 distribuye fondos a múltiples wallets
   - Alto volumen de transacciones
   - Muy alto risk_score (0.9)

4. **Transacciones Normales**: Wallets 5, 6, 8
   - Bajo a medio risk_score
   - Patrones de transacción regulares

## 🔧 Troubleshooting

### Error: "Connection refused"
- Asegúrate de que Neo4j esté corriendo
- Verifica el puerto (default: 7687)

### Error: "Authentication failed"
- Verifica usuario y contraseña
- Por defecto: neo4j/password

### No se ven los datos
- Ejecuta las consultas de verificación
- Revisa los logs de Neo4j

## 📚 Recursos Adicionales

- [Neo4j Browser Guide](https://neo4j.com/developer/neo4j-browser/)
- [Cypher Query Language](https://neo4j.com/developer/cypher/)
- [Neo4j Docker](https://hub.docker.com/_/neo4j)

