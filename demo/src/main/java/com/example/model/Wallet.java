package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Node("Wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {
    
    @Id
    private String address;
    
    private String chain; // BTC, ETH, etc.
    
    private Long totalReceived;
    
    private Long totalSent;
    
    private Long balance;
    
    private Integer txCount;
    
    private LocalDateTime firstSeen;
    
    private LocalDateTime lastSeen;
    
    // Relaciones salientes (como INPUT de transacciones)
    @Relationship(type = "INPUT", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<TransactionInput> inputs = new ArrayList<>();
    
    // Relaciones entrantes (como OUTPUT de transacciones)
    @Relationship(type = "OUTPUT", direction = Relationship.Direction.INCOMING)
    @Builder.Default
    private List<TransactionOutput> outputs = new ArrayList<>();
    
    // Tags opcionales para marcar wallets conocidas
    private List<String> tags;
    
    private String riskLevel; // LOW, MEDIUM, HIGH
}
