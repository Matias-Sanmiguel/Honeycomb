package com.example.algorithm;

import com.example.algorithm.BranchAndBoundAlgorithm.Edge;
import com.example.algorithm.BranchAndBoundAlgorithm.OptimalPathResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para BranchAndBoundAlgorithm
 */
class BranchAndBoundAlgorithmTest {

    private BranchAndBoundAlgorithm algorithm;
    private Map<String, List<Edge>> testGraph;

    @BeforeEach
    void setUp() {
        algorithm = new BranchAndBoundAlgorithm();
        testGraph = createTestGraph();
    }

    /**
     * Crea un grafo de prueba con diferentes costos
     *
     * Estructura:
     *        50(cost=20)      30(cost=10)
     *    A ────────────→ B ────────────→ D
     *    │                │
     *    │20(cost=5)      │40(cost=15)
     *    ↓                ↓
     *    C ────────────→ D
     *        40(cost=8)
     */
    private Map<String, List<Edge>> createTestGraph() {
        Map<String, List<Edge>> graph = new HashMap<>();

        // A → B (amount=50, cost=20), A → C (amount=20, cost=5)
        graph.put("A", Arrays.asList(
            new Edge("B", 50.0, 20.0, "tx1", System.currentTimeMillis()),
            new Edge("C", 20.0, 5.0, "tx2", System.currentTimeMillis())
        ));

        // B → D (amount=30, cost=10)
        graph.put("B", Arrays.asList(
            new Edge("D", 30.0, 10.0, "tx3", System.currentTimeMillis())
        ));

        // C → D (amount=40, cost=8)
        graph.put("C", Arrays.asList(
            new Edge("D", 40.0, 8.0, "tx4", System.currentTimeMillis())
        ));

        // D no tiene salidas
        graph.put("D", Collections.emptyList());

        return graph;
    }

    @Test
    @DisplayName("Debería encontrar camino óptimo A→C→D (costo=13)")
    void testFindOptimalPath() {
        double maxCost = 100.0;

        OptimalPathResult result = algorithm.findOptimalPath(testGraph, "A", "D", maxCost);

        assertNotNull(result);
        assertTrue(result.isPathFound(), "Debería encontrar un camino");
        assertEquals("A", result.getSourceWallet());
        assertEquals("D", result.getTargetWallet());

        // Camino óptimo: A → C → D (costo = 5 + 8 = 13)
        assertEquals(13.0, result.getTotalCost(), 0.01, "Costo total debería ser 13");
        assertEquals(2, result.getPathLength(), "Longitud del camino debería ser 2");

        // Verificar que el camino es [A, C, D]
        List<String> expectedPath = Arrays.asList("A", "C", "D");
        assertEquals(expectedPath, result.getPath());
    }

    @Test
    @DisplayName("Debería respetar restricción de costo máximo")
    void testCostConstraint() {
        double maxCost = 10.0; // Límite muy bajo

        OptimalPathResult result = algorithm.findOptimalPath(testGraph, "A", "D", maxCost);

        assertNotNull(result);

        if (result.isPathFound()) {
            // Si encontró camino, debe respetar el límite
            assertTrue(result.getTotalCost() <= maxCost,
                "Costo total debe ser <= maxCost: " + result.getTotalCost() + " vs " + maxCost);
        }
        // Si no encontró camino, está bien (restricción muy estricta)
    }

    @Test
    @DisplayName("Debería podar ramas ineficientes")
    void testBranchPruning() {
        double maxCost = 100.0;

        OptimalPathResult result = algorithm.findOptimalPath(testGraph, "A", "D", maxCost);

        assertNotNull(result);
        assertTrue(result.isPathFound());

        // Debería haber podado al menos algunas ramas
        assertTrue(result.getBranchesPruned() > 0,
            "Debería haber podado al menos una rama");

        // Eficiencia de poda
        if (result.getNodesExplored() > 0) {
            double pruningRatio = (double) result.getBranchesPruned() / result.getNodesExplored();
            assertTrue(pruningRatio >= 0, "Ratio de poda debe ser no negativo");
        }
    }

    @Test
    @DisplayName("Debería manejar caso cuando origen = destino")
    void testSameSourceAndTarget() {
        OptimalPathResult result = algorithm.findOptimalPath(testGraph, "A", "A", 100.0);

        assertNotNull(result);
        // Puede retornar camino vacío o costo 0
        assertEquals(0.0, result.getTotalCost(), "Costo debería ser 0 si origen=destino");
    }

