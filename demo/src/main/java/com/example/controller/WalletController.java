package com.example.controller;

import com.example.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class WalletController {

    private final WalletRepository walletRepository;

    @GetMapping("/analyze")
    public Map<String, Object> analyzeWallet(@RequestParam String address) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();

        try {
            // Obtener datos reales de la wallet desde Neo4j
            var recentTxs = walletRepository.findRecentTransactions(address, 50);
            var connections = walletRepository.findConnectedWallets(address);

            response.put("address", address);
            response.put("totalTransactions", recentTxs.size());
            response.put("uniqueConnections", connections.size());

            // Calcular métricas reales
            long totalVolume = 0;
            try {
                totalVolume = connections.stream()
                    .mapToLong(conn -> {
                        Object received = conn.get("received");
                        Object sent = conn.get("sent");
                        long receivedVal = received != null ? ((Number) received).longValue() : 0;
                        long sentVal = sent != null ? ((Number) sent).longValue() : 0;
                        return receivedVal + sentVal;
                    })
                    .sum();
            } catch (Exception e) {
                // Si falla el cálculo, usar 0
                totalVolume = 0;
            }

            double avgTxAmount = connections.isEmpty() ? 0 :
                (double) totalVolume / connections.size();

            response.put("totalVolume", totalVolume);
            response.put("averageTransactionAmount", avgTxAmount);

            // Top conexiones
            List<Map<String, Object>> topConnections = new ArrayList<>();
            for (int i = 0; i < Math.min(5, connections.size()); i++) {
                Map<String, Object> conn = connections.get(i);
                Map<String, Object> topConn = new HashMap<>();
                topConn.put("address", conn.get("address"));
                topConn.put("transactionCount", conn.get("txCount"));

                Object received = conn.get("received");
                Object sent = conn.get("sent");
                long receivedVal = received != null ? ((Number) received).longValue() : 0;
                long sentVal = sent != null ? ((Number) sent).longValue() : 0;

                topConn.put("totalAmount", receivedVal + sentVal);
                topConn.put("direction", conn.get("direction"));

                topConnections.add(topConn);
            }

            response.put("topConnections", topConnections);

            // Calcular nivel de riesgo basado en datos reales
            double riskScore = calculateRiskScore(connections.size(), totalVolume, avgTxAmount);
            response.put("riskScore", riskScore);

            List<String> suspiciousPatterns = new ArrayList<>();
            if (connections.size() > 50) {
                suspiciousPatterns.add("High number of connections");
            }
            if (avgTxAmount < 1000 && connections.size() > 20) {
                suspiciousPatterns.add("Multiple small transactions");
            }
            if (suspiciousPatterns.isEmpty()) {
                suspiciousPatterns.add("No suspicious patterns detected");
            }
            response.put("suspiciousPatterns", suspiciousPatterns);

            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            response.put("error", "Error analizando wallet: " + e.getMessage());
            response.put("address", address);
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");
            // Agregar valores por defecto en caso de error
            response.put("totalTransactions", 0);
            response.put("uniqueConnections", 0);
            response.put("totalVolume", 0);
            response.put("riskScore", 0.0);
        }

        return response;
    }

    private double calculateRiskScore(int connections, long volume, double avgAmount) {
        double score = 0.0;

        // Más conexiones = más riesgo
        if (connections > 100) score += 3.0;
        else if (connections > 50) score += 2.0;
        else if (connections > 20) score += 1.0;

        // Volumen alto = más riesgo
        if (volume > 1000000) score += 3.0;
        else if (volume > 500000) score += 2.0;
        else if (volume > 100000) score += 1.0;

        // Muchas transacciones pequeñas = más riesgo
        if (avgAmount < 1000 && connections > 20) score += 2.5;

        return Math.min(10.0, score);
    }
}
