// Script para cargar datos de prueba en Neo4j
// Ejecutar desde Neo4j Browser o cypher-shell

// Limpiar datos existentes
MATCH (n) DETACH DELETE n;

// Crear todo en una sola transacción para mantener las variables activas
CREATE
// ==================== WALLETS ====================
(w1:Wallet {
    address: '1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa',
    balance: 50.0,
    totalReceived: 100.0,
    totalSent: 50.0,
    txCount: 5,
    firstSeen: datetime('2023-01-01T10:00:00'),
    lastSeen: datetime('2023-12-31T15:30:00'),
    risk_score: 0.2
}),
(w2:Wallet {
    address: '1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2',
    balance: 25.5,
    totalReceived: 75.5,
    totalSent: 50.0,
    txCount: 8,
    firstSeen: datetime('2023-02-15T12:00:00'),
    lastSeen: datetime('2023-12-30T14:20:00'),
    risk_score: 0.5
}),
(w3:Wallet {
    address: '3J98t1WpEZ73CNmYviecrnyiWrnqRhWNLy',
    balance: 100.0,
    totalReceived: 200.0,
    totalSent: 100.0,
    txCount: 15,
    firstSeen: datetime('2023-01-10T09:00:00'),
    lastSeen: datetime('2023-12-29T16:45:00'),
    risk_score: 0.8
}),
(w4:Wallet {
    address: 'bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh',
    balance: 75.25,
    totalReceived: 150.0,
    totalSent: 74.75,
    txCount: 12,
    firstSeen: datetime('2023-03-01T11:30:00'),
    lastSeen: datetime('2023-12-28T13:15:00'),
    risk_score: 0.3
}),
(w5:Wallet {
    address: '1FeexV6bAHb8ybZjqQMjJrcCrHGW9sb6uF',
    balance: 10.0,
    totalReceived: 30.0,
    totalSent: 20.0,
    txCount: 6,
    firstSeen: datetime('2023-04-20T14:00:00'),
    lastSeen: datetime('2023-12-27T10:30:00'),
    risk_score: 0.6
}),
(w6:Wallet {
    address: '3FZbgi29cpjq2GjdwV8eyHuJJnkLtktZc5',
    balance: 5.5,
    totalReceived: 15.5,
    totalSent: 10.0,
    txCount: 4,
    firstSeen: datetime('2023-05-10T16:20:00'),
    lastSeen: datetime('2023-12-26T12:00:00'),
    risk_score: 0.4
}),
(w7:Wallet {
    address: 'bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4',
    balance: 200.0,
    totalReceived: 500.0,
    totalSent: 300.0,
    txCount: 25,
    firstSeen: datetime('2023-01-05T08:00:00'),
    lastSeen: datetime('2023-12-31T18:00:00'),
    risk_score: 0.9
}),
(w8:Wallet {
    address: '1NDyJtNTjmwk5xPNhjgAMu4HDHigtobu1s',
    balance: 15.75,
    totalReceived: 40.0,
    totalSent: 24.25,
    txCount: 9,
    firstSeen: datetime('2023-06-15T10:45:00'),
    lastSeen: datetime('2023-12-25T15:30:00'),
    risk_score: 0.35
}),

// ==================== TRANSACTIONS ====================
(t1:Transaction {
    hash: 'a1075db55d416d3ca199f55b6084e2115b9345e16c5cf302fc80e9d5fbf5d48d',
    confirmed: datetime('2023-03-15T14:30:00'),
    fee: 0.0001,
    size: 250,
    blockHeight: 780000,
    confirmations: 5000
}),
(t2:Transaction {
    hash: 'b2186eb66e427d4da209f75c7195f2116c8456f27d6dg413gd91faeff6g6e59e',
    confirmed: datetime('2023-04-20T16:45:00'),
    fee: 0.00015,
    size: 300,
    blockHeight: 781500,
    confirmations: 4500
}),
(t3:Transaction {
    hash: 'c3297fc77f538e5eb320g86d8206g3227d9567g38e7eh524he02gbfgg7h7f60f',
    confirmed: datetime('2023-05-25T11:20:00'),
    fee: 0.0002,
    size: 350,
    blockHeight: 783000,
    confirmations: 4000
}),
(t4:Transaction {
    hash: 'd4308gd88g649f6fc431h97e9317h4338e0678h49f8fi635if13hcghh8i8g71g',
    confirmed: datetime('2023-06-30T09:15:00'),
    fee: 0.00012,
    size: 280,
    blockHeight: 784500,
    confirmations: 3500
}),
(t5:Transaction {
    hash: 'e5419he99h750g7gd542i08f0428i5449f1789i50g9gj746jg24idhii9j9h82h',
    confirmed: datetime('2023-07-10T13:40:00'),
    fee: 0.00018,
    size: 320,
    blockHeight: 785000,
    confirmations: 3200
}),

