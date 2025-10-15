package com.example.service;

import com.example.model.Transaction;
import com.example.model.TransactionInput;
import com.example.model.TransactionOutput;
import com.example.model.Wallet;
import com.example.repository.TransactionRepository;
import com.example.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockCypherService {
    
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WebClient.Builder webClientBuilder;
    
    @Value("${blockcypher.api.base-url:https://api.blockcypher.com/v1}")
    private String baseUrl;
    
    @Value("${blockcypher.api.token:}")
    private String apiToken;
    
    /**
     * Fetch y guarda información de una wallet desde BlockCypher
     */
    public Wallet fetchAndSaveWallet(String address, String chain) {
        log.info("Fetching wallet {} from BlockCypher", address);
        
        try {
            String url = String.format("%s/%s/main/addrs/%s/full", baseUrl, chain.toLowerCase(), address);
            if (!apiToken.isEmpty()) {
                url += "?token=" + apiToken;
            }
            
            WebClient webClient = webClientBuilder.build();
            Map<String, Object> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            if (response == null) {
                throw new RuntimeException("No data received from BlockCypher");
            }
            
            return processAndSaveWalletData(response, chain);
            
        } catch (Exception e) {
            log.error("Error fetching wallet from BlockCypher: {}", e.getMessage());
            // Retornar wallet con datos mínimos
            return createMinimalWallet(address, chain);
        }
    }
    
    /**
     * Procesa los datos de BlockCypher y guarda la wallet con sus transacciones
     */
    @SuppressWarnings("unchecked")
    private Wallet processAndSaveWalletData(Map<String, Object> data, String chain) {
        String address = (String) data.get("address");
        
        // Crear la wallet
        Wallet wallet = Wallet.builder()
                .address(address)
                .chain(chain)
                .balance(getLongValue(data, "balance"))
                .totalReceived(getLongValue(data, "total_received"))
                .totalSent(getLongValue(data, "total_sent"))
                .txCount(getIntValue(data, "n_tx"))
                .riskLevel("LOW")
                .tags(new ArrayList<>())
                .inputs(new ArrayList<>())
                .outputs(new ArrayList<>())
                .build();
        
        // Procesar transacciones
        List<Map<String, Object>> txs = (List<Map<String, Object>>) data.get("txs");
        if (txs != null && !txs.isEmpty()) {
            processTransactions(wallet, txs, chain);
        }
        
        // Guardar wallet
        return walletRepository.save(wallet);
    }
    
    /**
     * Procesa y guarda las transacciones de una wallet
     */
    @SuppressWarnings("unchecked")
    private void processTransactions(Wallet wallet, List<Map<String, Object>> txsData, String chain) {
        for (Map<String, Object> txData : txsData) {
            try {
                String txHash = (String) txData.get("hash");
                
                // Verificar si ya existe
                if (transactionRepository.existsById(txHash)) {
                    continue;
                }
                
                Transaction transaction = Transaction.builder()
                        .hash(txHash)
                        .chain(chain)
                        .blockHeight(getLongValue(txData, "block_height"))
                        .blockHash((String) txData.get("block_hash"))
                        .confirmed(parseTimestamp(txData.get("confirmed")))
                        .totalInput(getLongValue(txData, "total"))
                        .totalOutput(getLongValue(txData, "total"))
                        .fees(getLongValue(txData, "fees"))
                        .confirmations(getIntValue(txData, "confirmations"))
                        .doubleSpend((Boolean) txData.getOrDefault("double_spend", false))
                        .inputs(new ArrayList<>())
                        .outputs(new ArrayList<>())
                        .build();
                
                // Procesar inputs
                List<Map<String, Object>> inputs = (List<Map<String, Object>>) txData.get("inputs");
                if (inputs != null) {
                    for (int i = 0; i < inputs.size(); i++) {
                        Map<String, Object> input = inputs.get(i);
                        List<String> addresses = (List<String>) input.get("addresses");
                        
                        if (addresses != null && !addresses.isEmpty()) {
                            TransactionInput txInput = TransactionInput.builder()
                                    .transaction(transaction)
                                    .outputValue(getLongValue(input, "output_value"))
                                    .outputIndex(i)
                                    .script((String) input.get("script"))
                                    .prevHash((String) input.get("prev_hash"))
                                    .build();
                            
                            transaction.getInputs().add(txInput);
                        }
                    }
                }
                
                // Procesar outputs
                List<Map<String, Object>> outputs = (List<Map<String, Object>>) txData.get("outputs");
                if (outputs != null) {
                    for (int i = 0; i < outputs.size(); i++) {
                        Map<String, Object> output = outputs.get(i);
                        List<String> addresses = (List<String>) output.get("addresses");
                        
                        if (addresses != null && !addresses.isEmpty()) {
                            TransactionOutput txOutput = TransactionOutput.builder()
                                    .wallet(wallet)
                                    .value(getLongValue(output, "value"))
                                    .outputIndex(i)
                                    .script((String) output.get("script"))
                                    .scriptType((String) output.get("script_type"))
                                    .spent(false)
                                    .build();
                            
                            transaction.getOutputs().add(txOutput);
                        }
                    }
                }
                
                // Guardar transacción
                transactionRepository.save(transaction);
                
            } catch (Exception e) {
                log.error("Error processing transaction: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Crea una wallet con datos mínimos cuando no se puede fetch de la API
     */
    private Wallet createMinimalWallet(String address, String chain) {
        return walletRepository.save(Wallet.builder()
                .address(address)
                .chain(chain)
                .balance(0L)
                .totalReceived(0L)
                .totalSent(0L)
                .txCount(0)
                .riskLevel("UNKNOWN")
                .tags(new ArrayList<>())
                .inputs(new ArrayList<>())
                .outputs(new ArrayList<>())
                .build());
    }
    
    /**
     * Helpers para parsear datos
     */
    private Long getLongValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return 0L;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }
    
    private Integer getIntValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return 0;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }
    
    private LocalDateTime parseTimestamp(Object timestamp) {
        if (timestamp == null) return null;
        try {
            if (timestamp instanceof String) {
                return LocalDateTime.parse((String) timestamp);
            } else if (timestamp instanceof Long) {
                return LocalDateTime.ofInstant(
                        Instant.ofEpochSecond((Long) timestamp),
                        ZoneId.systemDefault()
                );
            }
        } catch (Exception e) {
            log.warn("Could not parse timestamp: {}", timestamp);
        }
        return null;
    }
}
