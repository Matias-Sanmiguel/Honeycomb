package com.example.service;

import com.example.dto.CentralityResult;
import com.example.dto.CommunityResult;
import com.example.repository.AlgorithmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio implementando algoritmos Graph-based
 * Complejidad: O(V·E) para centralidad, O(V log V + E) para clustering
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GraphAlgorithmsService {

    private final AlgorithmRepository algorithmRepository;

    /**
     * ALGORITMO: Betweenness Centrality
     *
     * Concepto: Identifica wallets que actúan como PUENTES críticos en la red
     * Una wallet con alta centralidad es intermediaria en muchos caminos
     *
     * Formulación:
     * Betweenness(v) = Σ (σ(s,t|v) / σ(s,t))
     * donde:
     *   σ(s,t) = número de caminos cortos de s a t
     *   σ(s,t|v) = número de caminos que pasan por v
     *
     * Complejidad: O(V·E) utilizando algoritmo de Brandes
     * Interpretación: Wallets con alta centralidad pueden ser puntos de mezcla (mixers)
     */
    public List<CentralityResult> calculateBetweennessCentrality(Integer topN) {
        long startTime = System.currentTimeMillis();
        log.info("Calculating Betweenness Centrality for top {} wallets", topN);

        try {
            // Obtener datos de Neo4j (usando APOC si está disponible)
            List<Map<String, Object>> centralityData = algorithmRepository.calculateBetweennessCentrality(topN);

            List<CentralityResult> results = centralityData.stream()
                    .map(data -> CentralityResult.builder()
                            .wallet((String) data.get("wallet"))
                            .betweennessCentrality(((Number) data.get("betweenness")).doubleValue())
                            .closenessCentrality(data.get("closeness") != null ?
                                    ((Number) data.get("closeness")).doubleValue() : 0.0)
                            .degreeCentrality(data.get("degree") != null ?
                                    ((Number) data.get("degree")).doubleValue() : 0.0)
                            .bridgeConnections(data.get("bridgeConnections") != null ?
                                    ((Number) data.get("bridgeConnections")).intValue() : 0)
                            .totalTransactionVolume(data.get("volume") != null ?
                                    ((Number) data.get("volume")).longValue() : 0L)
                            .riskLevel(calculateRiskLevelByConnections(
                                    data.get("bridgeConnections") != null ?
                                            ((Number) data.get("bridgeConnections")).intValue() : 0))
                            .build())
                    .sorted(Comparator.comparingDouble(CentralityResult::getBetweennessCentrality).reversed())
                    .collect(Collectors.toList());

            // Asignar rankings después de crear la lista
            for (int i = 0; i < results.size(); i++) {
                results.get(i).setRank(i + 1);
            }

            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Centrality calculation completed in {}ms, found {} central wallets",
                    executionTime, results.size());

            return results;

        } catch (Exception e) {
            log.error("Error calculating betweenness centrality", e);
            return Collections.emptyList();
        }
    }

    /**
     * ALGORITMO: Community Detection (Louvain simplificado)
     *
     * Concepto: Encontrar CLUSTERS de wallets que interactúan frecuentemente
     * Útil para identificar grupos coordinados de lavadores de dinero
     *
     * Estrategia simplificada:
     * 1. Agrupar wallets por similitud de transacciones
     * 2. Calcular densidad de conexiones dentro del cluster
     * 3. Identificar comunidades con alta densidad interna
     *
     * Complejidad: O(V log V + E) para el algoritmo Louvain completo
     * Aquí usamos una versión simplificada O(V²) para clarity
     */
    public List<CommunityResult> detectCommunities(Integer minClusterSize) {
        long startTime = System.currentTimeMillis();
        log.info("Detecting communities with minClusterSize: {}", minClusterSize);

        try {
            List<Map<String, Object>> communityDataRaw = algorithmRepository.detectCommunities(minClusterSize);

            // Extraer los objetos "result" de los wrappers
            List<Map<String, Object>> communityData = new ArrayList<>();
            for (Map<String, Object> wrapper : communityDataRaw) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = wrapper.containsKey("result")
                    ? (Map<String, Object>) wrapper.get("result")
                    : wrapper;
                communityData.add(data);
            }

            List<CommunityResult> results = communityData.stream()
                    .map(data -> {
                        @SuppressWarnings("unchecked")
                        List<String> members = (List<String>) data.get("members");
                        Integer edgeCount = ((Number) data.get("edgeCount")).intValue();
                        Integer size = ((Number) data.get("size")).intValue();

                        // Calcular densidad: D = 2*E / (V*(V-1))
                        Double density = size > 1 ?
                                (2.0 * edgeCount) / (size * (size - 1.0)) : 0.0;

                        return CommunityResult.builder()
                                .communityId((String) data.get("communityId"))
                                .size(size)
                                .density(density)
                                .totalVolume(((Number) data.get("volume")).longValue())
                                .members(members)
                                .averageConnections(((Number) data.get("avgConnections")).doubleValue())
                                .edgeCount(edgeCount)
                                .suspiciousLevel(calculateCommunityRiskLevel(density, size))
                                .build();
                    })
                    .sorted(Comparator.comparingDouble(CommunityResult::getDensity).reversed())
                    .collect(Collectors.toList());

            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Community detection completed in {}ms, found {} communities",
                    executionTime, results.size());

            return results;

        } catch (Exception e) {
            log.error("Error detecting communities", e);
            return Collections.emptyList();
        }
    }

    /**
     * Análisis de importancia de nodos (página rank simplificado)
     * Identifica los nodos más importantes en la red
     */
    public List<CentralityResult> analyzeNodeImportance(Integer topN) {
        long startTime = System.currentTimeMillis();
        log.info("Analyzing node importance for top {} nodes", topN);

        try {
            List<Map<String, Object>> importanceDataRaw = algorithmRepository.calculateNodeImportance(topN);

            // Extraer los objetos "result" de los wrappers
            List<Map<String, Object>> importanceData = new ArrayList<>();
            for (Map<String, Object> wrapper : importanceDataRaw) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = wrapper.containsKey("result")
                    ? (Map<String, Object>) wrapper.get("result")
                    : wrapper;
                importanceData.add(data);
            }

            List<CentralityResult> results = importanceData.stream()
                    .map(data -> CentralityResult.builder()
                            .wallet((String) data.get("wallet"))
                            .degreeCentrality(((Number) data.get("pageRank")).doubleValue())
                            .bridgeConnections(((Number) data.get("inDegree")).intValue())
                            .totalTransactionVolume(((Number) data.get("volume")).longValue())
                            .build())
                    .sorted(Comparator.comparingDouble(CentralityResult::getDegreeCentrality).reversed())
                    .collect(Collectors.toList());

            // Asignar rankings después de crear la lista
            for (int i = 0; i < results.size(); i++) {
                results.get(i).setRank(i + 1);
            }

            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Node importance analysis completed in {}ms", executionTime);

            return results;

        } catch (Exception e) {
            log.error("Error analyzing node importance", e);
            return Collections.emptyList();
        }
    }

    /**
     * Calcular nivel de riesgo basado en número de conexiones puente
     */
    private String calculateRiskLevelByConnections(int connections) {
        if (connections >= 100) return "CRITICAL";
        if (connections >= 50) return "HIGH";
        if (connections >= 20) return "MEDIUM";
        return "LOW";
    }

    /**
     * Calcular nivel de sospecha de comunidad basado en densidad
     */
    private String calculateCommunityRiskLevel(Double density, Integer size) {
        if (density >= 0.8 && size >= 10) return "CRITICAL";
        if (density >= 0.6 && size >= 8) return "HIGH";
        if (density >= 0.4 && size >= 5) return "MEDIUM";
        return "LOW";
    }
}
