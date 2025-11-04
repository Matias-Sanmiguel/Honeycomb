package com.example.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests de integración para todos los endpoints de algoritmos
 */
@SpringBootTest
@AutoConfigureMockMvc
class AlgorithmEndpointsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // ========== HEALTH CHECK ==========

    @Test
    @DisplayName("GET /api/algorithms/health - Debería retornar status UP")
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/algorithms/health"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status", is("UP")))
            .andExpect(jsonPath("$.module", is("Algorithms")))
            .andExpect(jsonPath("$.algorithms", hasSize(greaterThan(5))))
            .andExpect(jsonPath("$.algorithms", hasItem("BACKTRACKING_SUSPICIOUS_CHAINS")))
            .andExpect(jsonPath("$.algorithms", hasItem("BRANCH_AND_BOUND_OPTIMAL_PATH")));
    }

    // ========== BACKTRACKING ENDPOINTS ==========

    @Test
    @DisplayName("GET /api/forensic/backtrack/suspicious-chains/{depth} - Con wallet")
    void testBacktrackingSuspiciousChainsWithWallet() throws Exception {
        String testWallet = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa";
        int depth = 5;

        mockMvc.perform(get("/api/forensic/backtrack/suspicious-chains/{depth}", depth)
                .param("wallet", testWallet))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.algorithm", is("BACKTRACKING")))
            .andExpect(jsonPath("$.complexity", containsString("O(b^d)")))
            .andExpect(jsonPath("$.startWallet", is(testWallet)))
            .andExpect(jsonPath("$.maxDepth", is(depth)))
            .andExpect(jsonPath("$.suspiciousChains").isArray())
            .andExpect(jsonPath("$.totalChainsFound").exists())
            .andExpect(jsonPath("$.statistics").exists())
            .andExpect(jsonPath("$.statistics.averageSuspicionLevel").exists())
            .andExpect(jsonPath("$.statistics.cyclesDetected").exists());
    }

    @Test
    @DisplayName("GET /api/forensic/backtrack/suspicious-chains/{depth} - Sin wallet (global)")
    void testBacktrackingGlobalSearch() throws Exception {
        int depth = 4;

        mockMvc.perform(get("/api/forensic/backtrack/suspicious-chains/{depth}", depth))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.algorithm", is("BACKTRACKING")))
            .andExpect(jsonPath("$.startWallet", is("GLOBAL_SEARCH")))
            .andExpect(jsonPath("$.maxDepth", is(depth)));
    }

    @Test
    @DisplayName("GET /api/forensic/backtrack/suspicious-chains/{depth} - Depth inválido")
    void testBacktrackingInvalidDepth() throws Exception {
        // Depth muy alto (>10)
        mockMvc.perform(get("/api/forensic/backtrack/suspicious-chains/{depth}", 15))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Depth must be between")));

        // Depth = 0
        mockMvc.perform(get("/api/forensic/backtrack/suspicious-chains/{depth}", 0))
            .andExpect(status().isBadRequest());
    }

    // ========== BRANCH & BOUND ENDPOINTS ==========

    @Test
    @DisplayName("GET /api/path/branch-bound/{addr1}/{addr2}/{maxCost} - Camino válido")
    void testBranchBoundOptimalPath() throws Exception {
        String addr1 = "walletA";
        String addr2 = "walletB";
        double maxCost = 100.0;

        mockMvc.perform(get("/api/path/branch-bound/{addr1}/{addr2}/{maxCost}",
                addr1, addr2, maxCost))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.algorithm", is("BRANCH_AND_BOUND")))
            .andExpect(jsonPath("$.complexity", containsString("O(b^d)")))
            .andExpect(jsonPath("$.sourceWallet", is(addr1)))
            .andExpect(jsonPath("$.targetWallet", is(addr2)))
            .andExpect(jsonPath("$.maxCostAllowed", is(maxCost)))
            .andExpect(jsonPath("$.pathFound").isBoolean());
    }

    @Test
    @DisplayName("GET /api/path/branch-bound/{addr1}/{addr2}/{maxCost} - MaxCost negativo")
    void testBranchBoundInvalidMaxCost() throws Exception {
        mockMvc.perform(get("/api/path/branch-bound/{addr1}/{addr2}/{maxCost}",
                "wallet1", "wallet2", -10.0))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("must be positive")));
    }

    @Test
    @DisplayName("GET /api/path/branch-bound/{addr1}/{addr2}/{maxCost} - Wallet vacía")
    void testBranchBoundEmptyWallet() throws Exception {
        mockMvc.perform(get("/api/path/branch-bound/{addr1}/{addr2}/{maxCost}",
                "", "wallet2", 100.0))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("GET /api/path/branch-bound/analyze/{addr1}/{addr2} - Análisis multi-escenario")
    void testBranchBoundMultiScenario() throws Exception {
        String addr1 = "walletA";
        String addr2 = "walletB";

        mockMvc.perform(get("/api/path/branch-bound/analyze/{addr1}/{addr2}", addr1, addr2))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.algorithm", is("BRANCH_AND_BOUND_MULTI")))
            .andExpect(jsonPath("$.sourceWallet", is(addr1)))
            .andExpect(jsonPath("$.targetWallet", is(addr2)))
            .andExpect(jsonPath("$.results").exists())
            .andExpect(jsonPath("$.totalScenarios").exists());
    }

    // ========== GREEDY ENDPOINTS ==========

    @Test
    @DisplayName("POST /api/algorithms/greedy/peel-chains - Análisis básico")
    void testGreedyPeelChains() throws Exception {
        String requestBody = """
            {
                "threshold": 0.95,
                "limit": 50
            }
            """;

        mockMvc.perform(post("/api/algorithms/greedy/peel-chains")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.algorithm", is("GREEDY_PEEL_CHAINS")))
            .andExpect(jsonPath("$.complexity", is("O(n log n)")))
            .andExpect(jsonPath("$.results").isArray())
            .andExpect(jsonPath("$.threshold", is(0.95)));
    }

    @Test
    @DisplayName("POST /api/algorithms/greedy/peel-chains - Threshold inválido")
    void testGreedyInvalidThreshold() throws Exception {
        String requestBody = """
            {
                "threshold": 1.5,
                "limit": 50
            }
            """;

        mockMvc.perform(post("/api/algorithms/greedy/peel-chains")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Threshold must be between")));
    }

    // ========== DYNAMIC PROGRAMMING ENDPOINTS ==========

    @Test
    @DisplayName("POST /api/algorithms/dp/max-flow-path - Camino válido")
    void testDynamicProgrammingMaxFlow() throws Exception {
        String requestBody = """
            {
                "sourceWallet": "wallet1",
                "targetWallet": "wallet2",
                "maxHops": 10
            }
            """;

        mockMvc.perform(post("/api/algorithms/dp/max-flow-path")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.algorithm", is("DYNAMIC_PROGRAMMING_MAX_FLOW")))
            .andExpect(jsonPath("$.complexity", is("O(V + E)")))
            .andExpect(jsonPath("$.sourceWallet", is("wallet1")))
            .andExpect(jsonPath("$.targetWallet", is("wallet2")))
            .andExpect(jsonPath("$.foundPath").isBoolean());
    }

    @Test
    @DisplayName("POST /api/algorithms/dp/max-flow-path - Sin sourceWallet")
    void testDynamicProgrammingMissingSource() throws Exception {
        String requestBody = """
            {
                "targetWallet": "wallet2",
                "maxHops": 10
            }
            """;

        mockMvc.perform(post("/api/algorithms/dp/max-flow-path")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error", containsString("Source wallet is required")));
    }

    // ========== GRAPH ALGORITHMS ENDPOINTS ==========

    @Test
    @DisplayName("GET /api/algorithms/graph/centrality - Análisis de centralidad")
    void testGraphCentrality() throws Exception {
        mockMvc.perform(get("/api/algorithms/graph/centrality")
                .param("topN", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.algorithm", is("BETWEENNESS_CENTRALITY")))
            .andExpect(jsonPath("$.complexity", is("O(V·E)")))
            .andExpect(jsonPath("$.topCentralWallets").isArray());
    }

    @Test
    @DisplayName("GET /api/algorithms/graph/communities - Detección de comunidades")
    void testGraphCommunities() throws Exception {
        mockMvc.perform(get("/api/algorithms/graph/communities")
                .param("minSize", "3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.algorithm", is("COMMUNITY_DETECTION")))
            .andExpect(jsonPath("$.complexity", is("O(V log V + E)")))
            .andExpect(jsonPath("$.communities").isArray())
            .andExpect(jsonPath("$.statistics").exists());
    }

    // ========== PATTERN MATCHING ENDPOINTS ==========

    @Test
    @DisplayName("POST /api/algorithms/pattern/detect-anomalies - Detección de patrones")
    void testPatternDetection() throws Exception {
        String requestBody = """
            {
                "analysisDepth": 5,
                "timeWindowDays": 30,
                "anomalyThreshold": 2.5,
                "patterns": ["MIXING", "CYCLICAL", "RAPID", "ANOMALY"]
            }
            """;

        mockMvc.perform(post("/api/algorithms/pattern/detect-anomalies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.algorithm", is("PATTERN_MATCHING")))
            .andExpect(jsonPath("$.detectedPatterns").isArray())
            .andExpect(jsonPath("$.totalAnomalies").exists())
            .andExpect(jsonPath("$.patternBreakdown").exists());
    }

    // ========== TESTS DE VALIDACIÓN ==========

    @Test
    @DisplayName("Todos los endpoints deberían retornar JSON")
    void testAllEndpointsReturnJson() throws Exception {
        // Health check
        mockMvc.perform(get("/api/algorithms/health"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Backtracking
        mockMvc.perform(get("/api/forensic/backtrack/suspicious-chains/5"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Branch & Bound
        mockMvc.perform(get("/api/path/branch-bound/w1/w2/100"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Centrality
        mockMvc.perform(get("/api/algorithms/graph/centrality"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Todos los endpoints deberían incluir timestamp")
    void testAllEndpointsIncludeTimestamp() throws Exception {
        // Backtracking
        mockMvc.perform(get("/api/forensic/backtrack/suspicious-chains/5"))
            .andExpect(jsonPath("$.timestamp").exists());

        // Branch & Bound
        mockMvc.perform(get("/api/path/branch-bound/w1/w2/100"))
            .andExpect(jsonPath("$.timestamp").exists());

        // Graph algorithms
        mockMvc.perform(get("/api/algorithms/graph/centrality"))
            .andExpect(jsonPath("$.timestamp").exists());
    }
}

