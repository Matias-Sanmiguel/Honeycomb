# 🚀 GUÍA RÁPIDA DE INSTALACIÓN Y USO

## 📋 Requisitos Previos

- **Java 17+**: `java --version`
- **Maven 3.8+**: `mvn --version`
- **Docker**: `docker --version`
- **Docker Compose**: `docker-compose --version`
- **curl** (para probar endpoints)

## 🔧 Instalación Rápida (5 minutos)

### Opción 1: Con Docker Compose (RECOMENDADO)

```bash
# 1. Navegar al repositorio
cd /home/cauchothegaucho/Repositorios/Honeycomb

# 2. Iniciar servicios
docker-compose up -d

# 3. Esperar a que Neo4j esté listo (~30 segundos)
docker-compose logs -f neo4j

# 4. Verificar que todo está corriendo
curl http://localhost:8080/api/algorithms/health
```

**Salida esperada**:
```json
{
  "status": "UP",
  "module": "Algorithms",
  "version": "1.0",
  "algorithms": [
    "GREEDY_PEEL_CHAINS",
    "DYNAMIC_PROGRAMMING_MAX_FLOW",
    "BETWEENNESS_CENTRALITY",
    "COMMUNITY_DETECTION",
    "PATTERN_MATCHING"
  ]
}
```

### Opción 2: Ejecución Local (Desarrollo)

```bash
# 1. Navegar al directorio demo
cd demo

# 2. Compilar
mvn clean install

# 3. Ejecutar
mvn spring-boot:run
```

---

## 📡 Primeros Pasos: Probar los Endpoints

### 1️⃣ Cargar datos de prueba

```bash
# Insertar wallet de prueba
curl -X POST http://localhost:8080/api/blockcypher/wallet/1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa?chain=BTC
```

### 2️⃣ Ejecutar Greedy Algorithm

```bash
curl -X POST http://localhost:8080/api/algorithms/greedy/peel-chains \
  -H "Content-Type: application/json" \
  -d '{
    "threshold": 0.95,
    "limit": 10
  }' | jq
```

### 3️⃣ Ejecutar Dynamic Programming

```bash
curl -X POST http://localhost:8080/api/algorithms/dp/max-flow-path \
  -H "Content-Type: application/json" \
  -d '{
    "sourceWallet": "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
    "targetWallet": "wallet_destino",
    "maxHops": 5
  }' | jq
```

### 4️⃣ Ejecutar Graph Algorithms

```bash
# Centralidad
curl http://localhost:8080/api/algorithms/graph/centrality?topN=10 | jq

# Comunidades
curl http://localhost:8080/api/algorithms/graph/communities?minSize=3 | jq
```

### 5️⃣ Ejecutar Pattern Matching

```bash
curl -X POST http://localhost:8080/api/algorithms/pattern/detect-anomalies \
  -H "Content-Type: application/json" \
  -d '{
    "analysisDepth": 3,
    "timeWindowDays": 30,
    "anomalyThreshold": 2.5,
    "patterns": ["MIXING", "CYCLICAL", "RAPID", "ANOMALY"]
  }' | jq
```

---

## 🗂️ Estructura de Directorios Creada

```
Honeycomb/
├── PROMPT_ALGORITMOS_ACADEMICO.md          ✅ Prompt completo con contexto
├── DOCUMENTACION_ALGORITMOS.md              ✅ Documentación exhaustiva
├── docker-compose.yml                       ✅ Orquestación de servicios
├── demo/
│   ├── Dockerfile                           ✅ Imagen Docker
│   ├── pom.xml                              (existente)
│   ├── src/main/java/com/example/
│   │   ├── algorithm/
│   │   │   ├── AlgorithmRequest.java        ✅ DTO request
│   │   │   ├── AlgorithmResponse.java       ✅ DTO response
│   │   │   └── AlgorithmMetrics.java        ✅ Métricas
│   │   ├── dto/
│   │   │   ├── PeelChainGreedyResult.java   ✅ Resultado Greedy
│   │   │   ├── MaxFlowPathResult.java       ✅ Resultado DP
│   │   │   ├── CentralityResult.java        ✅ Resultado Centralidad
│   │   │   ├── CommunityResult.java         ✅ Resultado Comunidades
│   │   │   └── PatternDetectionResult.java  ✅ Resultado Patrones
│   │   ├── service/
│   │   │   ├── GreedyAlgorithmService.java              ✅ Greedy
│   │   │   ├── DynamicProgrammingService.java           ✅ DP
│   │   │   ├── GraphAlgorithmsService.java              ✅ Graph
│   │   │   └── PatternMatchingService.java              ✅ Pattern
│   │   ├── controller/
│   │   │   └── AlgorithmController.java     ✅ REST (5 endpoints)
│   │   └── repository/
│   │       └── AlgorithmRepository.java     ✅ Cypher queries
│   └── src/test/java/com/example/
│       └── service/
│           └── GreedyAlgorithmServiceTest.java ✅ Tests
```

