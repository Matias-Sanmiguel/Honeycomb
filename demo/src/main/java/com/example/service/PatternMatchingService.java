package com.example.service;

import com.example.dto.PatternDetectionResult;
import com.example.repository.AlgorithmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio implementando detección de patrones de lavado de dinero
 * Complejidad: Varía según patrón (O(n) a O(n²))
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PatternMatchingService {

    private final AlgorithmRepository algorithmRepository;

    private static final double CONFIDENCE_THRESHOLD = 0.7;

    /**
     * Detectar múltiples patrones de lavado de dinero
     *
     * Patrones soportados:
     * 1. MIXING: Una wallet envía a múltiples direcciones que convergen
     * 2. CYCLICAL: Transacciones cíclicas (A→B→C→A) ocultando origen
     * 3. RAPID: Múltiples transacciones en corto tiempo (mismo bloque)
     * 4. ANOMALY: Saltos significativos en montos (detección outliers)
     */
    public List<PatternDetectionResult> detectAnomalyPatterns(
            Integer analysisDepth,
            Integer timeWindowDays,
            Double anomalyThreshold,
            List<String> patternTypes) {

        long startTime = System.currentTimeMillis();
        log.info("Starting pattern detection with depth: {}, window: {} days, threshold: {}",
                analysisDepth, timeWindowDays, anomalyThreshold);

        List<PatternDetectionResult> allPatterns = new ArrayList<>();

        try {
            // Determinar qué patrones buscar
            boolean detectMixing = patternTypes == null || patternTypes.isEmpty() || patternTypes.contains("MIXING");
            boolean detectCyclical = patternTypes == null || patternTypes.isEmpty() || patternTypes.contains("CYCLICAL");
            boolean detectRapid = patternTypes == null || patternTypes.isEmpty() || patternTypes.contains("RAPID");
            boolean detectAnomaly = patternTypes == null || patternTypes.isEmpty() || patternTypes.contains("ANOMALY");

            if (detectMixing) {
                allPatterns.addAll(detectMixingPatterns(analysisDepth));
            }

            if (detectCyclical) {
                allPatterns.addAll(detectCyclicalPatterns(analysisDepth));
            }

            if (detectRapid) {
                allPatterns.addAll(detectRapidRedistributionPatterns(timeWindowDays));
            }

            if (detectAnomaly) {
                allPatterns.addAll(detectAmountAnomalies(anomalyThreshold));
            }

            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Pattern detection completed in {}ms, found {} anomalies",
                    executionTime, allPatterns.size());

            return allPatterns.stream()
                    .sorted(Comparator.comparingDouble(PatternDetectionResult::getConfidence).reversed())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error in pattern detection", e);
            return Collections.emptyList();
        }
    }

    /**
     * PATRÓN 1: MIXING
     *
     * Característica: Una wallet envía a MÚLTIPLES direcciones que luego CONVERGEN
     * Indicador: Distribución seguida de convergencia (típico de mixers)
     *
     * Complejidad: O(n²) para encontrar todos los pares convergentes
     */
    private List<PatternDetectionResult> detectMixingPatterns(Integer depth) {
        log.debug("Detecting MIXING patterns with depth: {}", depth);

        List<Map<String, Object>> mixingData = algorithmRepository.detectMixingPatterns(depth);

        return mixingData.stream()
                .map(data -> {
                    List<String> affectedWallets = (List<String>) data.get("wallets");
                    Integer inputCount = ((Number) data.get("inputCount")).intValue();
                    Integer outputCount = ((Number) data.get("outputCount")).intValue();
                    Long totalAmount = ((Number) data.get("amount")).longValue();

                    // Confianza: mayor cuanto mayor sea la divergencia inicial
                    Double confidence = Math.min(0.99, 0.5 + (outputCount / 20.0));

                    return PatternDetectionResult.builder()
                            .patternType("MIXING")
                            .confidence(confidence)
                            .affectedWallets(affectedWallets)
                            .description(String.format("Wallet distributes to %d addresses that later converge", outputCount))
                            .severity(confidence >= 0.9 ? "CRITICAL" : confidence >= 0.8 ? "HIGH" : "MEDIUM")
                            .inputCount(inputCount)
                            .outputCount(outputCount)
                            .totalAmount(totalAmount)
                            .detectedAt(System.currentTimeMillis())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * PATRÓN 2: CYCLICAL
     *
     * Característica: Transacciones cíclicas (A→B→C→A)
     * Indicador: Detecta ciclos que pueden indicar ocultamiento de origen
     *
     * Complejidad: O(V + E) usando DFS para detección de ciclos
     */
    private List<PatternDetectionResult> detectCyclicalPatterns(Integer maxDepth) {
        log.debug("Detecting CYCLICAL patterns with maxDepth: {}", maxDepth);

        List<Map<String, Object>> cyclicalData = algorithmRepository.detectCycles(maxDepth);

        return cyclicalData.stream()
                .map(data -> {
                    List<String> cycle = (List<String>) data.get("cycle");
                    Integer cycleLength = ((Number) data.get("cycleLength")).intValue();
                    Long totalAmount = ((Number) data.get("amount")).longValue();

                    // Confianza: mayor para ciclos cortos (más sospechosos)
                    Double confidence = cycleLength <= 3 ? 0.95 : cycleLength <= 5 ? 0.85 : 0.70;

                    return PatternDetectionResult.builder()
                            .patternType("CYCLICAL")
                            .confidence(confidence)
                            .affectedWallets(cycle)
                            .cycle(cycle)
                            .cycleLength(cycleLength)
                            .description(String.format("Circular transaction pattern: %s", String.join(" → ", cycle)))
                            .severity(confidence >= 0.9 ? "CRITICAL" : confidence >= 0.8 ? "HIGH" : "MEDIUM")
                            .totalAmount(totalAmount)
                            .detectedAt(System.currentTimeMillis())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * PATRÓN 3: RAPID REDISTRIBUTION
     *
     * Característica: Múltiples transacciones en muy corto tiempo (mismo bloque)
     * Indicador: Redistribución rápida para evitar rastreo
     *
     * Complejidad: O(n log n) para agrupar por timestamp
     */
    private List<PatternDetectionResult> detectRapidRedistributionPatterns(Integer timeWindowDays) {
        log.debug("Detecting RAPID patterns with timeWindow: {} days", timeWindowDays);

        long timeWindowSeconds = (long) timeWindowDays * 24 * 60 * 60;
        List<Map<String, Object>> rapidData = algorithmRepository.detectRapidTransactions(timeWindowSeconds);

        return rapidData.stream()
                .map(data -> {
                    Integer txCount = ((Number) data.get("transactionCount")).intValue();
                    Long timeWindow = ((Number) data.get("timeSpan")).longValue();

                    // Confianza: mayor cuantas más transacciones en menor tiempo
                    Double confidence = Math.min(0.99, 0.5 + (txCount / 10.0));

                    return PatternDetectionResult.builder()
                            .patternType("RAPID")
                            .confidence(confidence)
                            .description(String.format("%d transactions in %d seconds", txCount, timeWindow))
                            .severity(confidence >= 0.9 ? "CRITICAL" : confidence >= 0.8 ? "HIGH" : "MEDIUM")
                            .transactionCount(txCount)
                            .timeWindowSeconds(timeWindow)
                            .detectedAt(System.currentTimeMillis())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * PATRÓN 4: ANOMALY (Detección de Outliers)
     *
     * Concepto: Saltos significativos en montos de transacciones
     * Método: Detección estadística (Z-score, IQR)
     *
     * Fórmula Z-score: z = (x - μ) / σ
     * Si |z| > threshold → es outlier
     *
     * Complejidad: O(n) para calcular media y desviación estándar
     */
    private List<PatternDetectionResult> detectAmountAnomalies(Double stdDeviationThreshold) {
        log.debug("Detecting ANOMALY patterns with threshold: {} std devs", stdDeviationThreshold);

        List<Map<String, Object>> transactionData = algorithmRepository.getTransactionAmounts();

        if (transactionData.isEmpty()) {
            return Collections.emptyList();
        }

        // Calcular estadísticas: media y desviación estándar
        List<Double> amounts = transactionData.stream()
                .map(data -> ((Number) data.get("amount")).doubleValue())
                .collect(Collectors.toList());

        double mean = amounts.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = amounts.stream()
                .mapToDouble(x -> Math.pow(x - mean, 2))
                .average()
                .orElse(0);
        double stdDev = Math.sqrt(variance);

        final double finalThreshold = stdDeviationThreshold != null ? stdDeviationThreshold : 2.5;

        // Detectar outliers usando Z-score
        return transactionData.stream()
                .filter(data -> {
                    double amount = ((Number) data.get("amount")).doubleValue();
                    double zScore = Math.abs((amount - mean) / (stdDev + 1e-10)); // Evitar div by 0
                    return zScore > finalThreshold;
                })
                .map(data -> {
                    double amount = ((Number) data.get("amount")).doubleValue();
                    double zScore = Math.abs((amount - mean) / (stdDev + 1e-10));

                    // Confianza aumenta con el Z-score
                    Double confidence = Math.min(0.99, 0.7 + (zScore / 10.0));

                    return PatternDetectionResult.builder()
                            .patternType("ANOMALY")
                            .confidence(confidence)
                            .affectedWallets(List.of((String) data.get("wallet")))
                            .description(String.format("Unusual transaction amount: %.2f BTC (%.2f std devs from mean)",
                                    amount, zScore))
                            .severity(zScore >= 5.0 ? "CRITICAL" : zScore >= 3.5 ? "HIGH" : "MEDIUM")
                            .anomalyScore(zScore)
                            .standardDeviations(zScore)
                            .totalAmount(((Number) data.get("amount")).longValue())
                            .detectedAt(System.currentTimeMillis())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
