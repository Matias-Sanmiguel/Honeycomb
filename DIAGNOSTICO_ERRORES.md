# Diagnóstico de Errores - Frontend/Backend
## 🔴 Problemas Críticos Encontrados
### 1. DESALINEACIÓN DE ENDPOINTS
#### Greedy Algorithm
- **Frontend llama**: `GET /api/greedy/peel-chains`
- **Backend tiene**: `POST /api/algorithms/greedy/peel-chains`
- **Estado**: ❌ BROKEN - Rutas y métodos incorrectos
#### Branch & Bound
- **Frontend llama**: `GET /api/branch-bound/optimal-path`
- **Backend tiene**: `GET /api/branch-bound/optimal-path` ✓
- **Estado**: ✅ OK (pero revisar parámetros)
#### Backtracking
- **Frontend llama**: `GET /api/backtracking/suspicious-chains`
- **Backend tiene**: `GET /api/backtracking/suspicious-chains` ✓
- **Estado**: ✅ Parcialmente OK
### 2. INCONSISTENCIAS EN MÉTODOS HTTP
| Algoritmo | Frontend | Backend | Estado |
|-----------|----------|---------|--------|
| Greedy | GET | POST | ❌ |
| Branch & Bound | GET | GET | ✅ |
| Backtracking | GET | GET | ✅ |
### 3. PROBLEMAS EN TESTS
- `Neo4jClient` es null en BranchBoundServiceTest
- Tests fallan con NullPointerException
- Mock de Neo4jClient no está configurado correctamente
### 4. RUTAS DE SERVICIO vs CONTROLADOR
```
Controladores:
- /api/greedy (GreedyController)
- /api/branch-bound (BranchBoundController)
- /api/backtracking (BacktrackingController)
- /api/algorithms (AlgorithmController)
- /api/graph (GraphController)
Frontend espera:
- /api/greedy/*
- /api/branch-bound/*
- /api/backtracking/*
- /api/algorithms/*
- /api/network/*
- /api/path-analysis/*
```
## 🔧 Correcciones Necesarias
1. Alineación de endpoints frontend ↔ backend
2. Cambiar métodos HTTP donde sea necesario
3. Arreglar configuración de Neo4jClient en tests
4. Validar parámetros de entrada/salida
5. Documentar API correctamente
