package com.example.algorithm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * ALGORITMO BACKTRACKING: Detección de Cadenas de Lavado y Ciclos
 *
 * <h2>Descripción Académica</h2>
 * Backtracking es una técnica de búsqueda exhaustiva que explora TODAS las
 * soluciones posibles, retrocediendo cuando encuentra un callejón sin salida.
 *
 * <h2>Aplicación en Análisis Forense</h2>
 * - Detectar cadenas complejas de lavado de dinero
 * - Encontrar ciclos (A→B→C→A) que ocultan el origen de fondos
 * - Identificar patrones sospechosos que requieren exploración exhaustiva
 *
 * <h2>Complejidad</h2>
 * <ul>
 *   <li><b>Temporal:</b> O(b^d) donde b=branching factor, d=profundidad máxima</li>
 *   <li><b>Espacial:</b> O(d) para la pila de recursión</li>
 *   <li><b>Peor caso:</b> Exploración completa del grafo - O(V + E)</li>
 * </ul>
 *
 * <h2>Estrategia</h2>
 * <pre>
 * function BACKTRACK(wallet, path, depth):
 *     if depth == 0 OR es_ciclo(path):
 *         registrar_patron(path)
 *         return
 *
 *     for each vecino in obtener_vecinos(wallet):
 *         if no_visitado_recientemente(vecino):
 *             path.add(vecino)
 *             BACKTRACK(vecino, path, depth-1)  // RECURSIÓN
 *             path.remove(vecino)               // BACKTRACK!
 * </pre>
 *
 * <h2>Ejemplo de Uso</h2>
 * <pre>
 * BacktrackingAlgorithm bt = new BacktrackingAlgorithm();
 * List&lt;SuspiciousChain&gt; chains = bt.findSuspiciousChains(
 *     graph,
 *     "wallet123",
 *     5  // profundidad máxima
 * );
 *
 * // Output:
 * // [
 * //   {path: [A→B→C→D], type: LINEAR_CHAIN, suspicionLevel: 0.85},
 * //   {path: [A→B→C→A], type: CYCLE, suspicionLevel: 0.95}
 * // ]
 * </pre>
 *
 * @author Crypto Forensic Team
 * @version 1.0
 */
@Slf4j
public class BacktrackingAlgorithm {

    /**
     * Encuentra cadenas sospechosas usando backtracking
     *
     * @param graph Grafo de transacciones (adjacency list)
     * @param startWallet Wallet desde donde iniciar la búsqueda
     * @param maxDepth Profundidad máxima de exploración
     * @return Lista de cadenas sospechosas detectadas
     */
    public List<SuspiciousChain> findSuspiciousChains(
            Map<String, List<Edge>> graph,
            String startWallet,
            int maxDepth) {

        long startTime = System.currentTimeMillis();
        log.info("Iniciando BACKTRACKING desde wallet: {} con profundidad: {}",
                startWallet, maxDepth);

        List<SuspiciousChain> suspiciousChains = new ArrayList<>();
        List<String> currentPath = new ArrayList<>();
        Set<String> visitedInPath = new HashSet<>();

        // Estadísticas de exploración
        BacktrackingMetrics metrics = new BacktrackingMetrics();

        // INICIO DEL BACKTRACKING
        currentPath.add(startWallet);
        visitedInPath.add(startWallet);

        backtrack(
            graph,
            startWallet,
            currentPath,
            visitedInPath,
            maxDepth,
            suspiciousChains,
            metrics
        );

        long executionTime = System.currentTimeMillis() - startTime;

        log.info("BACKTRACKING completado en {}ms", executionTime);
        log.info("Estadísticas: {} paths explorados, {} backtracks, {} ciclos detectados",
                metrics.pathsExplored, metrics.backtrackCount, metrics.cyclesDetected);

        // Ordenar por nivel de sospecha
        suspiciousChains.sort((a, b) ->
            Double.compare(b.suspicionLevel, a.suspicionLevel));

        return suspiciousChains;
    }

