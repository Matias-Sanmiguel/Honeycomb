package com.example.repository;

import com.example.model.Wallet;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repository para queries de análisis de caminos (paths) entre wallets
 */
@Repository
public interface PathAnalysisRepository extends Neo4jRepository<Wallet, String> {
    
    /**
     * Query 3: Encuentra el camino más corto entre dos wallets
     * Usa el algoritmo shortestPath de Neo4j
     */
    @Query("MATCH (w1:Wallet {address: $address1}) " +
           "MATCH (w2:Wallet {address: $address2}) " +
           "MATCH path = shortestPath((w1)-[:INPUT|OUTPUT*..10]-(w2)) " +
           "RETURN path, " +
           "       length(path) as pathLength, " +
           "       [n in nodes(path) | {address: n.address, labels: labels(n)}] as nodes, " +
           "       [r in relationships(path) | {type: type(r), value: COALESCE(r.value, r.outputValue)}] as relationships")
    Map<String, Object> findShortestPath(
            @Param("address1") String address1,
            @Param("address2") String address2
    );
    
    /**
     * Encuentra todos los caminos cortos entre dos wallets (hasta cierta longitud)
     */
    @Query("MATCH (w1:Wallet {address: $address1}) " +
           "MATCH (w2:Wallet {address: $address2}) " +
           "MATCH path = (w1)-[:INPUT|OUTPUT*..5]-(w2) " +
           "WHERE length(path) <= $maxLength " +
           "RETURN path, " +
           "       length(path) as pathLength " +
           "ORDER BY length(path) ASC " +
           "LIMIT 10")
    List<Map<String, Object>> findAllShortPaths(
            @Param("address1") String address1,
            @Param("address2") String address2,
            @Param("maxLength") int maxLength
    );
    
    /**
     * Encuentra wallets dentro de N saltos de una wallet dada
     */
    @Query("MATCH (w:Wallet {address: $address})-[:INPUT|OUTPUT*1..$hops]-(connected:Wallet) " +
           "WHERE w.address <> connected.address " +
           "RETURN DISTINCT connected.address as address, " +
           "       connected.balance as balance, " +
           "       connected.txCount as txCount " +
           "LIMIT 100")
    List<Map<String, Object>> findWalletsWithinHops(
            @Param("address") String address,
            @Param("hops") int hops
    );
    
    /**
     * Análisis de centralidad: encuentra wallets más conectadas
     */
    @Query("MATCH (w:Wallet)-[:INPUT|OUTPUT]-() " +
           "WITH w, COUNT(*) as connections " +
           "WHERE connections > $minConnections " +
           "RETURN w.address as address, " +
           "       connections, " +
           "       w.balance as balance " +
           "ORDER BY connections DESC " +
           "LIMIT $limit")
    List<Map<String, Object>> findMostConnectedWallets(
            @Param("minConnections") int minConnections,
            @Param("limit") int limit
    );
}
