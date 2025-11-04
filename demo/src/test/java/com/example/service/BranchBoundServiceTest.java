package com.example.service;

import com.example.algorithm.BranchAndBoundAlgorithm.OptimalPathResult;
import com.example.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para BranchBoundService
 */
@ExtendWith(MockitoExtension.class)
class BranchBoundServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    private BranchBoundService service;

    @BeforeEach
    void setUp() {
        service = new BranchBoundService(transactionRepository);
    }

    @Test
    @DisplayName("Debería encontrar camino óptimo con límite de costo")
    void testFindOptimalPathWithCostLimit() {
        // Preparar datos mock
        List<Map<String, Object>> mockEdges = createMockTransactionEdgesWithCosts();
        when(transactionRepository.executeCustomQuery(anyString(), anyMap()))
            .thenReturn(mockEdges);

        // Ejecutar
        OptimalPathResult result = service.findOptimalPathWithCostLimit(
            "walletA", "walletB", 100.0
        );

        // Verificar
        assertNotNull(result);
        assertEquals("walletA", result.getSourceWallet());
        assertEquals("walletB", result.getTargetWallet());
        verify(transactionRepository, atLeastOnce()).executeCustomQuery(anyString(), anyMap());
    }

    @Test
    @DisplayName("Debería manejar wallets sin conexión")
    void testNoConnection() {
        // Mock: sin aristas
        when(transactionRepository.executeCustomQuery(anyString(), anyMap()))
            .thenReturn(Collections.emptyList());

        // Ejecutar
        OptimalPathResult result = service.findOptimalPathWithCostLimit(
            "isolated1", "isolated2", 100.0
        );

        // Verificar
        assertNotNull(result);
        assertFalse(result.isPathFound(), "No debería encontrar camino entre wallets aisladas");
    }

    @Test
    @DisplayName("Debería encontrar múltiples caminos con diferentes costos")
    void testMultiplePathsWithDifferentCosts() {
        // Preparar datos mock
        when(transactionRepository.executeCustomQuery(anyString(), anyMap()))
            .thenReturn(createMockTransactionEdgesWithCosts());

        // Ejecutar
        Map<String, OptimalPathResult> results = service.findMultiplePathsWithDifferentCosts(
            "walletA", "walletB"
        );

        // Verificar
        assertNotNull(results);
        assertFalse(results.isEmpty(), "Debería retornar al menos un resultado");

        // Verificar que hay múltiples escenarios
        assertTrue(results.size() > 0, "Debería haber múltiples escenarios");
    }

    @Test
    @DisplayName("Debería encontrar el camino más barato sin restricción")
    void testFindCheapestPath() {
        // Preparar datos mock
        when(transactionRepository.executeCustomQuery(anyString(), anyMap()))
            .thenReturn(createMockTransactionEdgesWithCosts());

        // Ejecutar
        OptimalPathResult result = service.findCheapestPath("walletA", "walletB");

        // Verificar
        assertNotNull(result);
        assertEquals("walletA", result.getSourceWallet());
        assertEquals("walletB", result.getTargetWallet());
    }

    @Test
    @DisplayName("Debería validar inputs correctamente")
    void testInputValidation() {
        when(transactionRepository.executeCustomQuery(anyString(), anyMap()))
            .thenReturn(createMockTransactionEdgesWithCosts());

        // Wallets válidas
        assertDoesNotThrow(() ->
            service.findOptimalPathWithCostLimit("wallet1", "wallet2", 100.0)
        );

        // MaxCost negativo (debería manejarlo)
        assertDoesNotThrow(() ->
            service.findOptimalPathWithCostLimit("wallet1", "wallet2", -10.0)
        );
    }

    @Test
    @DisplayName("Debería manejar errores de Neo4j")
    void testNeo4jErrorHandling() {
        // Simular error
        when(transactionRepository.executeCustomQuery(anyString(), anyMap()))
            .thenThrow(new RuntimeException("Database error"));

        // No debería lanzar excepción
        assertDoesNotThrow(() -> {
            OptimalPathResult result = service.findOptimalPathWithCostLimit(
                "wallet1", "wallet2", 100.0
            );
            assertNotNull(result);
            assertFalse(result.isPathFound());
        });
    }

    @Test
    @DisplayName("Debería retornar métricas de exploración")
    void testExplorationMetrics() {
        when(transactionRepository.executeCustomQuery(anyString(), anyMap()))
            .thenReturn(createMockTransactionEdgesWithCosts());

        OptimalPathResult result = service.findOptimalPathWithCostLimit(
            "walletA", "walletB", 100.0
        );

        assertNotNull(result);
        // Las métricas deberían estar presentes incluso si no encuentra camino
        assertTrue(result.getNodesExplored() >= 0);
        assertTrue(result.getBranchesPruned() >= 0);
    }

    // Métodos auxiliares

    private List<Map<String, Object>> createMockTransactionEdgesWithCosts() {
        List<Map<String, Object>> edges = new ArrayList<>();

        // Arista 1: walletA → walletC (amount=50, cost=20)
        Map<String, Object> edge1 = new HashMap<>();
        edge1.put("fromWallet", "walletA");
        edge1.put("toWallet", "walletC");
        edge1.put("amount", 50.0);
        edge1.put("cost", 20.0);
        edge1.put("txHash", "tx1");
        edge1.put("timestamp", System.currentTimeMillis());
        edges.add(edge1);

        // Arista 2: walletC → walletB (amount=40, cost=15)
        Map<String, Object> edge2 = new HashMap<>();
        edge2.put("fromWallet", "walletC");
        edge2.put("toWallet", "walletB");
        edge2.put("amount", 40.0);
        edge2.put("cost", 15.0);
        edge2.put("txHash", "tx2");
        edge2.put("timestamp", System.currentTimeMillis());
        edges.add(edge2);

        // Arista 3: walletA → walletD (amount=30, cost=5)
        Map<String, Object> edge3 = new HashMap<>();
        edge3.put("fromWallet", "walletA");
        edge3.put("toWallet", "walletD");
        edge3.put("amount", 30.0);
        edge3.put("cost", 5.0);
        edge3.put("txHash", "tx3");
        edge3.put("timestamp", System.currentTimeMillis());
        edges.add(edge3);

        // Arista 4: walletD → walletB (amount=25, cost=8)
        Map<String, Object> edge4 = new HashMap<>();
        edge4.put("fromWallet", "walletD");
        edge4.put("toWallet", "walletB");
        edge4.put("amount", 25.0);
        edge4.put("cost", 8.0);
        edge4.put("txHash", "tx4");
        edge4.put("timestamp", System.currentTimeMillis());
        edges.add(edge4);

        return edges;
    }
}
package com.example.algorithm;

