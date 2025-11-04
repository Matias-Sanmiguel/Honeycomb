package com.example.service;

import com.example.algorithm.BranchAndBoundAlgorithm;
import com.example.algorithm.BranchAndBoundAlgorithm.Edge;
import com.example.algorithm.BranchAndBoundAlgorithm.OptimalPathResult;
import com.example.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Servicio que implementa BRANCH & BOUND para análisis forense
 *
 * Encuentra caminos óptimos entre wallets con restricciones de costo
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BranchBoundService {

    private final TransactionRepository transactionRepository;
    private final BranchAndBoundAlgorithm branchBoundAlgorithm;

    /**
     * Constructor para inyección de dependencias
     */
    public BranchBoundService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
        this.branchBoundAlgorithm = new BranchAndBoundAlgorithm();
    }

    /**
     * Encuentra el camino óptimo entre dos wallets con restricción de costo
     *
     * @param sourceWallet Wallet origen
     * @param targetWallet Wallet destino
     * @param maxCost Costo máximo permitido (en satoshis de fees)
     * @return Resultado con el camino óptimo
     */
    public OptimalPathResult findOptimalPathWithCostLimit(
            String sourceWallet,
            String targetWallet,
            double maxCost) {

        log.info("Buscando camino óptimo de {} a {} con maxCost: {}",
                sourceWallet, targetWallet, maxCost);

        // PASO 1: Construir grafo con costos desde Neo4j
        Map<String, List<Edge>> graph = buildGraphWithCosts(sourceWallet, targetWallet, 10);

        if (graph.isEmpty()) {
            log.warn("No se encontró conexión entre {} y {}", sourceWallet, targetWallet);
            return OptimalPathResult.builder()
                .sourceWallet(sourceWallet)
                .targetWallet(targetWallet)
                .pathFound(false)
                .build();
        }

        log.info("Grafo construido: {} wallets en el rango de búsqueda", graph.size());

        // PASO 2: Ejecutar BRANCH & BOUND
        OptimalPathResult result = branchBoundAlgorithm.findOptimalPath(
            graph,
            sourceWallet,
            targetWallet,
            maxCost
        );

        if (result.isPathFound()) {
            log.info("✅ Camino óptimo encontrado: {} hops, costo total: {}",
                    result.getPathLength(), result.getTotalCost());
        } else {
            log.warn("❌ No se encontró camino con costo <= {}", maxCost);
        }

        return result;
    }

    /**
     * Encuentra múltiples caminos óptimos con diferentes restricciones de costo
     * Útil para análisis comparativo
     *
     * @param sourceWallet Wallet origen
     * @param targetWallet Wallet destino
     * @return Mapa con resultados para diferentes límites de costo
     */
    public Map<String, OptimalPathResult> findMultiplePathsWithDifferentCosts(
            String sourceWallet,
            String targetWallet) {

        log.info("Buscando múltiples caminos óptimos entre {} y {}", sourceWallet, targetWallet);

        Map<String, List<Edge>> graph = buildGraphWithCosts(sourceWallet, targetWallet, 10);

        if (graph.isEmpty()) {
            return Collections.emptyMap();
        }

        // Probar con diferentes límites de costo
        double[] costLimits = {50.0, 100.0, 200.0, 500.0, 1000.0};
        Map<String, OptimalPathResult> results = new LinkedHashMap<>();

        for (double maxCost : costLimits) {
            OptimalPathResult result = branchBoundAlgorithm.findOptimalPath(
                graph,
                sourceWallet,
                targetWallet,
                maxCost
            );

            results.put("maxCost_" + (int)maxCost, result);

            // Si encontramos un camino, los siguientes límites también lo encontrarán
            if (result.isPathFound() && result.getTotalCost() < maxCost) {
                break;
            }
        }

        return results;
    }

    /**
     * Encuentra el camino con MENOR COSTO sin restricción
     * (equivalente a maxCost = infinito)
     *
     * @param sourceWallet Wallet origen
     * @param targetWallet Wallet destino
     * @return Camino con menor costo posible
     */
    public OptimalPathResult findCheapestPath(String sourceWallet, String targetWallet) {
        log.info("Buscando camino más barato entre {} y {}", sourceWallet, targetWallet);

        // Usar un límite muy alto para encontrar el camino más barato
        return findOptimalPathWithCostLimit(sourceWallet, targetWallet, Double.MAX_VALUE);
    }

    /**
     * Construye el grafo con COSTOS (fees) desde Neo4j
     *
     * @param sourceWallet Wallet origen
     * @param targetWallet Wallet destino
     * @param maxHops Número máximo de saltos a explorar
     * @return Grafo como adjacency list con costos
     */
    private Map<String, List<Edge>> buildGraphWithCosts(
            String sourceWallet,
            String targetWallet,
            int maxHops) {

        Map<String, List<Edge>> graph = new HashMap<>();

        try {
            // Query para obtener transacciones con fees
            String cypherQuery = String.format("""
                MATCH (source:Wallet {address: $source})
                MATCH (target:Wallet {address: $target})
                MATCH path = allShortestPaths((source)-[*1..%d]-(target))
                UNWIND relationships(path) as rel
                WITH startNode(rel) as from, endNode(rel) as to, rel
                WHERE from:Wallet AND to:Wallet
                
                // Obtener la transacción asociada para calcular fees
                MATCH (from)-[r]-(t:Transaction)-[]-(to)
                
                RETURN DISTINCT
                    from.address as fromWallet,
                    to.address as toWallet,
                    COALESCE(rel.amount, rel.value, rel.outputValue, 0) as amount,
                    COALESCE(t.fees, rel.amount * 0.001, 10) as cost,
                    COALESCE(rel.txHash, t.hash, 'unknown') as txHash,
                    COALESCE(t.confirmed, 0) as timestamp
                LIMIT 2000
                """, maxHops);

            List<Map<String, Object>> edges = transactionRepository.executeCustomQuery(
                cypherQuery,
                Map.of(
                    "source", sourceWallet,
                    "target", targetWallet
                )
            );

            // Si no hay caminos directos, intentar exploración más amplia
            if (edges.isEmpty()) {
                log.info("No se encontraron caminos directos, expandiendo búsqueda...");
                edges = buildGraphWithBreadthFirstSearch(sourceWallet, targetWallet, maxHops);
            }

            // Construir adjacency list
            for (Map<String, Object> edge : edges) {
                String from = (String) edge.get("fromWallet");
                String to = (String) edge.get("toWallet");
                double amount = ((Number) edge.getOrDefault("amount", 0)).doubleValue();
                double cost = ((Number) edge.getOrDefault("cost", 10.0)).doubleValue();
                String txHash = (String) edge.getOrDefault("txHash", "unknown");
                long timestamp = ((Number) edge.getOrDefault("timestamp", 0L)).longValue();

                // Agregar arista
                graph.computeIfAbsent(from, k -> new ArrayList<>())
                    .add(new Edge(to, amount, cost, txHash, timestamp));

                // Agregar arista bidireccional (grafo no dirigido)
                graph.computeIfAbsent(to, k -> new ArrayList<>())
                    .add(new Edge(from, amount, cost, txHash, timestamp));
            }

        } catch (Exception e) {
            log.error("Error construyendo grafo con costos desde Neo4j", e);
        }

        return graph;
    }

    /**
     * Búsqueda alternativa usando BFS para encontrar vecinos
     */
    private List<Map<String, Object>> buildGraphWithBreadthFirstSearch(
            String sourceWallet,
            String targetWallet,
            int maxHops) {

        try {
            String cypherQuery = String.format("""
                MATCH (start:Wallet {address: $source})
                MATCH path = (start)-[*1..%d]-(end:Wallet)
                UNWIND relationships(path) as rel
                WITH DISTINCT startNode(rel) as from, endNode(rel) as to, rel
                WHERE from:Wallet AND to:Wallet
                
                OPTIONAL MATCH (from)-[]-(t:Transaction)-[]-(to)
                
                RETURN 
                    from.address as fromWallet,
                    to.address as toWallet,
                    COALESCE(rel.amount, rel.value, rel.outputValue, 100) as amount,
                    COALESCE(t.fees, 10) as cost,
                    COALESCE(t.hash, 'unknown') as txHash,
                    0 as timestamp
                LIMIT 1000
                """, maxHops);

            return transactionRepository.executeCustomQuery(
                cypherQuery,
                Map.of("source", sourceWallet)
            );

        } catch (Exception e) {
            log.error("Error en BFS", e);
            return Collections.emptyList();
        }
    }
}

