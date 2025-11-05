package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO para el resultado de la query de paths de Neo4j
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PathQueryResult {
    private Integer pathLength;
    private List<Map<String, Object>> nodes;
    private List<Map<String, Object>> relationships;
}

