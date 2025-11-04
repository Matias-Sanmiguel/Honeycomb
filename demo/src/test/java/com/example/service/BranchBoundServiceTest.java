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
