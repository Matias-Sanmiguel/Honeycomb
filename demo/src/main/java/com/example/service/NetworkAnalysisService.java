package com.example.service;

import com.example.dto.NetworkAnalysisResult;
import com.example.model.Wallet;
import com.example.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NetworkAnalysisService {
    
    private final WalletRepository walletRepository;
    private final BlockCypherService blockCypherService;
    
    /**
     * Analiza la red completa de una wallet
     */
    public NetworkAnalysisResult analyzeWalletNetwork(String address) {
        log.info("Analyzing network for wallet: {}", address);
        
        try {
            // 1. Buscar la wallet en la base de datos
            Optional<Wallet> walletOpt = walletRepository.findById(address);

            if (walletOpt.isEmpty()) {
                log.warn("Wallet not found in database: {}", address);
                // Retornar resultado vacío en lugar de fallar
                return NetworkAnalysisResult.builder()
                        .address(address)
                        .balance(0L)
                        .totalTransactions(0)
                        .totalReceived(0L)
                        .totalSent(0L)
                        .directConnections(0)
                        .connectedWallets(new ArrayList<>())
                        .recentTransactions(new ArrayList<>())
                        .riskLevel("UNKNOWN")
                        .tags(new ArrayList<>())
                        .build();
            }

            Wallet wallet = walletOpt.get();

            // 2. Obtener información detallada de conexiones
            List<Map<String, Object>> connectedWalletsDataRaw = walletRepository.findConnectedWallets(address);

            // Extraer los objetos "result" de los wrappers
            List<Map<String, Object>> connectedWalletsData = new ArrayList<>();
            for (Map<String, Object> wrapper : connectedWalletsDataRaw) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = wrapper.containsKey("result")
                    ? (Map<String, Object>) wrapper.get("result")
                    : wrapper;
                connectedWalletsData.add(data);
            }

            // 3. Obtener transacciones recientes
            List<Map<String, Object>> recentTxsRaw = walletRepository.findRecentTransactions(address, 10);

            // Extraer los objetos "result" de los wrappers
            List<Map<String, Object>> recentTxsData = new ArrayList<>();
            for (Map<String, Object> wrapper : recentTxsRaw) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = wrapper.containsKey("result")
                    ? (Map<String, Object>) wrapper.get("result")
                    : wrapper;
                recentTxsData.add(data);
            }

            // 4. Procesar y construir el resultado
            return buildNetworkAnalysisResult(wallet, connectedWalletsData, recentTxsData);
        } catch (Exception e) {
            log.error("Error analyzing network for wallet: " + address, e);
            // Retornar resultado vacío en caso de error
            return NetworkAnalysisResult.builder()
                    .address(address)
                    .balance(0L)
                    .totalTransactions(0)
                    .totalReceived(0L)
                    .totalSent(0L)
                    .directConnections(0)
                    .connectedWallets(new ArrayList<>())
                    .recentTransactions(new ArrayList<>())
                    .riskLevel("ERROR")
                    .tags(new ArrayList<>())
                    .build();
        }
    }
    
    /**
     * Asegura que la wallet existe en la base de datos, si no la fetch de BlockCypher
     */
    private Wallet ensureWalletExists(String address) {
        return walletRepository.findById(address)
                .orElseGet(() -> {
                    log.info("Wallet {} not found, fetching from BlockCypher", address);
                    try {
                        return blockCypherService.fetchAndSaveWallet(address, "BTC");
                    } catch (Exception e) {
                        log.error("Error fetching wallet from BlockCypher: " + address, e);
                        return null;
                    }
                });
    }
    
    /**
     * Construye el resultado del análisis de red
     */
    private NetworkAnalysisResult buildNetworkAnalysisResult(
            Wallet wallet, 
            List<Map<String, Object>> connectedWalletsData,
            List<Map<String, Object>> recentTxsData) {

        // Convertir datos de wallets conectadas
        List<NetworkAnalysisResult.ConnectedWallet> connectedWallets = connectedWalletsData.stream()
                .map(data -> NetworkAnalysisResult.ConnectedWallet.builder()
                        .address((String) data.get("address"))
                        .totalTransferred(
                                ((Number) data.getOrDefault("received", 0L)).longValue() +
                                ((Number) data.getOrDefault("sent", 0L)).longValue()
                        )
                        .transactionCount(((Number) data.getOrDefault("txCount", 0)).intValue())
                        .direction((String) data.getOrDefault("direction", "UNKNOWN"))
                        .build())
                .collect(Collectors.toList());
        
        // Obtener transacciones recientes
        List<NetworkAnalysisResult.TransactionSummary> recentTransactions = recentTxsData.stream()
                .map(tx -> NetworkAnalysisResult.TransactionSummary.builder()
                        .hash((String) tx.get("hash"))
                        .amount(((Number) tx.getOrDefault("amount", 0L)).longValue())
                        .type((String) tx.getOrDefault("type", "UNKNOWN"))
                        .timestamp((String) tx.getOrDefault("timestamp", ""))
                        .build())
                .collect(Collectors.toList());
        
        return NetworkAnalysisResult.builder()
                .address(wallet.getAddress())
                .balance(wallet.getBalance())
                .totalTransactions(wallet.getTxCount())
                .totalReceived(wallet.getTotalReceived())
                .totalSent(wallet.getTotalSent())
                .directConnections(connectedWallets.size())
                .connectedWallets(connectedWallets)
                .recentTransactions(recentTransactions)
                .riskLevel(wallet.getRiskLevel())
                .tags(wallet.getTags())
                .build();
    }
    
    /**
     * Encuentra transacciones grandes en el sistema
     */
    public List<Map<String, Object>> findLargeTransactions(long minAmount, int limit) {
        log.info("Finding large transactions with min amount: {}", minAmount);
        return walletRepository.findLargeTransactions(minAmount, limit);
    }
    
    /**
     * Obtiene estadísticas generales de una wallet
     */
    public Map<String, Object> getWalletStatistics(String address) {
        Wallet wallet = walletRepository.findById(address)
                .orElseThrow(() -> new RuntimeException("Wallet not found: " + address));
        
        List<Map<String, Object>> connections = walletRepository.findConnectedWallets(address);
        
        return Map.of(
                "address", wallet.getAddress(),
                "balance", wallet.getBalance(),
                "totalReceived", wallet.getTotalReceived(),
                "totalSent", wallet.getTotalSent(),
                "transactionCount", wallet.getTxCount(),
                "directConnections", connections.size(),
                "riskLevel", wallet.getRiskLevel() != null ? wallet.getRiskLevel() : "UNKNOWN"
        );
    }
}
