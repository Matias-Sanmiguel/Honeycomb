package com.example.repository;

import com.example.model.Wallet;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface WalletRepository extends Neo4jRepository<Wallet, String> {
    
    /**
     * Query 1: Análisis de Red de una Wallet
     * Encuentra todas las conexiones de una wallet específica
     */
    @Query("MATCH (start:Wallet {address: $address}) " +
           "OPTIONAL MATCH (start)-[:INPUT|OUTPUT]-(t:Transaction)-[:INPUT|OUTPUT]-(other:Wallet) " +
           "RETURN start, " +
           "COLLECT(DISTINCT t) as transactions, " +
           "COLLECT(DISTINCT other) as connectedWallets")
    Map<String, Object> analyzeNetwork(@Param("address") String address);
    
    /**
     * Encuentra todas las transacciones recientes de una wallet
     */
    @Query("MATCH (w:Wallet {address: $address})-[:INPUT|OUTPUT]-(t:Transaction) " +
           "RETURN t ORDER BY t.confirmed DESC LIMIT $limit")
    List<Map<String, Object>> findRecentTransactions(
            @Param("address") String address,
            @Param("limit") int limit
    );
    
    /**
     * Obtiene información detallada de conexiones entre wallets
     */
    @Query("MATCH (w1:Wallet {address: $address})-[r:INPUT|OUTPUT]-(t:Transaction)-[r2:INPUT|OUTPUT]-(w2:Wallet) " +
           "WHERE w1.address <> w2.address " +
           "WITH w2, " +
           "     SUM(CASE WHEN type(r) = 'OUTPUT' THEN r.value ELSE 0 END) as received, " +
           "     SUM(CASE WHEN type(r) = 'INPUT' THEN r.outputValue ELSE 0 END) as sent, " +
           "     COUNT(DISTINCT t) as txCount " +
           "RETURN w2.address as address, " +
           "       received, " +
           "       sent, " +
           "       txCount, " +
           "       CASE " +
           "         WHEN received > 0 AND sent > 0 THEN 'BOTH' " +
           "         WHEN received > 0 THEN 'RECEIVED' " +
           "         ELSE 'SENT' " +
           "       END as direction " +
           "ORDER BY (received + sent) DESC " +
           "LIMIT 50")
    List<Map<String, Object>> findConnectedWallets(@Param("address") String address);
    
    /**
     * Encuentra transacciones grandes (útil para análisis)
     */
    @Query("MATCH (w:Wallet)-[in:INPUT]->(t:Transaction) " +
           "WHERE in.outputValue > $minAmount " +
           "RETURN w.address as wallet, " +
           "       t.hash as transaction, " +
           "       in.outputValue as amount " +
           "ORDER BY in.outputValue DESC " +
           "LIMIT $limit")
    List<Map<String, Object>> findLargeTransactions(
            @Param("minAmount") long minAmount,
            @Param("limit") int limit
    );
    
    /**
     * Busca wallets por cadena
     */
    List<Wallet> findByChain(String chain);
    
    /**
     * Busca wallets con balance mayor a cierto monto
     */
    @Query("MATCH (w:Wallet) WHERE w.balance > $minBalance RETURN w")
    List<Wallet> findWalletsWithBalanceGreaterThan(@Param("minBalance") long minBalance);
}
