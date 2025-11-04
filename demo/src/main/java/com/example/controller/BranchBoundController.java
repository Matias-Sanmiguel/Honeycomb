package com.example.controller;

import com.example.service.BranchBoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/branch-bound")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BranchBoundController {

    private final BranchBoundService branchBoundService;

    @GetMapping("/optimal-path")
    public Map<String, Object> findOptimalPath(
            @RequestParam String sourceAddress,
            @RequestParam String targetAddress,
            @RequestParam(defaultValue = "5") int maxDepth) {

        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();

        try {
            // TODO: Consider if maxDepth should be translated to a cost or used directly
            double maxCost = maxDepth * 1000; // Example conversion

            var result = branchBoundService.findOptimalPathWithCostLimit(
                sourceAddress, targetAddress, maxCost);

            response.put("sourceAddress", sourceAddress);
            response.put("targetAddress", targetAddress);
            response.put("maxDepth", maxDepth);
            response.put("pathFound", result.isPathFound());
            response.put("path", result.getPath());
            response.put("totalCost", result.getTotalCost());
            response.put("pathLength", result.getPathLength());
            response.put("nodesExplored", result.getNodesExplored());
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            response.put("error", "Error ejecutando branch and bound: " + e.getMessage());
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");
        }

        return response;
    }

    @GetMapping("/cheapest-path")
    public Map<String, Object> findCheapestPath(
            @RequestParam String sourceAddress,
            @RequestParam String targetAddress) {

        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();

        try {
            var result = branchBoundService.findCheapestPath(sourceAddress, targetAddress);

            response.put("sourceAddress", sourceAddress);
            response.put("targetAddress", targetAddress);
            response.put("pathFound", result.isPathFound());
            response.put("path", result.getPath());
            response.put("totalCost", result.getTotalCost());
            response.put("pathLength", result.getPathLength());
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            response.put("error", "Error buscando camino más barato: " + e.getMessage());
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");
        }

        return response;
    }

    @GetMapping("/multiple-paths")
    public Map<String, Object> findMultiplePaths(
            @RequestParam String sourceAddress,
            @RequestParam String targetAddress) {

        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();

        try {
            var results = branchBoundService.findMultiplePathsWithDifferentCosts(
                sourceAddress, targetAddress);

            response.put("sourceAddress", sourceAddress);
            response.put("targetAddress", targetAddress);
            response.put("pathsAnalyzed", results.size());
            response.put("results", results);
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            response.put("error", "Error buscando múltiples caminos: " + e.getMessage());
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");
        }

        return response;
    }
}
