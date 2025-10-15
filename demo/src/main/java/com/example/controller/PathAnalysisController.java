package com.example.controller;

import com.example.dto.PathResult;
import com.example.service.PathAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para análisis de caminos entre wallets
 */
@RestController
@RequestMapping("/api/path")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PathAnalysisController {
    
    private final PathAnalysisService pathAnalysisService;
    
    /**
     * Encuentra el camino más corto entre dos wallets
     * GET /api/path/shortest?from=ADDRESS1&to=ADDRESS2
     */
    @GetMapping("/shortest")
    public ResponseEntity<PathResult> findShortestPath(
            @RequestParam String from,
            @RequestParam String to) {
        
        log.info("REST: Finding shortest path from {} to {}", from, to);
        
        try {
            PathResult result = pathAnalysisService.findConnectionPath(from, to);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error finding shortest path: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Encuentra múltiples caminos cortos entre dos wallets
     * GET /api/path/all?from=ADDRESS1&to=ADDRESS2&maxLength=5
     */
    @GetMapping("/all")
    public ResponseEntity<List<PathResult>> findAllShortPaths(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "5") int maxLength) {
        
        log.info("REST: Finding all paths from {} to {} with max length {}", from, to, maxLength);
        
        try {
            List<PathResult> results = pathAnalysisService.findAllShortPaths(from, to, maxLength);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error finding all paths: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Encuentra wallets dentro de N saltos de una wallet
     * GET /api/path/neighbors/{address}?hops=2
     */
    @GetMapping("/neighbors/{address}")
    public ResponseEntity<List<Map<String, Object>>> findWalletsWithinHops(
            @PathVariable String address,
            @RequestParam(defaultValue = "2") int hops) {
        
        log.info("REST: Finding wallets within {} hops of {}", hops, address);
        
        try {
            List<Map<String, Object>> wallets = pathAnalysisService.findWalletsWithinHops(address, hops);
            return ResponseEntity.ok(wallets);
        } catch (Exception e) {
            log.error("Error finding neighbors: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Encuentra las wallets más conectadas
     * GET /api/path/most-connected?minConnections=10&limit=50
     */
    @GetMapping("/most-connected")
    public ResponseEntity<List<Map<String, Object>>> findMostConnectedWallets(
            @RequestParam(defaultValue = "10") int minConnections,
            @RequestParam(defaultValue = "50") int limit) {
        
        log.info("REST: Finding most connected wallets");
        
        try {
            List<Map<String, Object>> wallets = pathAnalysisService.findMostConnectedWallets(minConnections, limit);
            return ResponseEntity.ok(wallets);
        } catch (Exception e) {
            log.error("Error finding most connected wallets: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
