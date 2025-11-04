package com.example.algorithm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * ALGORITMO BRANCH AND BOUND (Ramificación y Poda): Camino Óptimo con Restricciones
 *
 * <h2>Descripción Académica</h2>
 * Branch and Bound es una técnica de optimización que explora el espacio de
 * soluciones de manera sistemática, PODANDO ramas que no pueden llevar a una
 * solución óptima.
 *
 * <h2>Aplicación en Análisis Forense</h2>
 * - Encontrar el camino MÁS CORTO entre dos wallets
 * - Con restricción de COSTO MÁXIMO (fees acumuladas)
 * - Útil para rastrear flujos minimizando comisiones
 *
 * <h2>Complejidad</h2>
 * <ul>
 *   <li><b>Temporal:</b> O(b^d) en peor caso, pero la PODA reduce drásticamente</li>
 *   <li><b>Espacial:</b> O(b·d) para la cola de prioridad</li>
 *   <li><b>Mejor caso:</b> O(V log V + E) similar a Dijkstra con poda efectiva</li>
 * </ul>
 *
 * <h2>Estrategia de Poda</h2>
 * <pre>
 * PODAR rama SI:
 *   1. Costo acumulado > maxCost (violación de restricción)
 *   2. Costo acumulado + heurística > mejor_solución (no puede mejorar)
 *   3. Profundidad > maxDepth (límite de exploración)
 * </pre>
 *
 * <h2>Diferencia con Dijkstra</h2>
 * - Dijkstra: Solo encuentra camino más corto sin restricciones
 * - B&B: Puede optimizar múltiples criterios Y respetar restricciones
 *
 * <h2>Ejemplo de Uso</h2>
 * <pre>
 * BranchAndBoundAlgorithm bb = new BranchAndBoundAlgorithm();
 * OptimalPath path = bb.findOptimalPath(
 *     graph,
 *     "walletA",
 *     "walletB",
 *     100.0  // maxCost = 100 satoshis de fees
 * );
 *
 * // Output:
 * // {
 * //   path: [A→C→D→B],
 * //   totalCost: 85.5,
 * //   pathLength: 3,
 * //   branchesPruned: 47,
 * //   nodesExplored: 23
 * // }
 * </pre>
 *
 * @author Crypto Forensic Team
 * @version 1.0
 */
@Slf4j
public class BranchAndBoundAlgorithm {

    /**
     * Encuentra el camino óptimo con restricción de costo usando Branch & Bound
     *
     * @param graph Grafo de transacciones (adjacency list)
     * @param sourceWallet Wallet origen
     * @param targetWallet Wallet destino
     * @param maxCost Costo máximo permitido (fees acumuladas)
     * @return Camino óptimo encontrado
     */
    public OptimalPathResult findOptimalPath(
            Map<String, List<Edge>> graph,
            String sourceWallet,
            String targetWallet,
            double maxCost) {

        long startTime = System.currentTimeMillis();
        log.info("Iniciando BRANCH & BOUND de {} a {} con maxCost: {}",
                sourceWallet, targetWallet, maxCost);

        // Métricas de exploración
        BranchBoundMetrics metrics = new BranchBoundMetrics();

        // Cola de prioridad: explora primero los nodos con menor costo
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(
            Comparator.comparingDouble(n -> n.costSoFar + n.heuristic)
        );

        // Mejor solución encontrada hasta ahora
        OptimalPathResult bestSolution = OptimalPathResult.builder()
            .sourceWallet(sourceWallet)
            .targetWallet(targetWallet)
            .pathFound(false)
            .totalCost(Double.MAX_VALUE)
            .build();

        // Nodo inicial
        Node startNode = new Node(
            sourceWallet,
            new ArrayList<>(List.of(sourceWallet)),
            0.0,  // costo inicial = 0
            estimateHeuristic(sourceWallet, targetWallet, graph)
        );

        priorityQueue.offer(startNode);

        // Visitados: para evitar ciclos
        Map<String, Double> visited = new HashMap<>();

        // ========== ALGORITMO BRANCH & BOUND ==========
        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();
            metrics.nodesExplored++;

            // CASO 1: LLEGAMOS AL DESTINO
            if (currentNode.wallet.equals(targetWallet)) {
                // VALIDAR: El camino debe respetar la restricción de costo máximo
                if (currentNode.costSoFar <= maxCost &&
                    currentNode.costSoFar < bestSolution.getTotalCost()) {
                    bestSolution = OptimalPathResult.builder()
                        .sourceWallet(sourceWallet)
                        .targetWallet(targetWallet)
                        .path(new ArrayList<>(currentNode.path))
                        .totalCost(currentNode.costSoFar)
                        .pathLength(currentNode.path.size() - 1)
                        .pathFound(true)
                        .build();

                    log.info("✨ Nueva mejor solución encontrada: costo = {}, longitud = {}",
                            bestSolution.getTotalCost(), bestSolution.getPathLength());
                }
                continue;
            }

            // PODA 1: Ya visitamos este nodo con menor costo
            if (visited.containsKey(currentNode.wallet) &&
                visited.get(currentNode.wallet) <= currentNode.costSoFar) {
                metrics.branchesPruned++;
                continue;
            }
            visited.put(currentNode.wallet, currentNode.costSoFar);

            // PODA 2: Costo excede el máximo permitido
            if (currentNode.costSoFar > maxCost) {
                metrics.branchesPruned++;
                continue;
            }

            // PODA 3: No puede mejorar la mejor solución
            if (currentNode.costSoFar + currentNode.heuristic >= bestSolution.getTotalCost()) {
                metrics.branchesPruned++;
                continue;
            }

            // PODA 4: Camino muy largo (evitar exploración infinita)
            if (currentNode.path.size() > 15) {
                metrics.branchesPruned++;
                continue;
            }

            // RAMIFICACIÓN: Explorar vecinos
            List<Edge> neighbors = graph.getOrDefault(currentNode.wallet, Collections.emptyList());

            for (Edge edge : neighbors) {
                String nextWallet = edge.to;
                double edgeCost = edge.cost;

                // Evitar ciclos en el camino actual
                if (currentNode.path.contains(nextWallet)) {
                    continue;
                }

                // Crear nuevo nodo (rama)
                List<String> newPath = new ArrayList<>(currentNode.path);
                newPath.add(nextWallet);

                double newCost = currentNode.costSoFar + edgeCost;
                double heuristic = estimateHeuristic(nextWallet, targetWallet, graph);

                Node newNode = new Node(
                    nextWallet,
                    newPath,
                    newCost,
                    heuristic
                );

                // Agregar a la cola de prioridad
                priorityQueue.offer(newNode);
                metrics.branchesCreated++;
            }
        }

