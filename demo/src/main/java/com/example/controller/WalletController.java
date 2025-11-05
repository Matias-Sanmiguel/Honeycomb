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
            List<Map<String, Object>> recentTxsRaw = walletRepository.findRecentTransactions(address, 50);
            List<Map<String, Object>> connectionsRaw = walletRepository.findConnectedWallets(address);

            // Extraer los objetos "result" de los wrappers
            List<Map<String, Object>> recentTxs = new ArrayList<>();
            for (Map<String, Object> txWrapper : recentTxsRaw) {
                @SuppressWarnings("unchecked")
                Map<String, Object> tx = txWrapper.containsKey("result")
                    ? (Map<String, Object>) txWrapper.get("result")
                    : txWrapper;
                recentTxs.add(tx);
            }

            List<Map<String, Object>> connections = new ArrayList<>();
            for (Map<String, Object> connWrapper : connectionsRaw) {
                @SuppressWarnings("unchecked")
                Map<String, Object> conn = connWrapper.containsKey("result")
                    ? (Map<String, Object>) connWrapper.get("result")
                    : connWrapper;
                connections.add(conn);
            }

            response.put("address", address);
            response.put("totalTransactions", recentTxs.size());
            response.put("uniqueConnections", connections.size());

            // Calcular métricas reales de forma más segura
            long totalVolume = 0;
            try {
                totalVolume = connections.stream()
                    .mapToLong(conn -> {
                        Object received = conn.get("received");
                        Object sent = conn.get("sent");
                        long receivedVal = 0;
                        long sentVal = 0;

                        if (received instanceof Number) {
                            receivedVal = ((Number) received).longValue();
                        } else if (received instanceof Double) {
                            receivedVal = ((Double) received).longValue();
                        }

                        if (sent instanceof Number) {
                            sentVal = ((Number) sent).longValue();
                        } else if (sent instanceof Double) {
                            sentVal = ((Double) sent).longValue();
                        }

                        return receivedVal + sentVal;
                    })
                    .sum();
            } catch (Exception e) {
                // Si falla el cálculo, usar 0 y loguear
                System.out.println("Error calculating volume: " + e.getMessage());
                totalVolume = 0;
            }

            double avgTxAmount = connections.isEmpty() ? 0 :
                (double) totalVolume / connections.size();

            response.put("totalVolume", totalVolume);
            response.put("averageTransactionAmount", avgTxAmount);

            // Top conexiones con manejo robusto de errores
            List<Map<String, Object>> topConnections = new ArrayList<>();
            try {
                for (int i = 0; i < Math.min(5, connections.size()); i++) {
                    Map<String, Object> conn = connections.get(i);
                    Map<String, Object> topConn = new HashMap<>();
                    topConn.put("address", conn.getOrDefault("address", "unknown"));
                    topConn.put("transactionCount", conn.getOrDefault("txCount", 0));

                    Object received = conn.get("received");
                    Object sent = conn.get("sent");
                    long receivedVal = 0;
                    long sentVal = 0;

                    if (received instanceof Number) {
                        receivedVal = ((Number) received).longValue();
                    }
                    if (sent instanceof Number) {
                        sentVal = ((Number) sent).longValue();
                    }

                    topConn.put("totalAmount", receivedVal + sentVal);
                    topConn.put("direction", conn.getOrDefault("direction", "UNKNOWN"));

                    topConnections.add(topConn);
                }
            } catch (Exception e) {
                System.out.println("Error processing top connections: " + e.getMessage());
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
            System.err.println("Error analyzing wallet: " + e.getMessage());
            e.printStackTrace();

            // Respuesta más amigable en caso de error
            response.put("address", address);
            response.put("totalTransactions", 0);
            response.put("uniqueConnections", 0);
            response.put("totalVolume", 0);
            response.put("riskScore", 0.0);
            response.put("topConnections", new ArrayList<>());
            response.put("suspiciousPatterns", List.of("Error: Could not analyze wallet - " + e.getMessage()));
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");
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
