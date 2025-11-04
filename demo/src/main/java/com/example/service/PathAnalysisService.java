package com.example.service;

import com.example.dto.PathResult;
import com.example.repository.PathAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PathAnalysisService {
    
    private final PathAnalysisRepository pathAnalysisRepository;
    
    /**
     * Encuentra el camino más corto entre dos wallets
     */
    public PathResult findConnectionPath(String fromAddress, String toAddress) {
        log.info("Finding path from {} to {}", fromAddress, toAddress);
        
        try {
            Map<String, Object> pathData = pathAnalysisRepository.findShortestPath(fromAddress, toAddress);
            
            if (pathData == null || pathData.isEmpty()) {
                return buildNoConnectionResult(fromAddress, toAddress);
            }
            
            return buildPathResult(fromAddress, toAddress, pathData);
            
        } catch (Exception e) {
            log.error("Error finding path: {}", e.getMessage());
            return buildNoConnectionResult(fromAddress, toAddress);
        }
    }
    
    /**
     * Encuentra múltiples caminos cortos entre dos wallets
     */
    public List<PathResult> findAllShortPaths(String fromAddress, String toAddress, int maxLength) {
        log.info("Finding all paths from {} to {} with max length {}", fromAddress, toAddress, maxLength);
        
        List<Map<String, Object>> pathsData = pathAnalysisRepository.findAllShortPaths(
                fromAddress, toAddress, maxLength);
        
        List<PathResult> results = new ArrayList<>();
        for (Map<String, Object> pathData : pathsData) {
            results.add(buildPathResult(fromAddress, toAddress, pathData));
        }
        
        return results;
    }
    
    /**
     * Encuentra wallets dentro de N saltos de una wallet dada
     */
    public List<Map<String, Object>> findWalletsWithinHops(String address, int hops) {
        log.info("Finding wallets within {} hops of {}", hops, address);
        return pathAnalysisRepository.findWalletsWithinHops(address, hops);
    }
    
    /**
     * Encuentra las wallets más conectadas del sistema
     */
    public List<Map<String, Object>> findMostConnectedWallets(int minConnections, int limit) {
        log.info("Finding most connected wallets with min {} connections", minConnections);
        return pathAnalysisRepository.findMostConnectedWallets(minConnections, limit);
    }
    
    /**
     * Construye el resultado cuando no hay conexión
     */
    private PathResult buildNoConnectionResult(String fromAddress, String toAddress) {
        return PathResult.builder()
                .fromAddress(fromAddress)
                .toAddress(toAddress)
                .connectionFound(false)
                .pathLength(0)
                .path(new ArrayList<>())
                .totalAmountTransferred(0L)
                .build();
    }
    
    /**
     * Construye el PathResult a partir de los datos de Neo4j
     */
    @SuppressWarnings("unchecked")
    private PathResult buildPathResult(String fromAddress, String toAddress, Map<String, Object> pathData) {
        Integer pathLength = (Integer) pathData.getOrDefault("pathLength", 0);
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) pathData.get("nodes");
        List<Map<String, Object>> relationships = (List<Map<String, Object>>) pathData.get("relationships");

        List<PathResult.PathNode> pathNodes = new ArrayList<>();

        // Procesar nodos y construir el path
        if (nodes != null) {
            for (int i = 0; i < nodes.size(); i++) {
                Map<String, Object> nodeData = nodes.get(i);
                List<String> labels = (List<String>) nodeData.get("labels");
                String nodeType = labels != null && !labels.isEmpty() ? labels.get(0) : "UNKNOWN";

                String address = null;
                String txHash = null;

                if ("Wallet".equals(nodeType)) {
                    address = (String) nodeData.get("address");
                } else if ("Transaction".equals(nodeType)) {
                    txHash = (String) nodeData.get("hash");
                    // For transactions, we might want to associate them with the *next* wallet in the path
                    // or handle them as distinct steps. Let's find the next wallet.
                    if (i + 1 < nodes.size()) {
                        Map<String, Object> nextNodeData = nodes.get(i + 1);
                        address = (String) nextNodeData.get("address");
                    }
                }

                Long amount = 0L;
                if (relationships != null && i < relationships.size()) {
                    Map<String, Object> rel = relationships.get(i);
                    Object valueObj = rel.get("amount"); // Changed from "value" to "amount"
                    if (valueObj != null) {
                        amount = ((Number) valueObj).longValue();
                    }
                }

                if ("Wallet".equals(nodeType)) {
                     pathNodes.add(PathResult.PathNode.builder()
                        .address(address)
                        .transactionHash(null) // Wallet nodes don't have a tx hash
                        .amount(0L) // Amount is on the relationship
                        .stepNumber(pathNodes.size() + 1)
                        .nodeType(nodeType)
                        .build());
                } else if ("Transaction".equals(nodeType)) {
                    // Let's update the last wallet node with transaction info
                    if (!pathNodes.isEmpty()) {
                        PathResult.PathNode lastNode = pathNodes.get(pathNodes.size() - 1);
                        lastNode.setTransactionHash(txHash);
                        lastNode.setAmount(amount);
                    }
                }
            }
        }

        // Calcular el total transferido
        Long totalAmount = 0L;
        if (relationships != null) {
            for (Map<String, Object> rel : relationships) {
                Object valueObj = rel.get("amount"); // Changed from "value" to "amount"
                if (valueObj != null) {
                    totalAmount += ((Number) valueObj).longValue();
                }
            }
        }
        
        return PathResult.builder()
                .fromAddress(fromAddress)
                .toAddress(toAddress)
                .connectionFound(true)
                .pathLength(pathLength)
                .path(pathNodes)
                .totalAmountTransferred(totalAmount)
                .build();
    }
}
