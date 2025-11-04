package com.example.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repositorio con queries Cypher avanzadas para algoritmos
 */
@Repository
public interface AlgorithmRepository extends Neo4jRepository<Object, String> {

    // ============== GREEDY QUERIES ==============

    /**
     * Obtiene candidatos a peel chains: wallets que gastan > threshold del input
     */
    @Query("""
        MATCH (w:Wallet)-[input:INPUT]->(t:Transaction)-[output:OUTPUT]->(recipients:Wallet)
        WITH w, t, SUM(input.amount) as inputAmount, SUM(output.amount) as outputsTotal
        WHERE outputsTotal > 0 AND inputAmount > 0
        WITH w, t, inputAmount, outputsTotal, toFloat(outputsTotal) / toFloat(inputAmount) as spendingPercent
        WHERE spendingPercent > $threshold
        RETURN 
            w.address as wallet,
            t.hash as transaction,
            inputAmount,
            outputsTotal,
            spendingPercent,
            COUNT(recipients) as recipientCount
        ORDER BY spendingPercent DESC
        """)
    List<Map<String, Object>> getPeelChainCandidates(Double threshold);

    /**
     * Detecta cadenas de peel chains (múltiples transacciones secuenciales)
     */
    @Query("""
        MATCH (w1:Wallet)-[i1:INPUT]->(t1:Transaction)-[o1:OUTPUT]->(w2:Wallet),
              (w2)-[i2:INPUT]->(t2:Transaction)-[o2:OUTPUT]->(w3:Wallet)
        WITH w1, w2, w3, t1, t2, SUM(i1.amount) as in1, SUM(o1.amount) as out1,
                             SUM(i2.amount) as in2, SUM(o2.amount) as out2
        WHERE out1 > 0 AND in1 > 0 AND out2 > 0 AND in2 > 0
        WITH w1, w2, w3, t1, t2, 
             toFloat(out1) / toFloat(in1) as spend1,
             toFloat(out2) / toFloat(in2) as spend2
        WHERE spend1 > $threshold AND spend2 > $threshold
        WITH w2, COUNT(DISTINCT w1) as inputCount, COUNT(DISTINCT w3) as outputCount,
             SUM(out1) as chainVolume
        WHERE inputCount + outputCount >= $minChainLength
        RETURN 
            w2.address as wallet,
            inputCount + outputCount as chainLength,
            chainVolume as totalAmount,
            (toFloat(outputCount) / toFloat(inputCount + outputCount)) as spendingPercentage
        ORDER BY chainLength DESC
        LIMIT $limit
        """)
    List<Map<String, Object>> getPeelChainClusters(Double threshold, Integer minChainLength);

    // ============== DYNAMIC PROGRAMMING QUERIES ==============

    /**
     * Encuentra caminos entre dos wallets con valores de transacciones
     * Usado para DP: maxFlowPath
     */
    @Query("""
        MATCH path = (source:Wallet {address: $sourceWallet})-[*1..$maxHops]->(target:Wallet {address: $targetWallet})
        WHERE ALL(rel IN relationships(path) WHERE type(rel) IN ['INPUT', 'OUTPUT'])
        UNWIND relationships(path) as rel
        WITH startNode(rel).address as from, endNode(rel).address as to,
             rel.amount as amount, rel.txHash as txHash, rel.timestamp as timestamp
        RETURN from, to, amount, txHash, timestamp
        LIMIT 1000
        """)
    List<Map<String, Object>> findPathsWithValues(String sourceWallet, String targetWallet, Integer maxHops);

    // ============== GRAPH ALGORITHMS QUERIES ==============

    /**
     * Calcula Betweenness Centrality (requiere APOC en Neo4j)
     * Identifica wallets puente críticas
     */
    @Query("""
        MATCH (w:Wallet)
        WITH collect(w) as wallets
        UNWIND wallets as wallet
        MATCH (wallet)-[r]-()
        WITH wallet, COUNT(DISTINCT r) as degree
        ORDER BY degree DESC
        LIMIT $topN
        MATCH (wallet)-[rels*..100]-(other:Wallet)
        WITH wallet, COUNT(DISTINCT other) as bridgeConnections, SUM(r.amount) as volume
        RETURN 
            wallet.address as wallet,
            toFloat(bridgeConnections) / 1000 as betweenness,
            bridgeConnections,
            volume
        ORDER BY betweenness DESC
        """)
    List<Map<String, Object>> calculateBetweennessCentrality(Integer topN);

