# 🚀 QUICK START: Testing

## ⚡ Ejecutar Tests en 3 Pasos

### 1. Script Automatizado (Recomendado)

```bash
# Windows
RUN_TESTS.bat

# El script hará TODO automáticamente:
# ✅ Verificar Java/Maven
# ✅ Compilar proyecto
# ✅ Ejecutar 58 tests
# ✅ Generar reporte de cobertura
# ✅ Abrir reporte en navegador
```

---

### 2. Maven Manual

```bash
cd demo

# Todos los tests (58 total)
mvn clean test

# Ver reporte de cobertura
mvn jacoco:report
start target\site\jacoco\index.html
```

---

### 3. Tests Específicos

```bash
# Solo Backtracking (12 tests)
mvn test -Dtest=BacktrackingAlgorithmTest

# Solo Branch & Bound (14 tests)
mvn test -Dtest=BranchAndBoundAlgorithmTest

# Solo servicios (12 tests)
mvn test -Dtest=*ServiceTest

# Solo integración (20 tests)
mvn test -Dtest=*IntegrationTest
```

---

## 📊 Resumen Rápido

| Suite | Tests | Tiempo |
|-------|-------|--------|
| BacktrackingAlgorithmTest | 12 | ~1s |
| BranchAndBoundAlgorithmTest | 14 | ~0.5s |
| BacktrackingServiceTest | 5 | ~0.3s |
| BranchBoundServiceTest | 7 | ~0.3s |
| AlgorithmEndpointsIntegrationTest | 20 | ~3s |
| **TOTAL** | **58** | **~5s** |

---

## ✅ Verificación Rápida

```bash
# 1. Verificar Java
java -version
# Debe ser Java 17+

# 2. Verificar Maven
mvn -version
# Debe ser Maven 3.8+

# 3. Compilar
cd demo
mvn clean compile

# 4. Ejecutar tests
mvn test

# 5. Ver resultados
# Buscar: "Tests run: 58, Failures: 0, Errors: 0"
```

---

## 🎯 Tests Importantes

### Test que NO puede fallar:
```bash
mvn test -Dtest=BacktrackingAlgorithmTest#testDetectCycle
```
Este test verifica que Backtracking detecta ciclos correctamente.

### Test de optimización:
```bash
mvn test -Dtest=BranchAndBoundAlgorithmTest#testFindOptimalPath
```
Este test verifica que Branch & Bound encuentra el camino óptimo.

---

## 🔧 Troubleshooting Rápido

### Tests fallan por Neo4j
**Solución:** Tests unitarios NO requieren Neo4j (usan mocks).
```bash
# Solo tests unitarios (no requieren Neo4j)
mvn test -Dtest=*AlgorithmTest

# Tests de integración (SÍ requieren Neo4j)
docker-compose up -d neo4j
mvn test -Dtest=*IntegrationTest
```

### Compilación falla
```bash
# Limpiar y recompilar
mvn clean compile -DskipTests
```

### Tests muy lentos
```bash
# Ejecutar en paralelo
mvn test -T 4
```

---

## 📚 Documentación Completa

- **TESTING_GUIDE.md** → Guía detallada completa
- **TESTING_SUMMARY.md** → Resumen de todos los tests
- **RUN_TESTS.bat** → Script automatizado

---

## 🎉 ¡Eso es todo!

```bash
# Un solo comando para todo:
RUN_TESTS.bat
```


