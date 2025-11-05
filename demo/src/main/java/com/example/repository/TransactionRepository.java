package com.example.repository;

import com.example.model.Transaction;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TransactionRepository extends Neo4jRepository<Transaction, String> {
    
    /**
     * Query 2: Detección de Peel Chain
     * Encuentra transacciones donde se gasta casi todo el balance (>95%)
     */
    @Query("MATCH (w:Wallet)-[in:INPUT]->(t:Transaction) " +
           "WITH w, t, in.amount as input_amount, " +
           "     [(t)-[out:OUTPUT]->(receiver:Wallet) | out.amount] as output_values " +
           "WITH w, t, input_amount, " +
           "     reduce(total = 0, val in output_values | total + val) as outputs_total " +
           "WHERE outputs_total > input_amount * 0.95 " +
           "RETURN w.address as wallet, " +
           "       t.hash as transaction, " +
           "       input_amount as inputAmount, " +
           "       outputs_total as outputsTotal " +
           "ORDER BY input_amount DESC " +
           "LIMIT 100")
    List<Map<String, Object>> detectPeelChains();
    
    /**
     * Variante mejorada con información adicional de peel chain
     */
    @Query("MATCH (w:Wallet)-[in:INPUT]->(t:Transaction) " +
           "MATCH (t)-[out:OUTPUT]->(receiver:Wallet) " +
           "WITH t, w.address as wallet_addr, " +
           "     SUM(in.amount) as total_input, " +
           "     SUM(out.amount) as total_output, " +
           "     COLLECT(DISTINCT receiver.address)[0..2] as recipients, " +
           "     COLLECT(DISTINCT out.amount)[0..2] as amounts " +
           "WHERE total_output > total_input * $threshold AND total_input > 0 " +
           "RETURN wallet_addr as wallet, " +
           "       t.hash as transaction, " +
           "       total_input as inputAmount, " +
           "       total_output as outputsTotal, " +
           "       recipients[0] as mainRecipient, " +
           "       amounts[0] as mainRecipientAmount, " +
           "       CASE WHEN size(recipients) > 1 THEN recipients[1] ELSE null END as changeAddress, " +
           "       CASE WHEN size(amounts) > 1 THEN amounts[1] ELSE null END as changeAmount " +
           "ORDER BY total_input DESC " +
           "LIMIT $limit")
    List<Map<String, Object>> detectPeelChainsDetailed(
            @Param("threshold") double threshold,
            @Param("limit") int limit
    );
    
    /**
     * Buscar transacciones por rango de montos
     */
    @Query("MATCH (t:Transaction) " +
           "WHERE t.totalOutput >= $minAmount AND t.totalOutput <= $maxAmount " +
           "RETURN t ORDER BY t.confirmed DESC LIMIT $limit")
    List<Transaction> findByAmountRange(
            @Param("minAmount") long minAmount,
            @Param("maxAmount") long maxAmount,
            @Param("limit") int limit
    );
    
    /**
     * Encuentra transacciones entre dos wallets específicas
     */
    @Query("MATCH (w1:Wallet {address: $address1})-[:INPUT|OUTPUT]-(t:Transaction)-[:INPUT|OUTPUT]-(w2:Wallet {address: $address2}) " +
           "RETURN DISTINCT t ORDER BY t.confirmed DESC LIMIT 20")
    List<Transaction> findTransactionsBetweenWallets(
            @Param("address1") String address1,
            @Param("address2") String address2
    );
    
    /**
     * Buscar transacciones con double spend
     */
    @Query("MATCH (t:Transaction) WHERE t.doubleSpend = true RETURN t ORDER BY t.confirmed DESC LIMIT 50")
    List<Transaction> findDoubleSpendTransactions();

    //Encuentra las wallets más activas (para análisis de ciclos)
    @Query("MATCH (w:Wallet)-[r]-() " +
           "WITH w, COUNT(DISTINCT r) as txCount " +
           "WHERE txCount > 5 " +
           "RETURN w.address as wallet, txCount " +
           "ORDER BY txCount DESC " +
           "LIMIT $limit")
    List<Map<String, Object>> findMostActiveWallets(@Param("limit") int limit);

     //Ejecuta una query Cypher personalizada
     //Útil para queries dinámicas de algoritmos
    @Query("$query")
    List<Map<String, Object>> executeCustomQuery(
            @Param("query") String query,
            @Param("params") Map<String, Object> params
    );
}
