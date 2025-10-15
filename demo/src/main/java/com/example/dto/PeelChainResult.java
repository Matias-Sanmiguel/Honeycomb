package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resultado de detección de Peel Chain
 * Identifica transacciones donde se gasta casi todo el balance
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeelChainResult {
    
    private String wallet;
    
    private String transaction;
    
    private Long inputAmount;
    
    private Long outputsTotal;
    
    // Porcentaje gastado
    private Double peelPercentage;
    
    // Dirección que recibió la mayoría
    private String mainRecipient;
    
    // Monto que recibió el destinatario principal
    private Long mainRecipientAmount;
    
    // Dirección del "cambio" (lo que sobra)
    private String changeAddress;
    
    // Monto del cambio
    private Long changeAmount;
    
    public void calculatePeelPercentage() {
        if (inputAmount != null && inputAmount > 0) {
            this.peelPercentage = (outputsTotal.doubleValue() / inputAmount.doubleValue()) * 100;
        }
    }
}
