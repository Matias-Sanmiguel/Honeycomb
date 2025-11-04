package com.example.controller;

import com.example.service.GreedyAlgorithmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/greedy")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GreedyController {

    private final GreedyAlgorithmService greedyAlgorithmService;

    @GetMapping("/max-value-path")
    public Map<String, Object> findMaxValuePath(
            @RequestParam String sourceAddress,
            @RequestParam(defaultValue = "0.95") double threshold,
            @RequestParam(defaultValue = "20") int limit) {

        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();

        try {
            var peelChains = greedyAlgorithmService.analyzePeelChainsGreedy(threshold, limit);

            response.put("sourceAddress", sourceAddress);
            response.put("threshold", threshold);
            response.put("chainsFound", peelChains.size());
            response.put("chains", peelChains);

            long totalValue = peelChains.stream()
                .mapToLong(chain -> chain.getTotalAmount())
                .sum();

            response.put("totalValue", totalValue);
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            response.put("error", "Error ejecutando algoritmo greedy: " + e.getMessage());
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");
        }

        return response;
    }

    @GetMapping("/peel-chains")
    public Map<String, Object> analyzePeelChains(
            @RequestParam(defaultValue = "0.95") double threshold,
            @RequestParam(defaultValue = "3") int minChainLength,
            @RequestParam(defaultValue = "20") int limit) {

        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();

        try {
            var chains = greedyAlgorithmService.analyzePeelChainClusters(
                threshold, minChainLength, limit);

            response.put("threshold", threshold);
            response.put("minChainLength", minChainLength);
            response.put("chainsFound", chains.size());
            response.put("chains", chains);
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            response.put("error", "Error analizando peel chains: " + e.getMessage());
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");
        }

        return response;
    }
}
