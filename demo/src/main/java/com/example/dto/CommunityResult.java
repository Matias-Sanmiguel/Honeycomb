package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Resultado de análisis de Comunidades/Clusters
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityResult {
    
    private String communityId;
    private Integer size;
    private Double density;
    private Long totalVolume;
    private List<String> members;
    private String suspiciousLevel;
    private Double averageConnections;
    private Integer edgeCount;
}