---

## 🎯 Algoritmos Implementados

| # | Algoritmo | Endpoint | Complejidad | Puntos |
|---|-----------|----------|-------------|--------|
| 1 | **Greedy Peel Chains** | `POST /api/algorithms/greedy/peel-chains` | O(n log n) | 1 |
| 2 | **DP Max Flow** | `POST /api/algorithms/dp/max-flow-path` | O(V+E) | 2 |
| 3 | **Betweenness Centrality** | `GET /api/algorithms/graph/centrality` | O(V·E) | 2 |
| 4 | **Community Detection** | `GET /api/algorithms/graph/communities` | O(V log V + E) | 2 |
| 5 | **Pattern Matching** | `POST /api/algorithms/pattern/detect-anomalies` | O(n²) | 2 |
| | | | **TOTAL** | **9** |

---

## 🔍 Detalles de Implementación

### Características Incluidas

✅ **4 algoritmos distintos** con enfoques académicos diferentes  
✅ **DTOs específicos** para cada algoritmo  
✅ **5 endpoints REST** documentados y validados  
✅ **Queries Cypher avanzadas** optimizadas para Neo4j  
✅ **Logging detallado** de ejecución  
✅ **Manejo de excepciones** robusto  
✅ **Validación de inputs** en todos los endpoints  
✅ **Docker & Docker Compose** para fácil despliegue  
✅ **Pruebas unitarias** con JUnit 5 y Mockito  
✅ **Documentación completa** con ejemplos  

### Tecnologías Utilizadas

- **Java 17** - Lenguaje de programación
- **Spring Boot 3.2** - Framework web
- **Spring Data Neo4j** - ORM para grafos
- **Neo4j 5.15** - Base de datos de grafos
- **Maven 3.8** - Build tool
- **Docker** - Containerización
- **JUnit 5** - Testing framework
- **Mockito** - Mock framework

---

## 📊 Verificación de Instalación

### Checklist de Post-Instalación

- [ ] Docker Compose está corriendo: `docker-compose ps`
- [ ] Neo4j es accesible: `curl http://localhost:7474`
- [ ] Aplicación es accesible: `curl http://localhost:8080/api/algorithms/health`
- [ ] Browser Neo4j funciona: `http://localhost:7474` (neo4j/password)
- [ ] Primer endpoint responde: `curl http://localhost:8080/api/algorithms/graph/centrality?topN=5`

### Comandos Útiles

```bash
# Ver logs de aplicación
docker-compose logs -f app

# Ver logs de Neo4j
docker-compose logs -f neo4j

# Parar servicios
docker-compose down

# Parar y limpiar volúmenes
docker-compose down -v

# Reconstruir imagen
docker-compose up --build
```

---

## 🐛 Solución de Problemas

### Problema: Puerto 8080 en uso

```bash
# Cambiar puerto en docker-compose.yml
# ports:
#   - "8081:8080"  # Cambiar a 8081
docker-compose up -d
```

### Problema: Neo4j no inicia

```bash
# Verificar logs
docker-compose logs neo4j

# Limpiar volúmenes y reiniciar
docker-compose down -v
docker-compose up -d
```

### Problema: Queries Cypher fallan

```bash
# Verificar índices en Neo4j Browser
http://localhost:7474

# Ejecutar:
SHOW INDEXES;
```

---

## 📚 Documentación

Archivos incluidos:

1. **PROMPT_ALGORITMOS_ACADEMICO.md** - Prompt completo para el proyecto
2. **DOCUMENTACION_ALGORITMOS.md** - Documentación detallada de algoritmos
3. **Este archivo (README_SETUP.md)** - Guía rápida de instalación

---

## ✅ Checklist de Implementación

- [x] Crear estructura de directorios
- [x] Implementar DTOs para cada algoritmo
- [x] Implementar GreedyAlgorithmService
- [x] Implementar DynamicProgrammingService
- [x] Implementar GraphAlgorithmsService
- [x] Implementar PatternMatchingService
- [x] Crear AlgorithmRepository con Cypher queries
- [x] Implementar AlgorithmController REST (5 endpoints)
- [x] Agregar validaciones y manejo de excepciones
- [x] Escribir tests unitarios
- [x] Documentar endpoints
- [x] Crear Dockerfile
- [x] Crear docker-compose.yml
- [x] Crear documentación completa

---

## 🚀 Próximos Pasos

1. **Cargar datos reales** desde BlockCypher API
2. **Ejecutar análisis** con los 5 endpoints
3. **Revisar resultados** en Neo4j Browser
4. **Ajustar umbrales** según necesidad
5. **Implementar caché** para queries frecuentes
6. **Agregar autenticación** (OAuth2/JWT)
7. **Publicar con Swagger/OpenAPI**

