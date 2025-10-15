package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Resultado del análisis de red de una wallet
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkAnalysisResult {
    
    private String address;
    
    private Long balance;
    
    private Integer totalTransactions;
    
    private Long totalReceived;
    
    private Long totalSent;
    
    private Integer directConnections;
    
    private List<ConnectedWallet> connectedWallets;
    
    private List<TransactionSummary> recentTransactions;
    
    private String riskLevel;
    
    private List<String> tags;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConnectedWallet {
        private String address;
        private Long totalTransferred;
        private Integer transactionCount;
        private String direction; // SENT, RECEIVED, BOTH
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransactionSummary {
        private String hash;
        private Long amount;
        private String type; // INPUT, OUTPUT
        private String timestamp;
    }
}
