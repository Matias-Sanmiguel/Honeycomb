package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resultado de análisis Greedy de Peel Chains
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeelChainGreedyResult {
    
    private String wallet;
    private String transactionHash;
    private Double spendingPercentage;
    private Integer rank;
    private Integer chainLength;
    private Long totalAmount;
    private String riskLevel;
    private Long transactionCount;
    private String mainRecipient;
    private Long changeAmount;
    private String pattern;
    private Double confidence;
}
