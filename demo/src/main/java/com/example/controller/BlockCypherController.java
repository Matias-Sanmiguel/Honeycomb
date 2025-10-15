package com.example.controller;

import com.example.model.Wallet;
import com.example.service.BlockCypherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para operaciones con BlockCypher API
 */
@RestController
@RequestMapping("/api/blockcypher")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BlockCypherController {
    
    private final BlockCypherService blockCypherService;
    
    /**
     * Fetch y guarda información de una wallet desde BlockCypher
     * POST /api/blockcypher/wallet/{address}?chain=BTC
     */
    @PostMapping("/wallet/{address}")
    public ResponseEntity<Wallet> fetchWallet(
            @PathVariable String address,
            @RequestParam(defaultValue = "BTC") String chain) {
        log.info("Fetching wallet {} from chain {}", address, chain);
        Wallet wallet = blockCypherService.fetchAndSaveWallet(address, chain);
        return ResponseEntity.ok(wallet);
    }
    
    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("BlockCypher service is running");
    }
}
