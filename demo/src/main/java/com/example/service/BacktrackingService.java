package com.example.service;

import com.example.algorithm.BacktrackingAlgorithm;
import com.example.algorithm.BacktrackingAlgorithm.Edge;
import com.example.algorithm.BacktrackingAlgorithm.SuspiciousChain;
import com.example.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio que implementa BACKTRACKING para análisis forense
 *
 * Detecta patrones complejos de lavado de dinero mediante exploración exhaustiva
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BacktrackingService {

    private final TransactionRepository transactionRepository;
    private final BacktrackingAlgorithm backtrackingAlgorithm;

    /**
     * Constructor para inyección de dependencias
     */
    public BacktrackingService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        this.backtrackingAlgorithm = new BacktrackingAlgorithm();
    }

    /**
     * Detecta cadenas sospechosas desde una wallet específica
     *
     * @param startWallet Wallet desde donde iniciar la búsqueda
     * @param depth Profundidad máxima de exploración (recomendado: 4-6)
     * @return Lista de cadenas sospechosas ordenadas por nivel de sospecha
     */
    public List<SuspiciousChain> detectSuspiciousChains(String startWallet, int depth) {
        log.info("Detectando cadenas sospechosas desde wallet: {} con depth: {}", startWallet, depth);

        // PASO 1: Construir grafo desde Neo4j
        Map<String, List<Edge>> graph = buildGraphFromNeo4j(startWallet, depth + 2);

        if (graph.isEmpty()) {
            log.warn("No se encontraron transacciones para wallet: {}", startWallet);
            return Collections.emptyList();
        }

        log.info("Grafo construido: {} wallets, {} aristas totales",
                graph.size(),
                graph.values().stream().mapToInt(List::size).sum());

        // PASO 2: Ejecutar BACKTRACKING
        List<SuspiciousChain> chains = backtrackingAlgorithm.findSuspiciousChains(
            graph,
            startWallet,
            depth
        );

        log.info("Backtracking completado: {} cadenas sospechosas detectadas", chains.size());

        return chains;
    }

    /**
     * Detecta TODOS los ciclos en la red (útil para análisis global)
     *
     * @param maxCycles Número máximo de ciclos a retornar
     * @return Lista de ciclos detectados
     */
    public List<SuspiciousChain> detectAllCycles(int maxCycles) {
        log.info("Detectando ciclos en toda la red (max: {})", maxCycles);

        // Obtener wallets más activas para buscar ciclos
        List<Map<String, Object>> activeWallets = transactionRepository.findMostActiveWallets(50);

        List<SuspiciousChain> allCycles = new ArrayList<>();

        for (Map<String, Object> walletData : activeWallets) {
            String wallet = (String) walletData.get("wallet");

            // Construir grafo local
            Map<String, List<Edge>> graph = buildGraphFromNeo4j(wallet, 5);

            // Buscar ciclos con backtracking
            List<SuspiciousChain> chains = backtrackingAlgorithm.findSuspiciousChains(
                graph,
                wallet,
                4
            );

            // Filtrar solo ciclos
            chains.stream()
                .filter(chain -> chain.getType() == BacktrackingAlgorithm.ChainType.CYCLE)
                .forEach(allCycles::add);

            if (allCycles.size() >= maxCycles) {
                break;
            }
        }

        log.info("Detección de ciclos completada: {} ciclos encontrados", allCycles.size());

        return allCycles.stream()
            .limit(maxCycles)
            .collect(Collectors.toList());
    }

    /**
     * Construye el grafo de transacciones desde Neo4j
     *
     * @param startWallet Wallet inicial
     * @param maxHops Número máximo de saltos
     * @return Grafo como adjacency list
     */
    private Map<String, List<Edge>> buildGraphFromNeo4j(String startWallet, int maxHops) {
        Map<String, List<Edge>> graph = new HashMap<>();

        try {
            // Query para obtener transacciones cercanas
            String cypherQuery = String.format("""
                MATCH (start:Wallet {address: $wallet})
                MATCH path = (start)-[r:INPUT|OUTPUT*1..%d]-(other:Wallet)
                WHERE start <> other
                UNWIND relationships(path) as rel
                WITH startNode(rel) as from, endNode(rel) as to, rel
                WHERE from:Wallet AND to:Wallet
                RETURN DISTINCT
                    from.address as fromWallet,
                    to.address as toWallet,
                    COALESCE(rel.amount, rel.value, rel.outputValue, 0) as amount,
                    COALESCE(rel.txHash, 'unknown') as txHash,
                    COALESCE(rel.timestamp, 0) as timestamp
                LIMIT 1000
                """, maxHops);

            // Ejecutar query (simplificado - en producción usar el repository)
            List<Map<String, Object>> edges = transactionRepository.executeCustomQuery(
                cypherQuery,
                Map.of("wallet", startWallet)
            );

            // Construir adjacency list
            for (Map<String, Object> edge : edges) {
                String from = (String) edge.get("fromWallet");
                String to = (String) edge.get("toWallet");
                double amount = ((Number) edge.getOrDefault("amount", 0)).doubleValue();
                String txHash = (String) edge.getOrDefault("txHash", "unknown");
                long timestamp = ((Number) edge.getOrDefault("timestamp", 0L)).longValue();

                graph.computeIfAbsent(from, k -> new ArrayList<>())
                    .add(new Edge(to, amount, txHash, timestamp));
            }

        } catch (Exception e) {
            log.error("Error construyendo grafo desde Neo4j", e);
        }

        return graph;
    }
}

