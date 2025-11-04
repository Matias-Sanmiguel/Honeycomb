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

