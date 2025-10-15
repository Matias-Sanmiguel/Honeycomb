package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * Representa la relación OUTPUT entre una Transaction y una Wallet
 * En el grafo: (Transaction)-[:OUTPUT {properties}]->(Wallet)
 */
@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionOutput {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @TargetNode
    private Wallet wallet;
    
    // Valor enviado a esta wallet
    private Long value;
    
    // Índice del output en la transacción
    private Integer outputIndex;
    
    // Script de bloqueo
    private String script;
    
    // Tipo de script (pay-to-pubkey-hash, etc.)
    private String scriptType;
    
    // Si el output ya fue gastado
    private Boolean spent;
    
    // Hash de la transacción que lo gastó
    private String spentBy;
}
