package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("Transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    
    @Id
    private String hash;
    
    private String chain;
    
    private Long blockHeight;
    
    private String blockHash;
    
    private String confirmed; // Cambiado de LocalDateTime a String para evitar problemas de conversión

    private Long totalInput;
    
    private Long totalOutput;
    
    private Long fees;
    
    private Integer confirmations;
    
    private Boolean doubleSpend;
    
    // Inputs: wallets que gastan en esta transacción
    @Relationship(type = "INPUT", direction = Relationship.Direction.INCOMING)
    @Builder.Default
    private List<TransactionInput> inputs = new ArrayList<>();
    
    // Outputs: wallets que reciben de esta transacción
    @Relationship(type = "OUTPUT", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<TransactionOutput> outputs = new ArrayList<>();
}
