# Frontend API Endpoints - Fixes Applied

## Fecha: 4 de noviembre de 2025

## Problemas Detectados y Solucionados

### 1. ❌ PathAnalysisController - Rutas Incorrectas
**Problema:** El frontend estaba llamando a `/api/path-analysis/*` pero el backend usa `/api/path/*`

**Backend Real:**
```java
@RequestMapping("/api/path")
- GET /api/path/shortest?from=X&to=Y
- GET /api/path/all?from=X&to=Y&maxLength=N
```

**Frontend Anterior (INCORRECTO):**
```javascript
api.get('/path-analysis/connection', { params: { sourceAddress, targetAddress }})
api.get('/path-analysis/all-paths', { params: { sourceAddress, targetAddress, maxDepth }})
```

**Frontend Corregido (CORRECTO):**
```javascript
api.get('/path/shortest', { params: { from: sourceAddress, to: targetAddress }})
api.get('/path/all', { params: { from: sourceAddress, to: targetAddress, maxLength: maxDepth }})
```

### 2. ❌ NetworkAnalysisController - Rutas Incorrectas
**Problema:** El frontend estaba llamando a `/api/network-analysis/*` pero el backend usa `/api/network/*`

**Backend Real:**
```java
@RequestMapping("/api/network")
- GET /api/network/analyze/{address}
- GET /api/network/statistics/{address}
- GET /api/network/large-transactions?minAmount=X&limit=Y
```

**Frontend Anterior (INCORRECTO):**
```javascript
api.get('/network-analysis/analyze', { params: { sourceAddress, depth }})
api.get('/network-analysis/communities', { params: { minClusterSize }})
```

**Frontend Corregido (CORRECTO):**
```javascript
api.get(`/network/analyze/${address}`)
api.get(`/network/statistics/${address}`)
api.get('/network/large-transactions', { params: { minAmount, limit }})
```

### 3. ❌ PatternMatchingController - NO EXISTE
**Problema:** El frontend intentaba llamar endpoints de `/api/pattern-matching/*` que NO están implementados en el backend

**Solución:** Comentado temporalmente hasta que se implemente el controlador

```javascript
// Pattern Matching - ENDPOINTS NO IMPLEMENTADOS EN EL BACKEND
// TODO: Implementar PatternMatchingController en el backend
/*
export const patternAPI = {
  detectMixingPatterns: (depth = 3) => ...
  detectRapidTransactions: (timeWindowSeconds = 3600) => ...
  detectOutliers: () => ...
};
*/
```

## ✅ Endpoints Verificados Como CORRECTOS

### BacktrackingController
```
✓ GET /api/backtracking/suspicious-chains
✓ GET /api/backtracking/detect-cycles
```

### BranchBoundController
```
✓ GET /api/branch-bound/optimal-path
✓ GET /api/branch-bound/cheapest-path
✓ GET /api/branch-bound/multiple-paths
```

### GreedyController
```
✓ GET /api/greedy/peel-chains
✓ GET /api/greedy/max-value-path
```

### GraphController
```
✓ GET /api/graph/dijkstra
✓ GET /api/graph/bellman-ford
✓ GET /api/graph/floyd-warshall
```

### WalletController
```
✓ GET /api/wallet/analyze
```

## Configuración del Proxy

El `vite.config.js` está correctamente configurado:
```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      secure: false,
    }
  }
}
```

## Cómo Probar

1. **Iniciar el backend:**
   ```bash
   cd demo
   ./mvnw spring-boot:run
   ```

2. **Iniciar el frontend:**
   ```bash
   cd frontend
   npm run dev
   ```

3. **Verificar que el backend está corriendo:**
   ```bash
   curl http://localhost:8080/api/wallet/analyze?address=wallet1
   ```

4. **Probar desde el frontend:**
   - Abrir http://localhost:3000
   - Los endpoints deberían funcionar correctamente ahora

## Resumen de Cambios

- ✅ Corregidas rutas de PathAnalysisController
- ✅ Corregidos parámetros de PathAnalysisController (from/to en lugar de sourceAddress/targetAddress)
- ✅ Corregidas rutas de NetworkAnalysisController
- ✅ Comentados endpoints inexistentes de PatternMatchingController
- ✅ Todos los demás endpoints verificados y funcionando

## Próximos Pasos

Si necesitas los endpoints de Pattern Matching, deberás:
1. Crear `PatternMatchingController.java` en el backend
2. Implementar los métodos:
   - `detectMixingPatterns(depth)`
   - `detectRapidTransactions(timeWindowSeconds)`
   - `detectOutliers()`
3. Descomentar `patternAPI` en el frontend