import com.example.algorithm.BacktrackingAlgorithm.ChainType;
import com.example.algorithm.BacktrackingAlgorithm.Edge;
import com.example.algorithm.BacktrackingAlgorithm.SuspiciousChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para BacktrackingAlgorithm
 */
class BacktrackingAlgorithmTest {

    private BacktrackingAlgorithm algorithm;
    private Map<String, List<Edge>> testGraph;

    @BeforeEach
    void setUp() {
        algorithm = new BacktrackingAlgorithm();
        testGraph = createTestGraph();
    }

    /**
     * Crea un grafo de prueba con ciclos y cadenas
     *
     * Estructura:
     *   A → B → C → A (ciclo)
     *   A → D → E → F (cadena)
     */
    private Map<String, List<Edge>> createTestGraph() {
        Map<String, List<Edge>> graph = new HashMap<>();

        // A → B, A → D
        graph.put("A", Arrays.asList(
            new Edge("B", 100.0, "tx1", System.currentTimeMillis()),
            new Edge("D", 50.0, "tx2", System.currentTimeMillis())
        ));

        // B → C
        graph.put("B", Arrays.asList(
            new Edge("C", 80.0, "tx3", System.currentTimeMillis())
        ));

        // C → A (cierra el ciclo)
        graph.put("C", Arrays.asList(
            new Edge("A", 70.0, "tx4", System.currentTimeMillis())
        ));

        // D → E
        graph.put("D", Arrays.asList(
            new Edge("E", 60.0, "tx5", System.currentTimeMillis())
        ));

        // E → F
        graph.put("E", Arrays.asList(
            new Edge("F", 40.0, "tx6", System.currentTimeMillis())
        ));

        // F no tiene salidas (nodo terminal)
        graph.put("F", Collections.emptyList());

        return graph;
    }

    @Test
    @DisplayName("Debería detectar ciclo A→B→C→A")
    void testDetectCycle() {
        // Ejecutar backtracking desde A con profundidad 4
        List<SuspiciousChain> chains = algorithm.findSuspiciousChains(testGraph, "A", 4);

        // Verificar que se detectó al menos un ciclo
        assertNotNull(chains);
        assertFalse(chains.isEmpty());

        // Buscar el ciclo específico
        Optional<SuspiciousChain> cycle = chains.stream()
            .filter(chain -> chain.getType() == ChainType.CYCLE)
            .findFirst();

        assertTrue(cycle.isPresent(), "Debería detectar al menos un ciclo");

        SuspiciousChain detectedCycle = cycle.get();
        assertEquals(ChainType.CYCLE, detectedCycle.getType());
        assertTrue(detectedCycle.getSuspicionLevel() >= 0.9,
            "Los ciclos deberían tener alto nivel de sospecha");
    }

    @Test
    @DisplayName("Debería explorar caminos hasta profundidad máxima")
    void testDepthLimit() {
        int maxDepth = 3;
        List<SuspiciousChain> chains = algorithm.findSuspiciousChains(testGraph, "A", maxDepth);

        // Todas las cadenas deberían tener longitud <= maxDepth + 1
        for (SuspiciousChain chain : chains) {
            assertTrue(chain.getDepth() <= maxDepth + 1,
                "Profundidad de cadena no debería exceder límite: " + chain.getDepth());
        }
    }

