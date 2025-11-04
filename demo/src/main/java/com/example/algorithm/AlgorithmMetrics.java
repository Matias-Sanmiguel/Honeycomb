package com.example.algorithm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Métricas de ejecución de algoritmos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlgorithmMetrics {
    
    private String algorithm;
    private String complexity;
    private Long executionTimeMs;
    private Integer nodesProcessed;
    private Integer edgesProcessed;
    private Double precision;
    private Double recall;
    private Long memoryUsedMb;
    
    public static AlgorithmMetrics empty(String algorithm, String complexity) {
        return AlgorithmMetrics.builder()
                .algorithm(algorithm)
                .complexity(complexity)
                .executionTimeMs(0L)
                .nodesProcessed(0)
                .edgesProcessed(0)
                .build();
    }
}

