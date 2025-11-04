package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Resultado de detección de patrones de lavado
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatternDetectionResult {
    
    private String patternType; // MIXING, CYCLICAL, RAPID, ANOMALY
    private Double confidence;
    private List<String> affectedWallets;
    private String description;
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private Long detectedAt;
    private Object patternDetails;
    
    // Para ANOMALY
    private Double anomalyScore;
    private Double standardDeviations;
    
    // Para MIXING
    private Integer inputCount;
    private Integer outputCount;
    private Long totalAmount;
    
    // Para CYCLICAL
    private List<String> cycle;
    private Integer cycleLength;
    
    // Para RAPID
    private Integer transactionCount;
    private Long timeWindowSeconds;
}
package com.example.algorithm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para requests de algoritmos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlgorithmRequest {
    
    // Greedy
    private Double threshold;
    private Integer limit;
    private String sortBy;
    
    // Dynamic Programming
    private String sourceWallet;
    private String targetWallet;
    private Integer maxHops;
    
    // Graph Algorithms
    private Integer topN;
    private Integer minClusterSize;
    
    // Pattern Matching
    private Integer analysisDepth;
    private Integer timeWindowDays;
    private Double anomalyThreshold;
    private List<String> patterns;
}

