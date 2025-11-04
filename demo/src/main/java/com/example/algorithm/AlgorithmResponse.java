package com.example.algorithm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO genérico para responses de algoritmos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlgorithmResponse {
    
    private String algorithm;
    private String complexity;
    private Long executionTimeMs;
    
    // Para Greedy
    private List<Map<String, Object>> results;
    
    // Para Dynamic Programming
    private Double maxFlowValue;
    private List<Map<String, Object>> path;
    private Integer pathLength;
    
    // Para Graph Algorithms
    private List<Map<String, Object>> topCentralWallets;
    private List<Map<String, Object>> communities;
    private Integer totalCommunities;
    
    // Para Pattern Matching
    private List<Map<String, Object>> detectedPatterns;
    private Integer totalAnomalies;
    
    // Genérico
    private Object data;
    private String status;
    private String errorMessage;
}

