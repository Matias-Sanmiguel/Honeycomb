package com.example.controller;

import com.example.service.BacktrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/backtracking")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BacktrackingController {

    private final BacktrackingService backtrackingService;

    @GetMapping("/suspicious-chains")
    public Map<String, Object> findSuspiciousChains(
            @RequestParam String sourceAddress,
            @RequestParam(defaultValue = "5") int maxDepth) {

        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();

        try {
            var chains = backtrackingService.detectSuspiciousChains(sourceAddress, maxDepth);

            response.put("sourceAddress", sourceAddress);
            response.put("maxDepth", maxDepth);
            response.put("chainsFound", chains.size());
            response.put("chains", chains);
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            response.put("error", "Error ejecutando backtracking: " + e.getMessage());
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");
        }

        return response;
    }

    @GetMapping("/detect-cycles")
    public Map<String, Object> detectCycles(@RequestParam(defaultValue = "10") int maxCycles) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();

        try {
            var cycles = backtrackingService.detectAllCycles(maxCycles);

            response.put("cyclesFound", cycles.size());
            response.put("cycles", cycles);
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");

        } catch (Exception e) {
            response.put("error", "Error detectando ciclos: " + e.getMessage());
            response.put("executionTime", (System.currentTimeMillis() - startTime) + "ms");
        }

        return response;
    }
}

