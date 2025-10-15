package com.example.controller;

import com.example.dto.NetworkAnalysisResult;
import com.example.service.NetworkAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para análisis de red de wallets
 */
@RestController
@RequestMapping("/api/network")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NetworkAnalysisController {
    
    private final NetworkAnalysisService networkAnalysisService;
    
    /**
     * Analiza la red completa de una wallet
     * GET /api/network/analyze/{address}
     */
    @GetMapping("/analyze/{address}")
    public ResponseEntity<NetworkAnalysisResult> analyzeNetwork(@PathVariable String address) {
        log.info("REST: Analyzing network for wallet: {}", address);
        
        try {
            NetworkAnalysisResult result = networkAnalysisService.analyzeWalletNetwork(address);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error analyzing network: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtiene estadísticas de una wallet
     * GET /api/network/statistics/{address}
     */
    @GetMapping("/statistics/{address}")
    public ResponseEntity<Map<String, Object>> getWalletStatistics(@PathVariable String address) {
        log.info("REST: Getting statistics for wallet: {}", address);
        
        try {
            Map<String, Object> stats = networkAnalysisService.getWalletStatistics(address);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting statistics: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Encuentra transacciones grandes
     * GET /api/network/large-transactions?minAmount=1000000&limit=50
     */
    @GetMapping("/large-transactions")
    public ResponseEntity<List<Map<String, Object>>> findLargeTransactions(
            @RequestParam(defaultValue = "1000000") long minAmount,
            @RequestParam(defaultValue = "50") int limit) {
        
        log.info("REST: Finding large transactions with min amount: {}", minAmount);
        
        try {
            List<Map<String, Object>> transactions = networkAnalysisService.findLargeTransactions(minAmount, limit);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Error finding large transactions: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
