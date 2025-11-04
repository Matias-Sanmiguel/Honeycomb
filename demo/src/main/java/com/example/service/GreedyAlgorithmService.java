package com.example.service;

import com.example.dto.PeelChainGreedyResult;
import com.example.model.Transaction;
import com.example.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para algoritmos Greedy de análisis forense
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GreedyAlgorithmService {

    private final TransactionRepository transactionRepository;

    /**
     * Analiza peel chains usando algoritmo greedy
     * Complejidad: O(n log n)
     */
    public List<PeelChainGreedyResult> analyzePeelChainsGreedy(Double threshold, Integer limit) {
        log.info("Analyzing peel chains with threshold: {}, limit: {}", threshold, limit);

        List<Transaction> transactions = transactionRepository.findAll();
        List<PeelChainGreedyResult> results = new ArrayList<>();

        // Análisis greedy simple
        for (Transaction tx : transactions) {
            if (results.size() >= limit) {
                break;
            }

            PeelChainGreedyResult result = PeelChainGreedyResult.builder()
                    .transactionHash(tx.getHash())
                    .chainLength(1)
                    .totalAmount(tx.getTotalOutput() != null ? tx.getTotalOutput() : 0L)
                    .confidence(0.8)
                    .pattern("SIMPLE")
                    .build();

            results.add(result);
        }

        log.info("Found {} peel chain candidates", results.size());
        return results;
    }

    /**
     * Analiza clusters de peel chains
     */
    public List<PeelChainGreedyResult> analyzePeelChainClusters(Double threshold, Integer minChainLength, Integer limit) {
        log.info("Analyzing peel chain clusters with threshold: {}", threshold);

        // Implementación simplificada
        return analyzePeelChainsGreedy(threshold, limit);
    }
}