    /**
     * Detecta comunidades de wallets
     * Identifica clusters de nodos altamente interconectados
     */
    @Query("""
        MATCH (w:Wallet)-[r]-(neighbor:Wallet)
        WITH w, COUNT(DISTINCT neighbor) as connections, SUM(r.amount) as volume
        WITH w, connections, volume
        WHERE connections >= $minClusterSize
        WITH w.address as communityId, COLLECT(w.address) as members,
             COUNT(w) as size, SUM(connections) as edgeCount,
             AVG(connections) as avgConnections, SUM(volume) as volume
        RETURN 
            'COMM_' + substring(communityId, 0, 5) as communityId,
            members,
            size,
            edgeCount,
            avgConnections,
            volume
        ORDER BY size DESC
        LIMIT 50
        """)
    List<Map<String, Object>> detectCommunities(Integer minClusterSize);

    /**
     * Calcula importancia de nodos (Page Rank simplificado)
     */
    @Query("""
        MATCH (w:Wallet)
        WITH w, size([(w)-[r]-() | r]) as degree, sum(r.amount) as volume
        MATCH (w)-[incoming]-(neighbor)
        WITH w, degree, volume, COUNT(DISTINCT neighbor) as inDegree, SUM(incoming.amount) as inVolume
        RETURN 
            w.address as wallet,
            toFloat(degree + inDegree) / 2.0 as pageRank,
            inDegree,
            COALESCE(volume, 0) as volume
        ORDER BY pageRank DESC
        LIMIT $topN
        """)
    List<Map<String, Object>> calculateNodeImportance(Integer topN);

    // ============== PATTERN MATCHING QUERIES ==============

    /**
     * Detecta patrones de mixing: wallet envía a múltiples direcciones que convergen
     */
    @Query("""
        MATCH (source:Wallet)-[out:OUTPUT]->(t1:Transaction)-[in1:INPUT]->(intermediate:Wallet),
              (intermediate)-[out2:OUTPUT]->(t2:Transaction)-[in2:INPUT]->(sink:Wallet)
        WITH source, COLLECT(DISTINCT intermediate.address) as intermediates,
             COLLECT(DISTINCT sink.address) as sinks,
             COUNT(DISTINCT t1) as inputCount,
             COUNT(DISTINCT t2) as outputCount,
             SUM(in1.amount) as amount
        WHERE size(intermediates) >= 5 AND size(sinks) >= 3
        RETURN 
            source.address as sourceWallet,
            intermediates as wallets,
            inputCount,
            outputCount,
            amount
        LIMIT 100
        """)
    List<Map<String, Object>> detectMixingPatterns(Integer depth);

    /**
     * Detecta ciclos en el grafo de transacciones
     */
    @Query("""
        MATCH cycle = (w:Wallet)-[*2..10]-(w)
        WHERE size(relationships(cycle)) >= 2
        WITH w, relationships(cycle) as rels, cycle
        WITH w.address as wallet, COLLECT(nodes(cycle)[0..size(nodes(cycle))-1]) as nodeList, 
             SIZE(relationships(cycle)) as cycleLength,
             SUM(r.amount) for r IN rels as amount
        RETURN 
            [node in nodeList | node.address] as cycle,
            cycleLength,
            amount
        LIMIT 100
        """)
    List<Map<String, Object>> detectCycles(Integer maxDepth);

    /**
     * Detecta transacciones rápidas (múltiples en corto tiempo)
     */
    @Query("""
        MATCH (t:Transaction)
        WHERE t.confirmed IS NOT NULL
        WITH t.confirmed as timeWindow, COLLECT(t.hash) as transactions,
             COUNT(t) as txCount, 
             max(t.confirmed) - min(t.confirmed) as timeSpan
        WHERE txCount >= 3
        RETURN 
            transactions[0] as representative,
            txCount as transactionCount,
            CASE WHEN timeSpan IS NULL THEN 0 ELSE timeSpan END as timeSpan
        ORDER BY txCount DESC
        LIMIT 50
        """)
    List<Map<String, Object>> detectRapidTransactions(Long timeWindowSeconds);

    /**
     * Obtiene montos de transacciones para análisis de outliers
     */
    @Query("""
        MATCH (w:Wallet)-[output:OUTPUT]->(t:Transaction)
        RETURN 
            w.address as wallet,
            output.amount as amount,
            t.hash as txHash
        LIMIT 10000
        """)
    List<Map<String, Object>> getTransactionAmounts();
}

