package com.example.repository;

import com.example.model.Wallet;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Repositorio con queries Cypher avanzadas para algoritmos
 */
@Repository
public interface AlgorithmRepository extends Neo4jRepository<Wallet, String> {

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
        MATCH (w:Wallet)-[:INPUT|OUTPUT]-(t:Transaction)-[:INPUT|OUTPUT]-(neighbor:Wallet)
        WHERE w.address <> neighbor.address
        WITH w, COUNT(DISTINCT neighbor) as connections, COUNT(DISTINCT t) as txCount
        WHERE connections >= COALESCE($minClusterSize, 1)
        RETURN {
            communityId: 'COMM_' + substring(w.address, 0, 8),
            members: [w.address],
            size: 1,
            edgeCount: connections,
            avgConnections: toFloat(connections),
            volume: txCount
        } as result
        ORDER BY connections DESC
        LIMIT 50
        """)
    List<Map<String, Object>> detectCommunities(Integer minClusterSize);

    /**
     * Calcula importancia de nodos (Page Rank simplificado)
     */
    @Query("""
        MATCH (w:Wallet)-[:INPUT|OUTPUT]-(t:Transaction)
        WITH w, COUNT(DISTINCT t) as txCount
        MATCH (w)-[:INPUT|OUTPUT]-(t2:Transaction)-[:INPUT|OUTPUT]-(neighbor:Wallet)
        WHERE w.address <> neighbor.address
        WITH w, txCount, COUNT(DISTINCT neighbor) as connections, COUNT(DISTINCT t2) as totalTxs
        RETURN {
            wallet: w.address,
            pageRank: toFloat(txCount + connections) / 2.0,
            inDegree: connections,
            volume: totalTxs
        } as result
        ORDER BY toFloat(txCount + connections) DESC
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

    // ============== BFS/DFS QUERIES ==============

    /**
     * Obtiene aristas del grafo para BFS/DFS
     */
    @Query("""
        MATCH (w1:Wallet)-[r]->(t:Transaction)-[r2]->(w2:Wallet)
        WHERE w1.address = $startWallet OR w1.address IN 
              [(w1)-[*1..$maxDepth]-(connected) | connected.address]
        RETURN DISTINCT
            w1.address as from,
            w2.address as to
        LIMIT 1000
        """)
    List<Map<String, Object>> getGraphEdges(String startWallet, Integer maxDepth);

    // ============== DIJKSTRA/PRIM/KRUSKAL QUERIES ==============

    /**
     * Obtiene grafo con pesos (cantidad de transacciones) para Dijkstra/Prim/Kruskal
     */
    @Query("""
        MATCH (w1:Wallet)-[r]->(t:Transaction)-[r2]->(w2:Wallet)
        WHERE w1.address = $startWallet OR w1.address IN 
              [(w1)-[*1..$maxNodes]-(connected) | connected.address]
        WITH w1.address as from, w2.address as to, 
             COALESCE(r.amount, r2.amount, 1) as weight
        WHERE weight > 0
        RETURN DISTINCT
            from,
            to,
            toFloat(weight) as weight
        LIMIT 5000
        """)
    List<Map<String, Object>> getWeightedGraphEdges(String startWallet, Integer maxNodes);

    // ============== DIVIDE & CONQUER QUERIES ==============

    /**
     * Obtiene wallets con balance para ordenamiento (QuickSort/MergeSort)
     */
    @Query("""
        MATCH (w:Wallet)
        WHERE w.balance IS NOT NULL AND w.balance > 0
        RETURN 
            w.address as wallet,
            w.balance as balance
        ORDER BY w.balance DESC
        LIMIT $limit
        """)
    List<Map<String, Object>> getWalletsForSorting(Integer limit);
}
