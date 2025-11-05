package com.example.repository;

import com.example.model.Wallet;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WalletRepository extends Neo4jRepository<Wallet, String> {

    /**
     * Query 1: Análisis de Red de una Wallet
     * Encuentra todas las conexiones de una wallet específica.
     *
     * Se devuelve una lista de mapas para evitar errores de mapeo automático
     * cuando Neo4j retorna múltiples valores (nodos y relaciones).
     */
    @Query("MATCH (start:Wallet {address: $address}) " +
            "OPTIONAL MATCH (start)-[:INPUT|OUTPUT]-(t:Transaction)-[:INPUT|OUTPUT]-(other:Wallet) " +
            "WHERE start.address <> other.address " +
            "RETURN {address: start.address, balance: start.balance, txCount: start.txCount} AS wallet, " +
            "       COLLECT(DISTINCT {hash: t.hash, confirmed: t.confirmed, blockHeight: t.blockHeight}) AS transactions, " +
            "       COLLECT(DISTINCT {address: other.address, balance: other.balance}) AS connectedWallets")
    List<Map<String, Object>> analyzeNetwork(@Param("address") String address);


    /**
     * Encuentra todas las transacciones recientes de una wallet
     */
    @Query("MATCH (w:Wallet {address: $address})-[r:INPUT|OUTPUT]-(t:Transaction) " +
            "RETURN t.hash AS hash, " +
            "       t.confirmed AS timestamp, " +
            "       t.blockHeight AS blockHeight, " +
            "       COALESCE(r.amount, 0) AS amount, " +
            "       type(r) AS type " +
            "ORDER BY t.confirmed DESC LIMIT $limit")
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
            "     SUM(CASE WHEN type(r2) = 'OUTPUT' THEN r2.amount ELSE 0 END) AS received, " +
            "     SUM(CASE WHEN type(r) = 'INPUT' THEN r.amount ELSE 0 END) AS sent, " +
            "     COUNT(DISTINCT t) AS txCount " +
            "RETURN w2.address AS address, " +
            "       received, " +
            "       sent, " +
            "       txCount, " +
            "       CASE " +
            "         WHEN received > 0 AND sent > 0 THEN 'BOTH' " +
            "         WHEN received > 0 THEN 'RECEIVED' " +
            "         ELSE 'SENT' " +
            "       END AS direction " +
            "ORDER BY (received + sent) DESC " +
            "LIMIT 50")
    List<Map<String, Object>> findConnectedWallets(@Param("address") String address);


    /**
     * Encuentra transacciones grandes (útil para análisis)
     */
    @Query("MATCH (w:Wallet)-[in:INPUT]->(t:Transaction) " +
            "WHERE in.amount > $minAmount " +
            "RETURN w.address AS wallet, " +
            "       t.hash AS transaction, " +
            "       in.amount AS amount " +
            "ORDER BY in.amount DESC " +
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