    /**
     * FUNCIÓN RECURSIVA DE BACKTRACKING
     *
     * Esta es la implementación core del algoritmo
     */
    private void backtrack(
            Map<String, List<Edge>> graph,
            String currentWallet,
            List<String> currentPath,
            Set<String> visitedInPath,
            int remainingDepth,
            List<SuspiciousChain> results,
            BacktrackingMetrics metrics) {

        metrics.pathsExplored++;

        // CASO BASE 1: Profundidad máxima alcanzada
        if (remainingDepth == 0) {
            analyzePath(currentPath, results, metrics, "MAX_DEPTH_REACHED");
            return;
        }

        // CASO BASE 2: No hay más vecinos (callejón sin salida)
        List<Edge> neighbors = graph.getOrDefault(currentWallet, Collections.emptyList());
        if (neighbors.isEmpty()) {
            analyzePath(currentPath, results, metrics, "DEAD_END");
            return;
        }

        // EXPLORACIÓN RECURSIVA
        for (Edge edge : neighbors) {
            String nextWallet = edge.to;

            // DETECCIÓN DE CICLO - ¡Patrón altamente sospechoso!
            if (visitedInPath.contains(nextWallet)) {
                metrics.cyclesDetected++;

                // Crear ciclo desde la primera aparición
                int cycleStartIndex = currentPath.indexOf(nextWallet);
                List<String> cyclePath = new ArrayList<>(
                    currentPath.subList(cycleStartIndex, currentPath.size())
                );
                cyclePath.add(nextWallet); // Cerrar el ciclo

                results.add(SuspiciousChain.builder()
                    .path(cyclePath)
                    .type(ChainType.CYCLE)
                    .suspicionLevel(0.95) // Ciclos son MUY sospechosos
                    .totalAmount(calculatePathAmount(cyclePath, graph))
                    .depth(currentPath.size() - cycleStartIndex)
                    .description("Ciclo detectado: fondos retornan al origen")
                    .build());

                continue; // No explorar ciclos (evitar bucle infinito)
            }

            // PODA: Evitar caminos muy largos sin sentido
            if (currentPath.size() > 20) {
                continue;
            }

            // AGREGAR AL CAMINO
            currentPath.add(nextWallet);
            visitedInPath.add(nextWallet);

            // LLAMADA RECURSIVA (el corazón del backtracking)
            backtrack(
                graph,
                nextWallet,
                currentPath,
                visitedInPath,
                remainingDepth - 1,
                results,
                metrics
            );

            // ⬅BACKTRACK: DESHACER la decisión (retroceder)
            currentPath.remove(currentPath.size() - 1);
            visitedInPath.remove(nextWallet);
            metrics.backtrackCount++;
        }

        // Si llegamos aquí y el camino es interesante, guardarlo
        if (currentPath.size() >= 3) {
            analyzePath(currentPath, results, metrics, "EXPLORED_FULLY");
        }
    }

    /**
     * Analiza un camino para determinar si es sospechoso
     */
    private void analyzePath(
            List<String> path,
            List<SuspiciousChain> results,
            BacktrackingMetrics metrics,
            String reason) {

        if (path.size() < 3) return; // Caminos muy cortos no son interesantes

        // Calcular nivel de sospecha basado en patrones
        double suspicionLevel = 0.0;
        ChainType type = ChainType.LINEAR_CHAIN;

        // PATRÓN 1: Cadena larga (peel chain potencial)
        if (path.size() >= 5) {
            suspicionLevel += 0.3;
            type = ChainType.PEEL_CHAIN;
        }

        // PATRÓN 2: Redistribución rápida
        if (path.size() >= 4 && path.size() <= 6) {
            suspicionLevel += 0.2;
            type = ChainType.RAPID_REDISTRIBUTION;
        }

        // PATRÓN 3: Caminos moderadamente largos
        if (path.size() >= 3) {
            suspicionLevel += 0.1 * path.size();
        }

        // Solo guardar si tiene nivel de sospecha significativo
        if (suspicionLevel >= 0.3) {
            results.add(SuspiciousChain.builder()
                .path(new ArrayList<>(path))
                .type(type)
                .suspicionLevel(Math.min(suspicionLevel, 1.0))
                .depth(path.size())
                .description("Cadena sospechosa detectada: " + reason)
                .build());
        }
    }

    /**
     * Calcula el monto total transferido en un camino
     */
    private double calculatePathAmount(List<String> path, Map<String, List<Edge>> graph) {
        double total = 0.0;

        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);

            List<Edge> edges = graph.getOrDefault(from, Collections.emptyList());
            for (Edge edge : edges) {
                if (edge.to.equals(to)) {
                    total += edge.amount;
                    break;
                }
            }
        }

        return total;
    }

    // CLASES AUXILIARES

    /**
     * Representa una arista en el grafo de transacciones
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Edge {
        private String to;
        private double amount;
        private String txHash;
        private long timestamp;
    }

    /**
     * Resultado: Cadena sospechosa detectada
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuspiciousChain {
        private List<String> path;
        private ChainType type;
        private double suspicionLevel;
        private double totalAmount;
        private int depth;
        private String description;
    }

    /**
     * Tipos de cadenas detectadas
     */
    public enum ChainType {
        CYCLE,                    // A→B→C→A
        PEEL_CHAIN,              // Cadena larga de transferencias
        RAPID_REDISTRIBUTION,    // Redistribución rápida
        LINEAR_CHAIN,            // Cadena simple
        MIXING_PATTERN           // Patrón de mezcla
    }

    /**
     * Métricas de ejecución del algoritmo
     */
    @Data
    public static class BacktrackingMetrics {
        private int pathsExplored = 0;
        private int backtrackCount = 0;
        private int cyclesDetected = 0;
    }
}

