# 🧪 Guía de Testing Completa

## 📋 Índice
1. [Estructura de Tests](#estructura-de-tests)
2. [Tests Unitarios](#tests-unitarios)
3. [Tests de Integración](#tests-de-integración)
4. [Cómo Ejecutar los Tests](#cómo-ejecutar-los-tests)
5. [Cobertura Esperada](#cobertura-esperada)
6. [Troubleshooting](#troubleshooting)

---

## 📁 Estructura de Tests

```
demo/src/test/java/com/example/
├── algorithm/
│   ├── BacktrackingAlgorithmTest.java          ✅ 12 tests
│   └── BranchAndBoundAlgorithmTest.java        ✅ 14 tests
│
├── service/
│   ├── BacktrackingServiceTest.java            ✅ 5 tests
│   └── BranchBoundServiceTest.java             ✅ 7 tests
│
└── integration/
    └── AlgorithmEndpointsIntegrationTest.java  ✅ 20 tests

TOTAL: 58 tests ✅
```

---

## 🔬 Tests Unitarios

### BacktrackingAlgorithmTest.java (12 tests)

Prueba el algoritmo puro de Backtracking sin dependencias externas.

| Test | Descripción |
|------|-------------|
| `testDetectCycle` | Detecta ciclo A→B→C→A |
| `testDepthLimit` | Respeta profundidad máxima |
| `testPeelChainDetection` | Detecta peel chains largos |
| `testEmptyGraph` | Maneja grafo vacío |
| `testIsolatedNode` | Maneja nodo aislado |
| `testSuspicionLevelCalculation` | Calcula nivel de sospecha [0-1] |
| `testDescriptionGeneration` | Genera descripciones |
| `testResultOrdering` | Ordena por nivel de sospecha |
| `testMultiplePatternTypes` | Detecta múltiples tipos |
| `testPerformance` | Completa en <1 segundo |

**Ejecutar:**
```bash
mvn test -Dtest=BacktrackingAlgorithmTest
```

---

### BranchAndBoundAlgorithmTest.java (14 tests)

Prueba el algoritmo puro de Branch & Bound.

| Test | Descripción |
|------|-------------|
| `testFindOptimalPath` | Encuentra camino óptimo A→C→D |
| `testCostConstraint` | Respeta restricción de costo |
| `testBranchPruning` | Poda ramas ineficientes |
| `testSameSourceAndTarget` | Maneja origen=destino |
| `testNoPath` | Maneja camino inexistente |
| `testEmptyGraph` | Maneja grafo vacío |
| `testExplorationMetrics` | Genera métricas correctas |
| `testMultiplePaths` | Elige camino más barato |
| `testPerformance` | Completa en <500ms |
| `testInvalidCosts` | Maneja costos inválidos |
| `testPathDetails` | Retorna detalles completos |
| `testPriorityOrdering` | Usa cola de prioridad |

**Ejecutar:**
```bash
mvn test -Dtest=BranchAndBoundAlgorithmTest
```

---

### BacktrackingServiceTest.java (5 tests)

Prueba la integración del servicio con Neo4j (con mocks).

| Test | Descripción |
|------|-------------|
| `testDetectSuspiciousChains` | Detecta cadenas desde wallet |
| `testEmptyWallet` | Maneja wallet sin transacciones |
| `testDetectAllCycles` | Detecta ciclos globales |
| `testDepthValidation` | Valida profundidad |
| `testNeo4jErrorHandling` | Maneja errores de BD |

**Ejecutar:**
```bash
mvn test -Dtest=BacktrackingServiceTest
```

---

### BranchBoundServiceTest.java (7 tests)

Prueba la integración del servicio con Neo4j (con mocks).

| Test | Descripción |
|------|-------------|
| `testFindOptimalPathWithCostLimit` | Encuentra camino con límite |
| `testNoConnection` | Maneja wallets sin conexión |
| `testMultiplePathsWithDifferentCosts` | Múltiples escenarios |
| `testFindCheapestPath` | Encuentra camino más barato |
| `testInputValidation` | Valida inputs |
| `testNeo4jErrorHandling` | Maneja errores de BD |
| `testExplorationMetrics` | Retorna métricas |

**Ejecutar:**
```bash
mvn test -Dtest=BranchBoundServiceTest
```

---

## 🌐 Tests de Integración

### AlgorithmEndpointsIntegrationTest.java (20 tests)

Prueba todos los endpoints REST de forma integrada.

#### Health Check (1 test)
- `testHealthCheck` - Verifica que el servicio esté UP

#### Backtracking Endpoints (3 tests)
- `testBacktrackingSuspiciousChainsWithWallet` - Con wallet específica
- `testBacktrackingGlobalSearch` - Búsqueda global
- `testBacktrackingInvalidDepth` - Validación de depth

#### Branch & Bound Endpoints (4 tests)
- `testBranchBoundOptimalPath` - Camino válido
- `testBranchBoundInvalidMaxCost` - MaxCost negativo
- `testBranchBoundEmptyWallet` - Wallet vacía
- `testBranchBoundMultiScenario` - Análisis multi-escenario

#### Greedy Endpoints (2 tests)
- `testGreedyPeelChains` - Análisis básico
- `testGreedyInvalidThreshold` - Threshold inválido

#### Dynamic Programming Endpoints (2 tests)
- `testDynamicProgrammingMaxFlow` - Camino válido
- `testDynamicProgrammingMissingSource` - Sin sourceWallet

#### Graph Algorithms Endpoints (2 tests)
- `testGraphCentrality` - Análisis de centralidad
- `testGraphCommunities` - Detección de comunidades

#### Pattern Matching Endpoints (1 test)
- `testPatternDetection` - Detección de patrones

#### Validaciones Generales (5 tests)
- `testAllEndpointsReturnJson` - Todos retornan JSON
- `testAllEndpointsIncludeTimestamp` - Todos incluyen timestamp

**Ejecutar:**
```bash
mvn test -Dtest=AlgorithmEndpointsIntegrationTest
```

---

## 🚀 Cómo Ejecutar los Tests

### Opción 1: Todos los Tests

```bash
cd demo
mvn clean test
```

### Opción 2: Solo Tests Unitarios

```bash
mvn test -Dtest=*AlgorithmTest
```

### Opción 3: Solo Tests de Servicios

```bash
mvn test -Dtest=*ServiceTest
```

### Opción 4: Solo Tests de Integración

```bash
mvn test -Dtest=*IntegrationTest
```

### Opción 5: Test Específico

```bash
mvn test -Dtest=BacktrackingAlgorithmTest#testDetectCycle
```

### Opción 6: Con Reporte de Cobertura

```bash
mvn clean test jacoco:report
# Abrir: target/site/jacoco/index.html
```

---

## 📊 Cobertura Esperada

### Algoritmos (algorithm/)
- **BacktrackingAlgorithm.java**: ~85% de cobertura
- **BranchAndBoundAlgorithm.java**: ~90% de cobertura

### Servicios (service/)
- **BacktrackingService.java**: ~70% de cobertura
- **BranchBoundService.java**: ~75% de cobertura

### Controladores (controller/)
- **AlgorithmController.java**: ~60% de cobertura (endpoints)

**Cobertura Global Esperada: ~75%**

---

## 🎯 Configuración de Maven (pom.xml)

Asegúrate de tener estas dependencias en tu `pom.xml`:

```xml
<dependencies>
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito JUnit Jupiter -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <!-- JaCoCo para cobertura -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.10</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

---

## 📝 Ejemplos de Salida

### Test Exitoso ✅

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.example.algorithm.BacktrackingAlgorithmTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.234 s
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Test con Fallo ❌

```
[ERROR] testDetectCycle  Time elapsed: 0.123 s  <<< FAILURE!
java.lang.AssertionError: Debería detectar al menos un ciclo
    at BacktrackingAlgorithmTest.testDetectCycle(BacktrackingAlgorithmTest.java:89)
```

---

## 🔧 Troubleshooting

### Problema 1: Tests fallan por Neo4j

**Síntoma:**
```
Connection refused: localhost:7687
```

**Solución:**
Los tests unitarios NO requieren Neo4j (usan mocks). Solo los tests de integración requieren Neo4j.

Para tests de integración:
```bash
docker-compose up -d neo4j
mvn test -Dtest=*IntegrationTest
```

---

### Problema 2: No encuentra clases de test

**Síntoma:**
```
No tests found matching pattern
```

**Solución:**
Asegúrate de que los archivos terminen en `Test.java`:
```bash
# Correcto
BacktrackingAlgorithmTest.java

# Incorrecto
TestBacktrackingAlgorithm.java
```

---

### Problema 3: Tests muy lentos

**Síntoma:**
Tests tardan más de 30 segundos.

**Solución:**
```bash
# Ejecutar en paralelo
mvn test -T 4

# Skip tests de integración (más lentos)
mvn test -DexcludedGroups=integration
```

---

### Problema 4: Mockito no funciona

**Síntoma:**
```
org.mockito.exceptions.misusing.WrongTypeOfReturnValue
```

**Solución:**
Verifica que los tipos de retorno del mock coincidan:
```java
// ✅ Correcto
when(repository.findWallet()).thenReturn(Collections.emptyList());

// ❌ Incorrecto
when(repository.findWallet()).thenReturn(null);
```

---

## 📈 CI/CD Integration

### GitHub Actions

Crea `.github/workflows/tests.yml`:

```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Run tests
      run: cd demo && mvn clean test
    
    - name: Generate coverage report
      run: cd demo && mvn jacoco:report
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        files: ./demo/target/site/jacoco/jacoco.xml
```

---

## 🎯 Checklist de Testing

Antes de hacer commit:

- [ ] Todos los tests pasan (`mvn test`)
- [ ] Cobertura >70% (`mvn jacoco:report`)
- [ ] No hay warnings de compilación
- [ ] Tests de integración funcionan con Neo4j
- [ ] Documentación actualizada

---

## 📚 Referencias

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

---

## 🎓 Contribuir con Más Tests

Si quieres agregar más tests:

1. **Identifica gaps de cobertura:**
   ```bash
   mvn jacoco:report
   # Abrir target/site/jacoco/index.html
   ```

2. **Crea test siguiendo la convención:**
   ```java
   @Test
   @DisplayName("Descripción clara del test")
   void testNombreDescriptivo() {
       // Given (preparar)
       // When (ejecutar)
       // Then (verificar)
   }
   ```

3. **Ejecuta y verifica:**
   ```bash
   mvn test -Dtest=TuNuevoTest
   ```

---

## ✅ Resumen de Comandos Útiles

```bash
# Ejecutar todos los tests
mvn clean test

# Solo tests de Backtracking
mvn test -Dtest=*Backtracking*

# Solo tests de Branch & Bound
mvn test -Dtest=*BranchBound*

# Con cobertura
mvn clean test jacoco:report

# Modo verbose
mvn test -X

# Skip tests (para compilar rápido)
mvn clean install -DskipTests

# Tests en paralelo (más rápido)
mvn test -T 4
```

---

**¡Happy Testing! 🧪✅**

