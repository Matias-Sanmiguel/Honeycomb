package com.example.service;

import com.example.algorithm.BranchAndBoundAlgorithm;
import com.example.algorithm.BranchAndBoundAlgorithm.Edge;
import com.example.algorithm.BranchAndBoundAlgorithm.OptimalPathResult;
import com.example.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
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
    private final Neo4jClient neo4jClient;
    private final BranchAndBoundAlgorithm branchBoundAlgorithm = new BranchAndBoundAlgorithm();

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
            // Query simplificada: obtener todas las conexiones desde la wallet origen
            String cypherQuery = """
                MATCH (w1:Wallet)-[:INPUT]->(t:Transaction)-[:OUTPUT]->(w2:Wallet)
                WHERE w1.address = $source AND w1.address <> w2.address
                RETURN DISTINCT 
                    w1.address as fromWallet,
                    w2.address as toWallet,
                    t.hash as txHash,
                    COALESCE(t.fee, 0.0001) as cost
                LIMIT 1000
                """;

            Collection<Map<String, Object>> edges = neo4jClient.query(cypherQuery)
                .bindAll(Map.of("source", sourceWallet))
                .fetch()
                .all();

            log.info("Encontradas {} aristas desde {}", edges.size(), sourceWallet);

            // Obtener también las conexiones desde el destino
            String targetQuery = """
                MATCH (w1:Wallet)-[:INPUT]->(t:Transaction)-[:OUTPUT]->(w2:Wallet)
                WHERE w1.address = $target AND w1.address <> w2.address
                RETURN DISTINCT 
                    w1.address as fromWallet,
                    w2.address as toWallet,
                    t.hash as txHash,
                    COALESCE(t.fee, 0.0001) as cost
                LIMIT 1000
                """;

            Collection<Map<String, Object>> targetEdges = neo4jClient.query(targetQuery)
                .bindAll(Map.of("target", targetWallet))
                .fetch()
                .all();

            List<Map<String, Object>> allEdges = new ArrayList<>();
            allEdges.addAll(edges);
            allEdges.addAll(targetEdges);
            log.info("Total de aristas: {}", allEdges.size());

            // Si aún no tenemos suficientes datos, intentar una búsqueda más amplia
            if (allEdges.size() < 5) {
                String broadQuery = """
                    MATCH (w1:Wallet)-[:INPUT]->(t:Transaction)-[:OUTPUT]->(w2:Wallet)
                    WHERE w1.address <> w2.address
                    RETURN DISTINCT 
                        w1.address as fromWallet,
                        w2.address as toWallet,
                        t.hash as txHash,
                        COALESCE(t.fee, 0.0001) as cost
                    LIMIT 500
                    """;

                Collection<Map<String, Object>> broadEdges = neo4jClient.query(broadQuery)
                    .fetch()
                    .all();

                allEdges.addAll(broadEdges);
                log.info("Después de búsqueda amplia: {} aristas", allEdges.size());
            }

            // Construir adjacency list
            for (Map<String, Object> edge : allEdges) {
                String from = (String) edge.get("fromWallet");
                String to = (String) edge.get("toWallet");

                if (from == null || to == null) continue;

                double cost = ((Number) edge.getOrDefault("cost", 0.0001)).doubleValue();
                String txHash = (String) edge.getOrDefault("txHash", "unknown");

                // Agregar arista
                graph.computeIfAbsent(from, k -> new ArrayList<>())
                    .add(new Edge(to, 1.0, cost, txHash, 0L));

                // Grafo bidireccional
                graph.computeIfAbsent(to, k -> new ArrayList<>())
                    .add(new Edge(from, 1.0, cost, txHash, 0L));
            }

            log.info("Grafo construido con {} nodos", graph.size());

        } catch (Exception e) {
            log.error("Error construyendo grafo con costos desde Neo4j: {}", e.getMessage(), e);
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
