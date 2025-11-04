package com.example.service;

import com.example.dto.MaxFlowPathResult;
import com.example.repository.AlgorithmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio implementando algoritmos Dynamic Programming
 * Complejidad: O(V + E) - similar a Bellman-Ford
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicProgrammingService {

    private final AlgorithmRepository algorithmRepository;

    /**
     * ALGORITMO DYNAMIC PROGRAMMING: Encontrar camino con máximo valor
     *
     * Problema: Dado un grafo de transacciones, encontrar la ruta que MAXIMIZA
     * el valor total de fondos transferidos entre dos wallets
     *
     * Formulación DP:
     * dp[wallet] = máximo valor acumulado para llegar a esa wallet
     *
     * Base: dp[origen] = 0
     * Transición: Para cada arista (u → v, valor):
     *            dp[v] = max(dp[v], dp[u] + valor)
     *
     * Respuesta: dp[destino]
     *
     * Complejidad Temporal: O(V + E)
     * Complejidad Espacial: O(V)
     */
    public MaxFlowPathResult findMaxFlowPath(
            String sourceWallet,
            String targetWallet,
            Integer maxHops) {

        long startTime = System.currentTimeMillis();
        log.info("Starting DP max-flow path analysis from {} to {} with maxHops: {}",
                sourceWallet, targetWallet, maxHops);

        try {
            // Validar inputs
            if (sourceWallet == null || sourceWallet.isBlank() ||
                targetWallet == null || targetWallet.isBlank()) {
                throw new IllegalArgumentException("Source and target wallets cannot be null");
            }

            if (sourceWallet.equals(targetWallet)) {
                log.warn("Source and target wallets are the same");
                return MaxFlowPathResult.builder()
                        .sourceWallet(sourceWallet)
                        .targetWallet(targetWallet)
                        .maxFlowValue(0.0)
                        .pathLength(0)
                        .foundPath(false)
                        .path(Collections.emptyList())
                        .build();
            }

            // Obtener transacciones entre wallets desde Neo4j
            List<Map<String, Object>> pathData = algorithmRepository
                    .findPathsWithValues(sourceWallet, targetWallet, maxHops != null ? maxHops : 10);

            if (pathData.isEmpty()) {
                log.info("No paths found between {} and {}", sourceWallet, targetWallet);
                return MaxFlowPathResult.builder()
                        .sourceWallet(sourceWallet)
                        .targetWallet(targetWallet)
                        .maxFlowValue(0.0)
                        .pathLength(0)
                        .foundPath(false)
                        .path(Collections.emptyList())
                        .build();
            }

            // FASE 1: Construir grafo de transacciones
            Map<String, List<Edge>> graph = buildTransactionGraph(pathData);

            // FASE 2: Aplicar DP para encontrar máximo flujo
            DPResult dpResult = computeMaxFlowDP(graph, sourceWallet, targetWallet);

            // FASE 3: Reconstruir el camino
            List<MaxFlowPathResult.PathStep> reconstructedPath =
                    reconstructPath(dpResult, sourceWallet, targetWallet, graph);

            long executionTime = System.currentTimeMillis() - startTime;

            log.info("DP analysis completed in {}ms, max flow: {}, path length: {}",
                    executionTime, dpResult.maxValue, reconstructedPath.size());

            return MaxFlowPathResult.builder()
                    .sourceWallet(sourceWallet)
                    .targetWallet(targetWallet)
                    .maxFlowValue(dpResult.maxValue)
                    .pathLength(reconstructedPath.size())
                    .path(reconstructedPath)
                    .foundPath(dpResult.maxValue > 0)
                    .build();

        } catch (Exception e) {
            log.error("Error in DP max-flow path analysis", e);
            return MaxFlowPathResult.builder()
                    .sourceWallet(sourceWallet)
                    .targetWallet(targetWallet)
                    .maxFlowValue(0.0)
                    .foundPath(false)
                    .build();
        }
    }

    /**
     * Estructura auxiliar para aristas en el grafo
     */
    private static class Edge {
        String to;
        Double amount;
        String txHash;
        Long timestamp;

        Edge(String to, Double amount, String txHash, Long timestamp) {
            this.to = to;
            this.amount = amount;
            this.txHash = txHash;
            this.timestamp = timestamp;
        }
    }

    /**
     * Construir grafo de transacciones desde datos de Neo4j
     */
    private Map<String, List<Edge>> buildTransactionGraph(List<Map<String, Object>> pathData) {
        Map<String, List<Edge>> graph = new HashMap<>();

        for (Map<String, Object> edge : pathData) {
            String from = (String) edge.get("from");
            String to = (String) edge.get("to");
            Double amount = ((Number) edge.get("amount")).doubleValue();
            String txHash = (String) edge.get("txHash");
            Long timestamp = ((Number) edge.get("timestamp")).longValue();

            graph.computeIfAbsent(from, k -> new ArrayList<>())
                    .add(new Edge(to, amount, txHash, timestamp));
        }

        return graph;
    }

    /**
     * Estructura para resultados de DP
     */
    private static class DPResult {
        Map<String, Double> dp;
        Map<String, String> parent;
        Double maxValue;

        DPResult(Map<String, Double> dp, Map<String, String> parent, Double maxValue) {
            this.dp = dp;
            this.parent = parent;
            this.maxValue = maxValue;
        }
    }

    /**
     * ALGORITMO DP: Calcular máximo flujo
     *
     * Implementación de DP para maximizar el valor acumulado
     */
    private DPResult computeMaxFlowDP(
            Map<String, List<Edge>> graph,
            String source,
            String target) {

        Map<String, Double> dp = new HashMap<>();
        Map<String, String> parent = new HashMap<>();

        // Base: valor inicial en la fuente es 0 (no hay entrada)
        dp.put(source, 0.0);

        // Procesar iterativamente (BFS topológico modificado)
        Queue<String> queue = new LinkedList<>();
        queue.offer(source);
        Set<String> visited = new HashSet<>();
        visited.add(source);

        int iterations = 0;
        int maxIterations = graph.size() * graph.size();

        while (!queue.isEmpty() && iterations < maxIterations) {
            String current = queue.poll();
            Double currentValue = dp.getOrDefault(current, Double.NEGATIVE_INFINITY);

            List<Edge> edges = graph.getOrDefault(current, Collections.emptyList());

            for (Edge edge : edges) {
                // Transición DP: actualizar valor máximo para el próximo nodo
                Double newValue = currentValue + edge.amount;
                Double existingValue = dp.getOrDefault(edge.to, Double.NEGATIVE_INFINITY);

                if (newValue > existingValue) {
                    dp.put(edge.to, newValue);
                    parent.put(edge.to, current);

                    if (!visited.contains(edge.to)) {
                        queue.offer(edge.to);
                        visited.add(edge.to);
                    }
                }
            }

            iterations++;
        }

        Double maxValue = dp.getOrDefault(target, 0.0);
        if (maxValue == Double.NEGATIVE_INFINITY) {
            maxValue = 0.0;
        }

        return new DPResult(dp, parent, maxValue);
    }

    /**
     * Reconstruir el camino a partir de los datos de DP
     */
    private List<MaxFlowPathResult.PathStep> reconstructPath(
            DPResult dpResult,
            String source,
            String target,
            Map<String, List<Edge>> graph) {

        List<MaxFlowPathResult.PathStep> path = new ArrayList<>();

        String current = target;
        int hopNumber = 0;

        while (!current.equals(source) && dpResult.parent.containsKey(current)) {
            String previous = dpResult.parent.get(current);

            // Encontrar la arista que conecta previous → current
            List<Edge> edges = graph.getOrDefault(previous, Collections.emptyList());
            Edge selectedEdge = edges.stream()
                    .filter(e -> e.to.equals(current))
                    .findFirst()
                    .orElse(null);

            if (selectedEdge != null) {
                MaxFlowPathResult.PathStep step = MaxFlowPathResult.PathStep.builder()
                        .from(previous)
                        .to(current)
                        .amount(selectedEdge.amount)
                        .transactionHash(selectedEdge.txHash)
                        .timestamp(new java.util.Date(selectedEdge.timestamp))
                        .hopNumber(hopNumber++)
                        .build();

                path.add(0, step); // Insertar al principio para mantener orden
            }

            current = previous;
        }

        return path;
    }
}

