package com.example.controller;

import com.example.algorithm.AlgorithmRequest;
import com.example.dto.*;
import com.example.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para algoritmos de análisis forense
 */
@RestController
@RequestMapping("/api/algorithms")
@RequiredArgsConstructor
@Slf4j
public class AlgorithmController {

    private final GreedyAlgorithmService greedyService;
    private final DynamicProgrammingService dpService;
    private final GraphAlgorithmsService graphService;
    private final PatternMatchingService patternService;

    // ============== GREEDY ALGORITHMS ==============

    /**
     * ENDPOINT 1: Análisis Greedy de Peel Chains
     *
     * POST /api/algorithms/greedy/peel-chains
     *
     * Complejidad: O(n log n)
     * Descripción: Detecta patrones de peel chain usando selección greedy
     *
     * Request:
     * {
     *   "threshold": 0.95,
     *   "limit": 50,
     *   "sortBy": "spendingPercentage"
     * }
     */
    @PostMapping("/greedy/peel-chains")
    public ResponseEntity<Map<String, Object>> analyzePeelChainsGreedy(
            @RequestBody AlgorithmRequest request) {

        log.info("Received greedy peel-chains analysis request");

        try {
            Double threshold = request.getThreshold() != null ? request.getThreshold() : 0.95;
            Integer limit = request.getLimit() != null ? request.getLimit() : 50;

            // Validar inputs
            if (threshold < 0 || threshold > 1) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Threshold must be between 0 and 1"));
            }

            List<PeelChainGreedyResult> results = greedyService.analyzePeelChainsGreedy(threshold, limit);

            Map<String, Object> response = new HashMap<>();
            response.put("algorithm", "GREEDY_PEEL_CHAINS");
            response.put("complexity", "O(n log n)");
            response.put("results", results);
            response.put("resultCount", results.size());
            response.put("threshold", threshold);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in greedy peel-chains analysis", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    /**
     * Endpoint auxiliar para análisis avanzado de peel chain clusters
     */
    @PostMapping("/greedy/peel-chain-clusters")
    public ResponseEntity<Map<String, Object>> analyzePeelChainClusters(
            @RequestBody AlgorithmRequest request) {

        log.info("Received greedy peel-chain clusters analysis request");

        try {
            Double threshold = request.getThreshold() != null ? request.getThreshold() : 0.95;
            Integer minChainLength = request.getLimit() != null ? request.getLimit() : 3;
            Integer limit = 50;

            List<PeelChainGreedyResult> results = greedyService.analyzePeelChainClusters(
                    threshold, minChainLength, limit);

            Map<String, Object> response = new HashMap<>();
            response.put("algorithm", "GREEDY_PEEL_CHAIN_CLUSTERS");
            response.put("complexity", "O(n log n)");
            response.put("results", results);
            response.put("resultCount", results.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in greedy peel-chain clusters analysis", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ============== DYNAMIC PROGRAMMING ==============

    /**
     * ENDPOINT 2: Dynamic Programming - Maximum Flow Path
     *
     * POST /api/algorithms/dp/max-flow-path
     *
     * Complejidad: O(V + E)
     * Descripción: Encuentra el camino que maximiza el valor de fondos transferidos
     *
     * Request:
     * {
     *   "sourceWallet": "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
     *   "targetWallet": "1dice8EMCQAqQSN7ufuN6Ent9Qc1q5W9z",
     *   "maxHops": 10
     * }
     */
    @PostMapping("/dp/max-flow-path")
    public ResponseEntity<Map<String, Object>> findMaxFlowPath(
            @RequestBody AlgorithmRequest request) {

        log.info("Received DP max-flow-path analysis request");

        try {
            // Validar inputs
            if (request.getSourceWallet() == null || request.getSourceWallet().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Source wallet is required"));
            }

            if (request.getTargetWallet() == null || request.getTargetWallet().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Target wallet is required"));
            }

            Integer maxHops = request.getMaxHops() != null ? request.getMaxHops() : 10;

            if (maxHops < 1 || maxHops > 100) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "MaxHops must be between 1 and 100"));
            }

            MaxFlowPathResult result = dpService.findMaxFlowPath(
                    request.getSourceWallet(),
                    request.getTargetWallet(),
                    maxHops);

            Map<String, Object> response = new HashMap<>();
            response.put("algorithm", "DYNAMIC_PROGRAMMING_MAX_FLOW");
            response.put("complexity", "O(V + E)");
            response.put("sourceWallet", result.getSourceWallet());
            response.put("targetWallet", result.getTargetWallet());
            response.put("maxFlowValue", result.getMaxFlowValue());
            response.put("pathLength", result.getPathLength());
            response.put("path", result.getPath());
            response.put("foundPath", result.getFoundPath());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in DP max-flow-path analysis", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ============== GRAPH ALGORITHMS ==============

    /**
     * ENDPOINT 3: Análisis de Centralidad
     *
     * GET /api/algorithms/graph/centrality?topN=10
     *
     * Complejidad: O(V·E)
     * Descripción: Calcula Betweenness Centrality para identificar wallets puente
     */
    @GetMapping("/graph/centrality")
    public ResponseEntity<Map<String, Object>> calculateCentrality(
            @RequestParam(defaultValue = "10") Integer topN) {

        log.info("Received centrality analysis request for top {} wallets", topN);

        try {
            if (topN < 1 || topN > 1000) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "topN must be between 1 and 1000"));
            }

            List<CentralityResult> results = graphService.calculateBetweennessCentrality(topN);

            Map<String, Object> response = new HashMap<>();
            response.put("algorithm", "BETWEENNESS_CENTRALITY");
            response.put("complexity", "O(V·E)");
            response.put("topCentralWallets", results);
            response.put("resultCount", results.size());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in centrality analysis", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ENDPOINT 4: Detección de Comunidades
     *
     * GET /api/algorithms/graph/communities?minSize=3
     *
     * Complejidad: O(V log V + E)
     * Descripción: Detecta clusters de wallets (comunidades)
     */
    @GetMapping("/graph/communities")
    public ResponseEntity<Map<String, Object>> detectCommunities(
            @RequestParam(defaultValue = "3") Integer minSize) {

        log.info("Received community detection request with minSize: {}", minSize);

        try {
            if (minSize < 1 || minSize > 1000) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "minSize must be between 1 and 1000"));
            }

            List<CommunityResult> communities = graphService.detectCommunities(minSize);

            Map<String, Object> response = new HashMap<>();
            response.put("algorithm", "COMMUNITY_DETECTION");
            response.put("complexity", "O(V log V + E)");
            response.put("communities", communities);
            response.put("totalCommunities", communities.size());
            response.put("timestamp", System.currentTimeMillis());

            // Estadísticas agregadas
            double avgDensity = communities.stream()
                    .mapToDouble(CommunityResult::getDensity)
                    .average()
                    .orElse(0);
            long totalVolume = communities.stream()
                    .mapToLong(CommunityResult::getTotalVolume)
                    .sum();

            response.put("statistics", Map.of(
                    "averageDensity", avgDensity,
                    "totalVolume", totalVolume,
                    "averageCommunitySize", communities.stream()
                            .mapToInt(CommunityResult::getSize)
                            .average()
                            .orElse(0)
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in community detection", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint auxiliar: Node Importance Analysis
     */
    @GetMapping("/graph/node-importance")
    public ResponseEntity<Map<String, Object>> analyzeNodeImportance(
            @RequestParam(defaultValue = "20") Integer topN) {

        log.info("Received node importance analysis request");

        try {
            List<CentralityResult> results = graphService.analyzeNodeImportance(topN);

            Map<String, Object> response = new HashMap<>();
            response.put("algorithm", "NODE_IMPORTANCE");
            response.put("complexity", "O(V log V)");
            response.put("results", results);
            response.put("resultCount", results.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in node importance analysis", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ============== PATTERN MATCHING ==============

    /**
     * ENDPOINT 5: Detección de Patrones de Lavado
     *
     * POST /api/algorithms/pattern/detect-anomalies
     *
     * Complejidad: O(n) a O(n²) según patrón
     * Descripción: Detecta múltiples patrones de lavado de dinero
     *
     * Request:
     * {
     *   "analysisDepth": 5,
     *   "timeWindowDays": 30,
     *   "anomalyThreshold": 2.5,
     *   "patterns": ["MIXING", "CYCLICAL", "RAPID", "ANOMALY"]
     * }
     */
    @PostMapping("/pattern/detect-anomalies")
    public ResponseEntity<Map<String, Object>> detectAnomalies(
            @RequestBody AlgorithmRequest request) {

        log.info("Received pattern detection analysis request");

        try {
            Integer depth = request.getAnalysisDepth() != null ? request.getAnalysisDepth() : 5;
            Integer timeWindow = request.getTimeWindowDays() != null ? request.getTimeWindowDays() : 30;
            Double threshold = request.getAnomalyThreshold() != null ? request.getAnomalyThreshold() : 2.5;

            // Validaciones
            if (depth < 1 || depth > 50) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "analysisDepth must be between 1 and 50"));
            }

            List<PatternDetectionResult> patterns = patternService.detectAnomalyPatterns(
                    depth, timeWindow, threshold, request.getPatterns());

            // Agrupar por tipo de patrón
            Map<String, List<PatternDetectionResult>> patternsByType = patterns.stream()
                    .collect(Collectors.groupingBy(PatternDetectionResult::getPatternType));

            Map<String, Object> response = new HashMap<>();
            response.put("algorithm", "PATTERN_MATCHING");
            response.put("complexity", "O(n) to O(n²)");
            response.put("detectedPatterns", patterns);
            response.put("totalAnomalies", patterns.size());
            response.put("patternBreakdown", patternsByType.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().size()
                    )));
            response.put("timestamp", System.currentTimeMillis());

            // Estadísticas
            double avgConfidence = patterns.stream()
                    .mapToDouble(PatternDetectionResult::getConfidence)
                    .average()
                    .orElse(0);
            long criticalCount = patterns.stream()
                    .filter(p -> "CRITICAL".equals(p.getSeverity()))
                    .count();

            response.put("statistics", Map.of(
                    "averageConfidence", avgConfidence,
                    "criticalPatterns", criticalCount
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in pattern detection", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ============== HEALTH CHECK ==============

    /**
     * Health check del módulo de algoritmos
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "module", "Algorithms",
                "version", "1.0",
                "algorithms", List.of(
                        "GREEDY_PEEL_CHAINS",
                        "DYNAMIC_PROGRAMMING_MAX_FLOW",
                        "BETWEENNESS_CENTRALITY",
                        "COMMUNITY_DETECTION",
                        "PATTERN_MATCHING"
                )
        ));
    }
}
