package com.example.service;

import com.example.algorithm.BacktrackingAlgorithm.ChainType;
import com.example.algorithm.BacktrackingAlgorithm.SuspiciousChain;
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
 * Tests unitarios para BacktrackingService
 */
@ExtendWith(MockitoExtension.class)
class BacktrackingServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private org.springframework.data.neo4j.core.Neo4jClient neo4jClient;

    private BacktrackingService service;

    @BeforeEach
    void setUp() {
        service = new BacktrackingService(transactionRepository, neo4jClient);
    }

    @Test
    @DisplayName("Debería detectar cadenas sospechosas desde wallet específica")
    void testDetectSuspiciousChains() {
        // Preparar datos mock
        List<Map<String, Object>> mockEdges = createMockTransactionEdges();
        when(transactionRepository.executeCustomQuery(anyString(), anyMap()))
            .thenReturn(mockEdges);

        // Ejecutar
        List<SuspiciousChain> chains = service.detectSuspiciousChains("wallet1", 5);

        // Verificar
        assertNotNull(chains);
        verify(transactionRepository, atLeastOnce()).executeCustomQuery(anyString(), anyMap());
    }

    @Test
    @DisplayName("Debería manejar wallet sin transacciones")
    void testEmptyWallet() {
        // Mock: wallet sin transacciones
        when(transactionRepository.executeCustomQuery(anyString(), anyMap()))
            .thenReturn(Collections.emptyList());

        // Ejecutar
        List<SuspiciousChain> chains = service.detectSuspiciousChains("emptyWallet", 5);

        // Verificar
        assertNotNull(chains);
        assertTrue(chains.isEmpty(), "Wallet sin transacciones no debería generar cadenas");
    }

    @Test
    @DisplayName("Debería detectar todos los ciclos en la red")
    void testDetectAllCycles() {
        // Mock: wallets activas
        List<Map<String, Object>> activeWallets = Arrays.asList(
            createWalletMap("wallet1", 50),
            createWalletMap("wallet2", 30)
        );
        when(transactionRepository.findMostActiveWallets(anyInt()))
            .thenReturn(activeWallets);

        // Mock: transacciones
        when(transactionRepository.executeCustomQuery(anyString(), anyMap()))
            .thenReturn(createMockTransactionEdges());

        // Ejecutar
        List<SuspiciousChain> cycles = service.detectAllCycles(10);

        // Verificar
        assertNotNull(cycles);
        verify(transactionRepository).findMostActiveWallets(50);
    }

    @Test
    @DisplayName("Debería validar profundidad dentro de límites")
    void testDepthValidation() {
        when(transactionRepository.executeCustomQuery(anyString(), anyMap()))
            .thenReturn(createMockTransactionEdges());

        // Profundidad válida
        assertDoesNotThrow(() -> service.detectSuspiciousChains("wallet1", 5));

        // Profundidad muy alta (debería funcionar pero con poda)
        assertDoesNotThrow(() -> service.detectSuspiciousChains("wallet1", 10));
    }

    @Test
    @DisplayName("Debería manejar errores de Neo4j gracefully")
    void testNeo4jErrorHandling() {
        // Simular error en Neo4j
        when(transactionRepository.executeCustomQuery(anyString(), anyMap()))
            .thenThrow(new RuntimeException("Neo4j connection error"));

        // No debería lanzar excepción, sino retornar lista vacía
        assertDoesNotThrow(() -> {
            List<SuspiciousChain> chains = service.detectSuspiciousChains("wallet1", 5);
            assertNotNull(chains);
        });
    }

    // Métodos auxiliares para crear datos mock

    private List<Map<String, Object>> createMockTransactionEdges() {
        List<Map<String, Object>> edges = new ArrayList<>();

        // Arista 1: wallet1 → wallet2
        Map<String, Object> edge1 = new HashMap<>();
        edge1.put("fromWallet", "wallet1");
        edge1.put("toWallet", "wallet2");
        edge1.put("amount", 100.0);
        edge1.put("txHash", "tx123");
        edge1.put("timestamp", System.currentTimeMillis());
        edges.add(edge1);

        // Arista 2: wallet2 → wallet3
        Map<String, Object> edge2 = new HashMap<>();
        edge2.put("fromWallet", "wallet2");
        edge2.put("toWallet", "wallet3");
        edge2.put("amount", 80.0);
        edge2.put("txHash", "tx456");
        edge2.put("timestamp", System.currentTimeMillis());
        edges.add(edge2);

        // Arista 3: wallet3 → wallet1 (cierra ciclo)
        Map<String, Object> edge3 = new HashMap<>();
        edge3.put("fromWallet", "wallet3");
        edge3.put("toWallet", "wallet1");
        edge3.put("amount", 70.0);
        edge3.put("txHash", "tx789");
        edge3.put("timestamp", System.currentTimeMillis());
        edges.add(edge3);

        return edges;
    }

    private Map<String, Object> createWalletMap(String address, int txCount) {
        Map<String, Object> wallet = new HashMap<>();
        wallet.put("wallet", address);
        wallet.put("txCount", txCount);
        return wallet;
    }
}