---

## 📞 Soporte

Para problemas o preguntas:

1. Revisar DOCUMENTACION_ALGORITMOS.md
2. Verificar logs: `docker-compose logs -f app`
3. Consultar queries: http://localhost:7474

---

**Estado del Proyecto**: ✅ LISTO PARA USAR  
**Versión**: 1.0  
**Fecha**: 2025-01-04

package com.example.service;

import com.example.dto.PeelChainGreedyResult;
import com.example.repository.AlgorithmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests para GreedyAlgorithmService
 */
@DisplayName("Greedy Algorithm Service Tests")
public class GreedyAlgorithmServiceTest {
    
    @Mock
    private AlgorithmRepository algorithmRepository;
    
    @InjectMocks
    private GreedyAlgorithmService greedyService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("Should analyze peel chains and return sorted results")
    void testAnalyzePeelChainsGreedy() {
        // Arrange
        List<Map<String, Object>> mockData = createMockPeelChainData();
        when(algorithmRepository.getPeelChainCandidates(0.95))
                .thenReturn(mockData);
        
        // Act
        List<PeelChainGreedyResult> results = greedyService.analyzePeelChainsGreedy(0.95, 50);
        
        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Verificar que están ordenados por spending percentage descendente
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(
                    results.get(i).getSpendingPercentage() >= 
                    results.get(i + 1).getSpendingPercentage(),
                    "Results should be sorted by spending percentage DESC"
            );
        }
        
        // Verificar que todos tienen ranks asignados
        results.forEach(r -> assertNotNull(r.getRank()));
    }
    
    @Test
    @DisplayName("Should assign correct risk levels")
    void testRiskLevelAssignment() {
        // Arrange
        List<Map<String, Object>> mockData = createMockPeelChainData();
        when(algorithmRepository.getPeelChainCandidates(0.95))
                .thenReturn(mockData);
        
        // Act
        List<PeelChainGreedyResult> results = greedyService.analyzePeelChainsGreedy(0.95, 50);
        
        // Assert - verificar risk levels
        results.stream()
                .filter(r -> r.getSpendingPercentage() >= 0.98)
                .forEach(r -> assertEquals("CRITICAL", r.getRiskLevel()));
        
        results.stream()
                .filter(r -> r.getSpendingPercentage() >= 0.95 && r.getSpendingPercentage() < 0.98)
                .forEach(r -> assertEquals("HIGH", r.getRiskLevel()));
    }
    
    @Test
    @DisplayName("Should return empty list when threshold not met")
    void testEmptyResultsWhenThresholdNotMet() {
        // Arrange
        when(algorithmRepository.getPeelChainCandidates(0.99))
                .thenReturn(Collections.emptyList());
        
        // Act
        List<PeelChainGreedyResult> results = greedyService.analyzePeelChainsGreedy(0.99, 50);
        
        // Assert
        assertTrue(results.isEmpty());
    }
    
    @Test
    @DisplayName("Should respect limit parameter")
    void testLimitParameter() {
        // Arrange
        List<Map<String, Object>> mockData = createMockPeelChainDataWithSize(100);
        when(algorithmRepository.getPeelChainCandidates(0.95))
                .thenReturn(mockData);
        
        // Act
        List<PeelChainGreedyResult> results = greedyService.analyzePeelChainsGreedy(0.95, 10);
        
        // Assert
        assertTrue(results.size() <= 10);
    }
    
    private List<Map<String, Object>> createMockPeelChainData() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        data.add(Map.of(
                "wallet", "wallet1",
                "transaction", "tx1",
                "inputAmount", 1000L,
                "outputsTotal", 980L,
                "mainRecipient", "wallet2",
                "changeAmount", 20L
        ));
        
        data.add(Map.of(
                "wallet", "wallet2",
                "transaction", "tx2",
                "inputAmount", 980L,
                "outputsTotal", 950L,
                "mainRecipient", "wallet3",
                "changeAmount", 30L
        ));
        
        data.add(Map.of(
                "wallet", "wallet3",
                "transaction", "tx3",
                "inputAmount", 950L,
                "outputsTotal", 920L,
                "mainRecipient", "wallet4",
                "changeAmount", 30L
        ));
        
        return data;
    }
    
    private List<Map<String, Object>> createMockPeelChainDataWithSize(int size) {
        List<Map<String, Object>> data = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            long input = 1000L - (i * 10);
            long output = input - (input / 50); // 98% gasto
            
            data.add(Map.of(
                    "wallet", "wallet" + i,
                    "transaction", "tx" + i,
                    "inputAmount", input,
                    "outputsTotal", output,
                    "mainRecipient", "wallet" + (i + 1),
                    "changeAmount", input - output
            ));
        }
        
        return data;
    }
}