        long executionTime = System.currentTimeMillis() - startTime;

        log.info("BRANCH & BOUND completado en {}ms", executionTime);
        log.info("Estadísticas: {} nodos explorados, {} ramas creadas, {} ramas podadas",
                metrics.nodesExplored, metrics.branchesCreated, metrics.branchesPruned);

        // Agregar métricas al resultado
        bestSolution.setNodesExplored(metrics.nodesExplored);
        bestSolution.setBranchesPruned(metrics.branchesPruned);
        bestSolution.setExecutionTimeMs(executionTime);

        return bestSolution;
    }

    /**
     * Heurística para estimar el costo restante al objetivo
     *
     * Usamos una heurística admisible (nunca sobrestima) para garantizar optimalidad
     * En este caso: 0 (heurística nula) o costo promedio de aristas
     */
    private double estimateHeuristic(
            String current,
            String target,
            Map<String, List<Edge>> graph) {

        // Heurística simple: 0 (equivalente a Dijkstra)
        // Podrías mejorarla con distancia en el grafo o costo promedio
        return 0.0;

        // Heurística alternativa (descomenta para usar):
        // return calculateAverageCost(graph) * estimateHops(current, target);
    }

    // CLASES AUXILIARES

    /**
     * Nodo en el árbol de búsqueda Branch & Bound
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Node {
        private String wallet;
        private List<String> path;
        private double costSoFar;      // Costo acumulado desde el origen
        private double heuristic;      // Estimación al objetivo

        // Costo total estimado = costSoFar + heuristic
        public double getTotalEstimatedCost() {
            return costSoFar + heuristic;
        }
    }

    /**
     * Representa una arista con costo (fees)
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Edge {
        private String to;
        private double amount;      // Monto transferido
        private double cost;        // Costo (fees)
        private String txHash;
        private long timestamp;
    }

    /**
     * Resultado del algoritmo Branch & Bound
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptimalPathResult {
        private String sourceWallet;
        private String targetWallet;
        private List<String> path;
        private double totalCost;
        private int pathLength;
        private boolean pathFound;

        // Métricas de exploración
        private int nodesExplored;
        private int branchesPruned;
        private long executionTimeMs;

        /**
         * Detalles del camino para visualización
         */
        public List<PathStep> getPathDetails(Map<String, List<Edge>> graph) {
            if (!pathFound || path == null || path.size() < 2) {
                return Collections.emptyList();
            }

            List<PathStep> steps = new ArrayList<>();

            for (int i = 0; i < path.size() - 1; i++) {
                String from = path.get(i);
                String to = path.get(i + 1);

                // Buscar la arista correspondiente
                List<Edge> edges = graph.getOrDefault(from, Collections.emptyList());
                for (Edge edge : edges) {
                    if (edge.to.equals(to)) {
                        steps.add(PathStep.builder()
                            .from(from)
                            .to(to)
                            .amount(edge.amount)
                            .cost(edge.cost)
                            .txHash(edge.txHash)
                            .hopNumber(i)
                            .build());
                        break;
                    }
                }
            }

            return steps;
        }
    }

    /**
     * Paso individual en el camino
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PathStep {
        private String from;
        private String to;
        private double amount;
        private double cost;
        private String txHash;
        private int hopNumber;
    }

    /**
     * Métricas de ejecución
     */
    @Data
    public static class BranchBoundMetrics {
        private int nodesExplored = 0;
        private int branchesCreated = 0;
        private int branchesPruned = 0;
    }
}