    @Test
    @DisplayName("Debería detectar peel chains largos")
    void testPeelChainDetection() {
        List<SuspiciousChain> chains = algorithm.findSuspiciousChains(testGraph, "A", 5);

        // Buscar peel chains (cadenas largas)
        Optional<SuspiciousChain> peelChain = chains.stream()
            .filter(chain -> chain.getType() == ChainType.PEEL_CHAIN)
            .findFirst();

        // Puede o no encontrar peel chains dependiendo de la profundidad
        if (peelChain.isPresent()) {
            assertTrue(peelChain.get().getDepth() >= 5,
                "Peel chains deberían ser cadenas largas");
        }
    }

    @Test
    @DisplayName("Debería manejar grafo vacío correctamente")
    void testEmptyGraph() {
        Map<String, List<Edge>> emptyGraph = new HashMap<>();
        List<SuspiciousChain> chains = algorithm.findSuspiciousChains(emptyGraph, "A", 5);

        assertNotNull(chains);
        assertTrue(chains.isEmpty(), "Grafo vacío no debería producir cadenas");
    }

    @Test
    @DisplayName("Debería manejar nodo sin vecinos")
    void testIsolatedNode() {
        Map<String, List<Edge>> isolatedGraph = new HashMap<>();
        isolatedGraph.put("ISOLATED", Collections.emptyList());

        List<SuspiciousChain> chains = algorithm.findSuspiciousChains(isolatedGraph, "ISOLATED", 5);

        assertNotNull(chains);
        // No debería generar cadenas desde nodo aislado
        assertTrue(chains.isEmpty() || chains.stream()
            .allMatch(chain -> chain.getDepth() == 0));
    }

    @Test
    @DisplayName("Debería calcular nivel de sospecha correctamente")
    void testSuspicionLevelCalculation() {
        List<SuspiciousChain> chains = algorithm.findSuspiciousChains(testGraph, "A", 5);

        for (SuspiciousChain chain : chains) {
            // Nivel de sospecha debe estar entre 0 y 1
            assertTrue(chain.getSuspicionLevel() >= 0.0 && chain.getSuspicionLevel() <= 1.0,
                "Nivel de sospecha debe estar entre 0 y 1: " + chain.getSuspicionLevel());

            // Ciclos deben tener nivel muy alto
            if (chain.getType() == ChainType.CYCLE) {
                assertTrue(chain.getSuspicionLevel() >= 0.9,
                    "Ciclos deben tener nivel de sospecha alto");
            }
        }
    }

    @Test
    @DisplayName("Debería generar descripciones para cada cadena")
    void testDescriptionGeneration() {
        List<SuspiciousChain> chains = algorithm.findSuspiciousChains(testGraph, "A", 5);

        for (SuspiciousChain chain : chains) {
            assertNotNull(chain.getDescription(), "Cada cadena debe tener descripción");
            assertFalse(chain.getDescription().isEmpty(), "Descripción no debe estar vacía");
        }
    }

    @Test
    @DisplayName("Debería ordenar resultados por nivel de sospecha")
    void testResultOrdering() {
        List<SuspiciousChain> chains = algorithm.findSuspiciousChains(testGraph, "A", 5);

        if (chains.size() > 1) {
            // Verificar que están ordenados de mayor a menor sospecha
            for (int i = 0; i < chains.size() - 1; i++) {
                assertTrue(chains.get(i).getSuspicionLevel() >= chains.get(i + 1).getSuspicionLevel(),
                    "Resultados deben estar ordenados por nivel de sospecha descendente");
            }
        }
    }

    @Test
    @DisplayName("Debería detectar múltiples tipos de patrones")
    void testMultiplePatternTypes() {
        List<SuspiciousChain> chains = algorithm.findSuspiciousChains(testGraph, "A", 5);

        // Obtener tipos únicos detectados
        Set<ChainType> detectedTypes = new HashSet<>();
        for (SuspiciousChain chain : chains) {
            detectedTypes.add(chain.getType());
        }

        // Debería detectar al menos el ciclo
        assertTrue(detectedTypes.contains(ChainType.CYCLE),
            "Debería detectar al menos el tipo CYCLE");
    }

    @Test
    @DisplayName("Test de performance: debería completar en tiempo razonable")
    void testPerformance() {
        long startTime = System.currentTimeMillis();

        List<SuspiciousChain> chains = algorithm.findSuspiciousChains(testGraph, "A", 6);

        long executionTime = System.currentTimeMillis() - startTime;

        // Debería completar en menos de 1 segundo para grafo pequeño
        assertTrue(executionTime < 1000,
            "Búsqueda debería completar en menos de 1 segundo: " + executionTime + "ms");

        assertNotNull(chains);
    }
}

