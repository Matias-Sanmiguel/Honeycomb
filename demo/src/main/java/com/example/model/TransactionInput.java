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
 * Representa la relación INPUT entre una Wallet y una Transaction
 * En el grafo: (Wallet)-[:INPUT {properties}]->(Transaction)
 */
@RelationshipProperties
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionInput {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @TargetNode
    private Transaction transaction;
    
    // Valor gastado de esta wallet en la transacción
    private Long outputValue;
    
    // Índice del output previo que se está gastando
    private Integer outputIndex;
    
    // Script de firma
    private String script;
    
    // Dirección del output anterior
    private String prevHash;
}
