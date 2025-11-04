package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Resultado de análisis Dynamic Programming - Max Flow Path
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaxFlowPathResult {
    
    private String sourceWallet;
    private String targetWallet;
    private Double maxFlowValue;
    private Integer pathLength;
    private List<PathStep> path;
    private Boolean foundPath;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PathStep {
        private String from;
        private String to;
        private Double amount;
        private String transactionHash;
        private LocalDateTime timestamp;
        private Integer hopNumber;
    }
}