// ==================== RELACIONES ====================
// Wallet 1 -> Transaction 1 -> Wallet 2, Wallet 3
(w1)-[:INPUT {amount: 20.0, timestamp: datetime('2023-03-15T14:30:00'), index: 0}]->(t1),
(t1)-[:OUTPUT {amount: 10.0, timestamp: datetime('2023-03-15T14:30:00'), index: 0}]->(w2),
(t1)-[:OUTPUT {amount: 9.5, timestamp: datetime('2023-03-15T14:30:00'), index: 1}]->(w3),

// Wallet 2 -> Transaction 2 -> Wallet 4, Wallet 5
(w2)-[:INPUT {amount: 15.0, timestamp: datetime('2023-04-20T16:45:00'), index: 0}]->(t2),
(t2)-[:OUTPUT {amount: 8.0, timestamp: datetime('2023-04-20T16:45:00'), index: 0}]->(w4),
(t2)-[:OUTPUT {amount: 6.5, timestamp: datetime('2023-04-20T16:45:00'), index: 1}]->(w5),

// Wallet 3 -> Transaction 3 -> Wallet 4, Wallet 6
(w3)-[:INPUT {amount: 50.0, timestamp: datetime('2023-05-25T11:20:00'), index: 0}]->(t3),
(t3)-[:OUTPUT {amount: 30.0, timestamp: datetime('2023-05-25T11:20:00'), index: 0}]->(w4),
(t3)-[:OUTPUT {amount: 19.5, timestamp: datetime('2023-05-25T11:20:00'), index: 1}]->(w6),

// Wallet 4 -> Transaction 4 -> Wallet 7, Wallet 8
(w4)-[:INPUT {amount: 30.0, timestamp: datetime('2023-06-30T09:15:00'), index: 0}]->(t4),
(t4)-[:OUTPUT {amount: 20.0, timestamp: datetime('2023-06-30T09:15:00'), index: 0}]->(w7),
(t4)-[:OUTPUT {amount: 9.5, timestamp: datetime('2023-06-30T09:15:00'), index: 1}]->(w8),

// Wallet 5 -> Transaction 5 -> Wallet 6, Wallet 7
(w5)-[:INPUT {amount: 10.0, timestamp: datetime('2023-07-10T13:40:00'), index: 0}]->(t5),
(t5)-[:OUTPUT {amount: 6.0, timestamp: datetime('2023-07-10T13:40:00'), index: 0}]->(w6),
(t5)-[:OUTPUT {amount: 3.5, timestamp: datetime('2023-07-10T13:40:00'), index: 1}]->(w7);

// ==================== VERIFICACIÓN ====================

// Verificar wallets creadas
MATCH (w:Wallet) RETURN count(w) as totalWallets;

// Verificar transacciones creadas
MATCH (t:Transaction) RETURN count(t) as totalTransactions;

// Verificar relaciones INPUT
MATCH ()-[r:INPUT]->() RETURN count(r) as totalInputs;

// Verificar relaciones OUTPUT
MATCH ()-[r:OUTPUT]->() RETURN count(r) as totalOutputs;

// Ver el grafo completo
MATCH (w:Wallet)-[r]-(t:Transaction) RETURN w, r, t LIMIT 100;