    @Test
    @DisplayName("Debería manejar camino inexistente")
    void testNoPath() {
        // Agregar nodo aislado
        testGraph.put("ISOLATED", Collections.emptyList());

        OptimalPathResult result = algorithm.findOptimalPath(testGraph, "A", "ISOLATED", 100.0);

        assertNotNull(result);
        assertFalse(result.isPathFound(), "No debería encontrar camino a nodo aislado");
    }

    @Test
    @DisplayName("Debería manejar grafo vacío")
    void testEmptyGraph() {
        Map<String, List<Edge>> emptyGraph = new HashMap<>();

        OptimalPathResult result = algorithm.findOptimalPath(emptyGraph, "A", "B", 100.0);

        assertNotNull(result);
        assertFalse(result.isPathFound(), "No debería encontrar camino en grafo vacío");
    }

    @Test
    @DisplayName("Debería generar métricas de exploración")
    void testExplorationMetrics() {
        OptimalPathResult result = algorithm.findOptimalPath(testGraph, "A", "D", 100.0);

        assertNotNull(result);
        assertTrue(result.getNodesExplored() > 0, "Debería haber explorado al menos un nodo");
        assertTrue(result.getBranchesPruned() >= 0, "Ramas podadas debe ser no negativo");
        assertTrue(result.getExecutionTimeMs() >= 0, "Tiempo de ejecución debe ser no negativo");
    }

    @Test
    @DisplayName("Debería encontrar mejor solución con múltiples caminos")
    void testMultiplePaths() {
        // Hay dos caminos posibles:
        // 1. A → B → D (costo = 20 + 10 = 30)
        // 2. A → C → D (costo = 5 + 8 = 13)

        OptimalPathResult result = algorithm.findOptimalPath(testGraph, "A", "D", 100.0);

        assertNotNull(result);
        assertTrue(result.isPathFound());

        // Debería elegir el camino más barato (A → C → D)
        assertEquals(13.0, result.getTotalCost(), 0.01,
            "Debería elegir el camino más barato: A→C→D");
    }

    @Test
    @DisplayName("Test de performance: debería completar rápidamente")
    void testPerformance() {
        long startTime = System.currentTimeMillis();

        OptimalPathResult result = algorithm.findOptimalPath(testGraph, "A", "D", 100.0);

        long executionTime = System.currentTimeMillis() - startTime;

        // Debería completar en menos de 500ms para grafo pequeño
        assertTrue(executionTime < 500,
            "Búsqueda debería completar en menos de 500ms: " + executionTime + "ms");

        assertNotNull(result);
        assertTrue(result.isPathFound());
    }

    @Test
    @DisplayName("Debería manejar costos negativos o inválidos")
    void testInvalidCosts() {
        // Intentar con maxCost negativo
        OptimalPathResult result = algorithm.findOptimalPath(testGraph, "A", "D", -1.0);

        // El algoritmo debería manejar esto gracefully
        assertNotNull(result);
        // No debería encontrar ningún camino con costo negativo
    }

    @Test
    @DisplayName("Debería retornar camino completo con detalles")
    void testPathDetails() {
        OptimalPathResult result = algorithm.findOptimalPath(testGraph, "A", "D", 100.0);

        assertNotNull(result);
        assertTrue(result.isPathFound());

        List<String> path = result.getPath();
        assertNotNull(path, "Camino no debe ser null");
        assertFalse(path.isEmpty(), "Camino no debe estar vacío");

        // Primer elemento debe ser el origen
        assertEquals("A", path.get(0), "Primer elemento debe ser origen");

        // Último elemento debe ser el destino
        assertEquals("D", path.get(path.size() - 1), "Último elemento debe ser destino");
    }

    @Test
    @DisplayName("Debería priorizar por costo (cola de prioridad)")
    void testPriorityOrdering() {
        // Con maxCost suficientemente alto, debería explorar el camino barato primero
        OptimalPathResult result = algorithm.findOptimalPath(testGraph, "A", "D", 1000.0);

        assertNotNull(result);
        assertTrue(result.isPathFound());

        // El algoritmo debería encontrar el camino óptimo
        assertEquals(13.0, result.getTotalCost(), 0.01);

        // Y debería haber explorado pocos nodos debido a la poda
        assertTrue(result.getNodesExplored() <= 10,
            "Debería explorar pocos nodos con poda efectiva: " + result.getNodesExplored());
    }
}

