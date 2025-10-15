package com.example.service;

import com.example.dto.PeelChainResult;
import com.example.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForensicAnalysisService {
    
    private final TransactionRepository transactionRepository;
    
    /**
     * Detecta transacciones Peel Chain (patrones de lavado de dinero)
     * Son transacciones donde se gasta >95% del input
     */
    public List<PeelChainResult> detectPeelChains() {
        log.info("Detecting peel chain patterns...");
        
        List<Map<String, Object>> results = transactionRepository.detectPeelChains();
        
        return results.stream()
                .map(this::mapToPeelChainResult)
                .collect(Collectors.toList());
    }
    
    /**
     * Detecta peel chains con información detallada
     */
    public List<PeelChainResult> detectPeelChainsDetailed(double threshold, int limit) {
        log.info("Detecting detailed peel chains with threshold: {}", threshold);
        
        List<Map<String, Object>> results = transactionRepository.detectPeelChainsDetailed(threshold, limit);
        
        return results.stream()
                .map(this::mapToDetailedPeelChainResult)
                .collect(Collectors.toList());
    }
    
    /**
     * Mapea los datos de Neo4j a PeelChainResult
     */
    private PeelChainResult mapToPeelChainResult(Map<String, Object> data) {
        PeelChainResult result = PeelChainResult.builder()
                .wallet((String) data.get("wallet"))
                .transaction((String) data.get("transaction"))
                .inputAmount(((Number) data.get("inputAmount")).longValue())
                .outputsTotal(((Number) data.get("outputsTotal")).longValue())
                .build();
        
        result.calculatePeelPercentage();
        return result;
    }
    
    /**
     * Mapea los datos detallados de peel chain
     */
    private PeelChainResult mapToDetailedPeelChainResult(Map<String, Object> data) {
        PeelChainResult result = PeelChainResult.builder()
                .wallet((String) data.get("wallet"))
                .transaction((String) data.get("transaction"))
                .inputAmount(((Number) data.get("inputAmount")).longValue())
                .outputsTotal(((Number) data.get("outputsTotal")).longValue())
                .mainRecipient((String) data.get("mainRecipient"))
                .mainRecipientAmount(
                        data.get("mainRecipientAmount") != null 
                                ? ((Number) data.get("mainRecipientAmount")).longValue() 
                                : null
                )
                .changeAddress((String) data.get("changeAddress"))
                .changeAmount(
                        data.get("changeAmount") != null 
                                ? ((Number) data.get("changeAmount")).longValue() 
                                : null
                )
                .build();
        
        result.calculatePeelPercentage();
        return result;
    }
    
    /**
     * Analiza patrones sospechosos en transacciones
     */
    public Map<String, Object> analyzeSuspiciousPatterns() {
        log.info("Analyzing suspicious transaction patterns...");
        
        // Detectar peel chains
        List<PeelChainResult> peelChains = detectPeelChains();
        
        // Detectar double spends
        var doubleSpends = transactionRepository.findDoubleSpendTransactions();
        
        return Map.of(
                "peelChains", peelChains.size(),
                "peelChainsDetected", peelChains,
                "doubleSpends", doubleSpends.size(),
                "timestamp", System.currentTimeMillis()
        );
    }
    
    /**
     * Genera un reporte forense completo
     */
    public Map<String, Object> generateForensicReport(String walletAddress) {
        log.info("Generating forensic report for wallet: {}", walletAddress);
        
        // Analizar transacciones relacionadas con la wallet
        var peelChains = detectPeelChainsDetailed(0.95, 100);
        
        // Filtrar las que involucran esta wallet
        var relatedPeelChains = peelChains.stream()
                .filter(pc -> pc.getWallet().equals(walletAddress) || 
                             (pc.getMainRecipient() != null && pc.getMainRecipient().equals(walletAddress)))
                .collect(Collectors.toList());
        
        return Map.of(
                "walletAddress", walletAddress,
                "peelChainsInvolved", relatedPeelChains.size(),
                "suspiciousTransactions", relatedPeelChains,
                "riskScore", calculateRiskScore(relatedPeelChains.size()),
                "timestamp", System.currentTimeMillis()
        );
    }
    
    /**
     * Calcula un score de riesgo basado en patrones detectados
     */
    private String calculateRiskScore(int suspiciousCount) {
        if (suspiciousCount == 0) return "LOW";
        if (suspiciousCount < 5) return "MEDIUM";
        return "HIGH";
    }
}
