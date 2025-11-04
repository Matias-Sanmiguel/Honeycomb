package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resultado de análisis de Centralidad
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CentralityResult {
    
    private String wallet;
    private Double betweennessCentrality;
    private Double closenessCentrality;
    private Double degreeCentrality;
    private Integer rank;
    private Integer bridgeConnections;
    private Long totalTransactionVolume;
    private String riskLevel;
}

