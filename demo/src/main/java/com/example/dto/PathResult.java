package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Resultado del análisis de camino entre dos wallets
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PathResult {
    
    private String fromAddress;
    
    private String toAddress;
    
    private Integer pathLength;
    
    private Boolean connectionFound;
    
    private List<PathNode> path;
    
    private Long totalAmountTransferred;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PathNode {
        private String address;
        private String transactionHash;
        private Long amount;
        private Integer stepNumber;
        private String nodeType; // WALLET, TRANSACTION
    }
}
