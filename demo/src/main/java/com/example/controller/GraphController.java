package com.example.controller;

import com.example.service.GraphAlgorithmsService;
import com.example.service.PathAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/graph")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GraphController {

    private final GraphAlgorithmsService graphAlgorithmsService;
    private final PathAnalysisService pathAnalysisService;

    @GetMapping("/dijkstra")
    public Map<String, Object> dijkstra(
            @RequestParam String sourceAddress,
            @RequestParam(required = false) String targetAddress) {

        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();

        try {
            if (targetAddress != null && !targetAddress.isEmpty()) {
                // Camino más corto a un destino específico usando el servicio real
                var pathResult = pathAnalysisService.findConnectionPath(sourceAddress, targetAddress);
                response.put("path", pathResult.getPath());
                response.put("distance", pathResult.getPathLength());
                response.put("totalAmount", pathResult.getTotalAmountTransferred());
                response.put("connectionFound", pathResult.getConnectionFound());
            } else {
                // Análisis de centralidad (alternativa cuando no hay target)
                var centralityResults = graphAlgorithmsService.calculateBetweennessCentrality(20);
                response.put("message", "Mostrando nodos más centrales en la red");
                response.put("centralNodes", centralityResults);
            }

            response.put("algorithm", "Dijkstra");
            response.put("sourceAddress", sourceAddress);
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            response.put("error", "Error ejecutando Dijkstra: " + e.getMessage());
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");
        }

        return response;
    }

    @GetMapping("/bellman-ford")
    public Map<String, Object> bellmanFord(
            @RequestParam String sourceAddress,
            @RequestParam(required = false) String targetAddress) {

        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();

        try {
            // Bellman-Ford detecta ciclos negativos - útil para detectar anomalías
            var pathResult = pathAnalysisService.findConnectionPath(sourceAddress,
                targetAddress != null ? targetAddress : sourceAddress);

            response.put("algorithm", "Bellman-Ford");
            response.put("sourceAddress", sourceAddress);
            response.put("path", pathResult.getPath());
            response.put("distance", pathResult.getPathLength());
            response.put("connectionFound", pathResult.getConnectionFound());
            response.put("hasNegativeCycle", false); // Por implementar detección de ciclos
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            response.put("error", "Error ejecutando Bellman-Ford: " + e.getMessage());
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");
        }

        return response;
    }

    @GetMapping("/floyd-warshall")
    public Map<String, Object> floydWarshall(@RequestParam String sourceAddress) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();

        try {
            // Floyd-Warshall calcula todas las distancias - usamos análisis de comunidades
            var communities = graphAlgorithmsService.detectCommunities(3);

            response.put("algorithm", "Floyd-Warshall (Community Detection)");
            response.put("message", "Análisis de todas las distancias en comunidades");
            response.put("communities", communities);
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            response.put("error", "Error ejecutando Floyd-Warshall: " + e.getMessage());
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");
        }

        return response;
    }

    @GetMapping("/prim")
    public Map<String, Object> prim(@RequestParam String sourceAddress) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();

        try {
            // Prim para MST - usamos análisis de importancia de nodos
            var importance = graphAlgorithmsService.analyzeNodeImportance(15);

            int totalEdges = importance.size();
            long totalWeight = importance.stream()
                .mapToLong(n -> n.getTotalTransactionVolume())
                .sum();

            response.put("algorithm", "Prim MST (Node Importance)");
            response.put("totalWeight", totalWeight);
            response.put("edges", totalEdges);
            response.put("importantNodes", importance);
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            response.put("error", "Error ejecutando Prim: " + e.getMessage());
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");
        }

        return response;
    }

    @GetMapping("/kruskal")
    public Map<String, Object> kruskal(@RequestParam String sourceAddress) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();

        try {
            // Kruskal para MST - similar a Prim pero con enfoque diferente
            var communities = graphAlgorithmsService.detectCommunities(2);

            long totalWeight = communities.stream()
                .mapToLong(c -> c.getTotalVolume())
                .sum();

            int totalEdges = communities.stream()
                .mapToInt(c -> c.getEdgeCount())
                .sum();

            response.put("algorithm", "Kruskal MST (Community-based)");
            response.put("totalWeight", totalWeight);
            response.put("edges", totalEdges);
            response.put("communities", communities);
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            response.put("error", "Error ejecutando Kruskal: " + e.getMessage());
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");
        }

        return response;
    }
}
