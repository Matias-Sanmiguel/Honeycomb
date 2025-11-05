package com.example.service;

import com.example.dto.PathQueryResult;
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
            PathQueryResult pathData = pathAnalysisRepository.findShortestPath(fromAddress, toAddress);

            if (pathData == null || pathData.getPathLength() == null) {
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
        for (Map<String, Object> pathDataWrapper : pathsData) {
            // Extraer el objeto "result" del wrapper
            @SuppressWarnings("unchecked")
            Map<String, Object> pathData = pathDataWrapper.containsKey("result")
                ? (Map<String, Object>) pathDataWrapper.get("result")
                : pathDataWrapper;

            results.add(buildPathResultFromMap(fromAddress, toAddress, pathData));
        }
        
        return results;
    }
    
    /**
     * Encuentra wallets dentro de N saltos de una wallet dada
     */
    public List<Map<String, Object>> findWalletsWithinHops(String address, int hops) {
        log.info("Finding wallets within {} hops of {}", hops, address);
        List<Map<String, Object>> results = pathAnalysisRepository.findWalletsWithinHops(address, hops);

        // Extraer el objeto "result" de cada mapa
        List<Map<String, Object>> processedResults = new ArrayList<>();
        for (Map<String, Object> result : results) {
            if (result.containsKey("result")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> innerResult = (Map<String, Object>) result.get("result");
                processedResults.add(innerResult);
            } else {
                processedResults.add(result);
            }
        }

        return processedResults;
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
    private PathResult buildPathResult(String fromAddress, String toAddress, PathQueryResult pathData) {
        Integer pathLength = pathData.getPathLength();
        List<Map<String, Object>> nodes = pathData.getNodes();
        List<Map<String, Object>> relationships = pathData.getRelationships();

        List<PathResult.PathNode> pathNodes = new ArrayList<>();
        Long totalAmount = 0L;

        // Procesar nodos y construir el path
        if (nodes != null && !nodes.isEmpty()) {
            for (int i = 0; i < nodes.size(); i++) {
                Map<String, Object> nodeData = nodes.get(i);
                if (nodeData == null) continue;

                List<String> labels = (List<String>) nodeData.get("labels");
                String nodeType = labels != null && !labels.isEmpty() ? labels.get(0) : "UNKNOWN";

                if ("Wallet".equals(nodeType)) {
                    String address = (String) nodeData.get("address");

                    // Buscar la siguiente transacción y su monto
                    String txHash = null;
                    Long amount = 0L;

                    // La siguiente relación (si existe) tiene el monto
                    if (relationships != null && i < relationships.size()) {
                        Map<String, Object> rel = relationships.get(i);
                        if (rel != null) {
                            Object amountObj = rel.get("amount");
                            if (amountObj != null) {
                                amount = ((Number) amountObj).longValue();
                                totalAmount += amount;
                            }
                        }
                    }

                    // La siguiente transacción (si existe) en los nodos
                    if (i + 1 < nodes.size()) {
                        Map<String, Object> nextNode = nodes.get(i + 1);
                        if (nextNode != null) {
                            List<String> nextLabels = (List<String>) nextNode.get("labels");
                            if (nextLabels != null && nextLabels.contains("Transaction")) {
                                txHash = (String) nextNode.get("hash");
                            }
                        }
                    }

                    pathNodes.add(PathResult.PathNode.builder()
                            .address(address)
                            .transactionHash(txHash)
                            .amount(amount)
                            .stepNumber(pathNodes.size() + 1)
                            .nodeType(nodeType)
                            .build());
                }
            }
        }

        return PathResult.builder()
                .fromAddress(fromAddress)
                .toAddress(toAddress)
                .connectionFound(true)
                .pathLength(pathLength / 2) // Dividir por 2 porque cada salto son 2 relaciones
                .path(pathNodes)
                .totalAmountTransferred(totalAmount)
                .build();
    }

    /**
     * Construye el PathResult a partir de un Map (para findAllShortPaths)
     */
    @SuppressWarnings("unchecked")
    private PathResult buildPathResultFromMap(String fromAddress, String toAddress, Map<String, Object> pathData) {
        Integer pathLength = ((Number) pathData.getOrDefault("pathLength", 0)).intValue();
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) pathData.get("nodes");
        List<Map<String, Object>> relationships = (List<Map<String, Object>>) pathData.get("relationships");

        List<PathResult.PathNode> pathNodes = new ArrayList<>();
        Long totalAmount = 0L;

        // Procesar nodos y construir el path
        if (nodes != null && !nodes.isEmpty()) {
            for (int i = 0; i < nodes.size(); i++) {
                Map<String, Object> nodeData = nodes.get(i);
                if (nodeData == null) continue;

                List<String> labels = (List<String>) nodeData.get("labels");
                String nodeType = labels != null && !labels.isEmpty() ? labels.get(0) : "UNKNOWN";

                if ("Wallet".equals(nodeType)) {
                    String address = (String) nodeData.get("address");

                    // Buscar la siguiente transacción y su monto
                    String txHash = null;
                    Long amount = 0L;

                    // La siguiente relación (si existe) tiene el monto
                    if (relationships != null && i < relationships.size()) {
                        Map<String, Object> rel = relationships.get(i);
                        if (rel != null) {
                            Object amountObj = rel.get("amount");
                            if (amountObj != null) {
                                amount = ((Number) amountObj).longValue();
                                totalAmount += amount;
                            }
                        }
                    }

                    // La siguiente transacción (si existe) en los nodos
                    if (i + 1 < nodes.size()) {
                        Map<String, Object> nextNode = nodes.get(i + 1);
                        if (nextNode != null) {
                            List<String> nextLabels = (List<String>) nextNode.get("labels");
                            if (nextLabels != null && nextLabels.contains("Transaction")) {
                                txHash = (String) nextNode.get("hash");
                            }
                        }
                    }

                    pathNodes.add(PathResult.PathNode.builder()
                            .address(address)
                            .transactionHash(txHash)
                            .amount(amount)
                            .stepNumber(pathNodes.size() + 1)
                            .nodeType(nodeType)
                            .build());
                }
            }
        }
        
        return PathResult.builder()
                .fromAddress(fromAddress)
                .toAddress(toAddress)
                .connectionFound(true)
                .pathLength(pathLength / 2) // Dividir por 2 porque cada salto son 2 relaciones
                .path(pathNodes)
                .totalAmountTransferred(totalAmount)
                .build();
    }
}
