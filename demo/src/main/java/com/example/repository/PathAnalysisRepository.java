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
     * Modelo: Wallet -[:INPUT]-> Transaction -[:OUTPUT]-> Wallet
     */
    @Query("MATCH (w1:Wallet {address: $address1}), (w2:Wallet {address: $address2}) " +
            "MATCH path = shortestPath((w1)-[:INPUT|OUTPUT*..20]-(w2)) " +
            "WITH path, " +
            "     length(path) as pathLength, " +
            "     [n in nodes(path) | n] as pathNodes, " +
            "     relationships(path) as pathRels " +
            "WHERE pathLength > 0 AND pathLength % 2 = 0 " +
            "RETURN path, " +
            "       pathLength, " +
            "       [i in range(0, size(pathNodes)-1) | " +
            "           CASE WHEN 'Wallet' IN labels(pathNodes[i]) THEN {address: pathNodes[i].address, labels: labels(pathNodes[i])} " +
            "                WHEN 'Transaction' IN labels(pathNodes[i]) THEN {hash: pathNodes[i].hash, labels: labels(pathNodes[i])} " +
            "           END " +
            "       ] as nodes, " +
            "       [r in pathRels | {type: type(r), amount: r.amount}] as relationships")
    Map<String, Object> findShortestPath(
            @Param("address1") String address1,
            @Param("address2") String address2
    );
    
    /**
     * Encuentra todos los caminos cortos entre dos wallets (hasta cierta longitud)
     * Longitud debe ser par (Wallet -> INPUT -> Tx -> OUTPUT -> Wallet = 2 relaciones por salto)
     */
    @Query("MATCH (w1:Wallet {address: $address1}), (w2:Wallet {address: $address2}) " +
           "MATCH path = allShortestPaths((w1)-[:INPUT|OUTPUT*..20]-(w2)) " +
           "WHERE length(path) <= ($maxLength * 2) AND length(path) % 2 = 0 AND length(path) > 0 " +
           "WITH path, length(path) as pathLength " +
           "RETURN path, pathLength " +
           "ORDER BY pathLength ASC " +
           "LIMIT 10")
    List<Map<String, Object>> findAllShortPaths(
            @Param("address1") String address1,
            @Param("address2") String address2,
            @Param("maxLength") int maxLength
    );
    
    /**
     * Encuentra wallets dentro de N saltos de una wallet dada
     * hops se multiplica por 2 porque cada salto real son 2 relaciones (INPUT -> Transaction -> OUTPUT)
     */
    @Query("MATCH (w:Wallet {address: $address})-[:INPUT|OUTPUT*..20]-(connected:Wallet) " +
           "WHERE w.address <> connected.address " +
           "WITH DISTINCT connected, " +
           "     shortestPath((w)-[:INPUT|OUTPUT*]-(connected)) as path " +
           "WHERE length(path) <= ($hops * 2) AND length(path) % 2 = 0 " +
           "RETURN DISTINCT connected.address as address, " +
           "       connected.balance as balance, " +
           "       connected.txCount as txCount, " +
           "       length(path) / 2 as hops " +
           "ORDER BY hops ASC " +
           "LIMIT 100")
    List<Map<String, Object>> findWalletsWithinHops(
            @Param("address") String address,
            @Param("hops") int hops
    );
    
    /**
     * Análisis de centralidad: encuentra wallets más conectadas
     */
    @Query("MATCH (w:Wallet)-[:INPUT|OUTPUT]-(t:Transaction) " +
           "WITH w, COUNT(DISTINCT t) as connections " +
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
