package com.example.controller;

import com.example.dto.NetworkAnalysisResult;
import com.example.dto.PeelChainResult;
import com.example.dto.PathResult;
import com.example.service.ForensicAnalysisService;
import com.example.service.NetworkAnalysisService;
import com.example.service.PathAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller principal para análisis forense de blockchain
 */
@RestController
@RequestMapping("/api/forensic")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ForensicController {
    
    private final NetworkAnalysisService networkAnalysisService;
    private final ForensicAnalysisService forensicAnalysisService;
    private final PathAnalysisService pathAnalysisService;
    
    /**
     * Endpoint 1: Análisis de red de una wallet
     * GET /api/forensic/network/{address}
     */
    @GetMapping("/network/{address}")
    public ResponseEntity<NetworkAnalysisResult> analyzeNetwork(@PathVariable String address) {
        log.info("Network analysis request for: {}", address);
        NetworkAnalysisResult result = networkAnalysisService.analyzeWalletNetwork(address);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Endpoint 2: Detección de Peel Chains
     * GET /api/forensic/peel-chains
     */
    @GetMapping("/peel-chains")
    public ResponseEntity<List<PeelChainResult>> detectPeelChains() {
        log.info("Peel chain detection request");
        List<PeelChainResult> results = forensicAnalysisService.detectPeelChains();
        return ResponseEntity.ok(results);
    }
    
    /**
     * Detección de peel chains con threshold personalizado
     * GET /api/forensic/peel-chains/detailed?threshold=0.95&limit=100
     */
    @GetMapping("/peel-chains/detailed")
    public ResponseEntity<List<PeelChainResult>> detectPeelChainsDetailed(
            @RequestParam(defaultValue = "0.95") double threshold,
            @RequestParam(defaultValue = "100") int limit) {
        log.info("Detailed peel chain detection with threshold: {}", threshold);
        List<PeelChainResult> results = forensicAnalysisService.detectPeelChainsDetailed(threshold, limit);
        return ResponseEntity.ok(results);
    }
    
    /**
     * Endpoint 3: Camino más corto entre dos wallets
     * GET /api/forensic/connection-path?from=xxx&to=yyy
     */
    @GetMapping("/connection-path")
    public ResponseEntity<PathResult> findConnectionPath(
            @RequestParam String from,
            @RequestParam String to) {
        log.info("Connection path request from {} to {}", from, to);
        PathResult result = pathAnalysisService.findConnectionPath(from, to);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Encuentra múltiples caminos cortos
     * GET /api/forensic/all-paths?from=xxx&to=yyy&maxLength=5
     */
    @GetMapping("/all-paths")
    public ResponseEntity<List<PathResult>> findAllPaths(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "5") int maxLength) {
        log.info("All paths request from {} to {} with max length {}", from, to, maxLength);
        List<PathResult> results = pathAnalysisService.findAllShortPaths(from, to, maxLength);
        return ResponseEntity.ok(results);
    }
    
    /**
     * Encuentra wallets cercanas (dentro de N saltos)
     * GET /api/forensic/nearby-wallets/{address}?hops=2
     */
    @GetMapping("/nearby-wallets/{address}")
    public ResponseEntity<List<Map<String, Object>>> findNearbyWallets(
            @PathVariable String address,
            @RequestParam(defaultValue = "2") int hops) {
        log.info("Finding wallets within {} hops of {}", hops, address);
        List<Map<String, Object>> results = pathAnalysisService.findWalletsWithinHops(address, hops);
        return ResponseEntity.ok(results);
    }
    
    /**
     * Encuentra las wallets más conectadas
     * GET /api/forensic/most-connected?minConnections=10&limit=20
     */
    @GetMapping("/most-connected")
    public ResponseEntity<List<Map<String, Object>>> findMostConnected(
            @RequestParam(defaultValue = "10") int minConnections,
            @RequestParam(defaultValue = "20") int limit) {
        log.info("Finding most connected wallets");
        List<Map<String, Object>> results = pathAnalysisService.findMostConnectedWallets(minConnections, limit);
        return ResponseEntity.ok(results);
    }
    
    /**
     * Análisis de patrones sospechosos general
     * GET /api/forensic/suspicious-patterns
     */
    @GetMapping("/suspicious-patterns")
    public ResponseEntity<Map<String, Object>> analyzeSuspiciousPatterns() {
        log.info("Analyzing suspicious patterns");
        Map<String, Object> results = forensicAnalysisService.analyzeSuspiciousPatterns();
        return ResponseEntity.ok(results);
    }
    
    /**
     * Reporte forense completo de una wallet
     * GET /api/forensic/report/{address}
     */
    @GetMapping("/report/{address}")
    public ResponseEntity<Map<String, Object>> generateForensicReport(@PathVariable String address) {
        log.info("Generating forensic report for: {}", address);
        Map<String, Object> report = forensicAnalysisService.generateForensicReport(address);
        return ResponseEntity.ok(report);
    }
    
    /**
     * Obtener estadísticas de una wallet
     * GET /api/forensic/statistics/{address}
     */
    @GetMapping("/statistics/{address}")
    public ResponseEntity<Map<String, Object>> getWalletStatistics(@PathVariable String address) {
        log.info("Getting statistics for: {}", address);
        Map<String, Object> stats = networkAnalysisService.getWalletStatistics(address);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Buscar transacciones grandes
     * GET /api/forensic/large-transactions?minAmount=1000000&limit=50
     */
    @GetMapping("/large-transactions")
    public ResponseEntity<List<Map<String, Object>>> findLargeTransactions(
            @RequestParam(defaultValue = "1000000") long minAmount,
            @RequestParam(defaultValue = "50") int limit) {
        log.info("Finding large transactions with min amount: {}", minAmount);
        List<Map<String, Object>> results = networkAnalysisService.findLargeTransactions(minAmount, limit);
        return ResponseEntity.ok(results);
    }
}
