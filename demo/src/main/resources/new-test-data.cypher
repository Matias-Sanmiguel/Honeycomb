// Limpiar datos existentes
MATCH (n) DETACH DELETE n;

// Crear wallets de prueba
CREATE (w1:Wallet {
    address: 'wallet_alice',
    chain: 'BTC',
    balance: 5000000,
    totalReceived: 10000000,
    totalSent: 5000000,
    txCount: 15,
    riskLevel: 'LOW',
    tags: []
});

CREATE (w2:Wallet {
    address: 'wallet_bob',
    chain: 'BTC',
    balance: 3000000,
    totalReceived: 8000000,
    totalSent: 5000000,
    txCount: 12,
    riskLevel: 'MEDIUM',
    tags: ['exchange']
});

CREATE (w3:Wallet {
    address: 'wallet_charlie',
    chain: 'BTC',
    balance: 1000000,
    totalReceived: 5000000,
    totalSent: 4000000,
    txCount: 8,
    riskLevel: 'HIGH',
    tags: ['suspicious']
});

CREATE (w4:Wallet {
    address: 'wallet_diana',
    chain: 'BTC',
    balance: 7500000,
    totalReceived: 15000000,
    totalSent: 7500000,
    txCount: 20,
    riskLevel: 'LOW',
    tags: []
});

CREATE (w5:Wallet {
    address: 'wallet_eve',
    chain: 'BTC',
    balance: 2000000,
    totalReceived: 6000000,
    totalSent: 4000000,
    txCount: 10,
    riskLevel: 'HIGH',
    tags: ['mixer', 'suspicious']
});

// Crear transacciones que conectan las wallets
CREATE (tx1:Transaction {
    hash: 'tx_001',
    chain: 'BTC',
    blockHeight: 750000,
    blockHash: 'block_001',
    confirmed: '2024-01-15T10:30:00',
    totalInput: 2000000,
    totalOutput: 1990000,
    fees: 10000,
    confirmations: 100,
    doubleSpend: false
});

CREATE (tx2:Transaction {
    hash: 'tx_002',
    chain: 'BTC',
    blockHeight: 750001,
    blockHash: 'block_002',
    confirmed: '2024-01-15T11:00:00',
    totalInput: 1500000,
    totalOutput: 1490000,
    fees: 10000,
    confirmations: 99,
    doubleSpend: false
});

CREATE (tx3:Transaction {
    hash: 'tx_003',
    chain: 'BTC',
    blockHeight: 750002,
    blockHash: 'block_003',
    confirmed: '2024-01-15T11:30:00',
    totalInput: 3000000,
    totalOutput: 2990000,
    fees: 10000,
    confirmations: 98,
    doubleSpend: false
});

CREATE (tx4:Transaction {
    hash: 'tx_004',
    chain: 'BTC',
    blockHeight: 750003,
    blockHash: 'block_004',
    confirmed: '2024-01-15T12:00:00',
    totalInput: 2500000,
    totalOutput: 2490000,
    fees: 10000,
    confirmations: 97,
    doubleSpend: false
});

CREATE (tx5:Transaction {
    hash: 'tx_005',
    chain: 'BTC',
    blockHeight: 750004,
    blockHash: 'block_005',
    confirmed: '2024-01-15T12:30:00',
    totalInput: 1800000,
    totalOutput: 1790000,
    fees: 10000,
    confirmations: 96,
    doubleSpend: false
});

CREATE (tx6:Transaction {
    hash: 'tx_006',
    chain: 'BTC',
    blockHeight: 750005,
    blockHash: 'block_006',
    confirmed: '2024-01-15T13:00:00',
    totalInput: 4000000,
    totalOutput: 3990000,
    fees: 10000,
    confirmations: 95,
    doubleSpend: false
});

// Crear relaciones INPUT (wallet gasta en transacción)
MATCH (w1:Wallet {address: 'wallet_alice'}), (tx1:Transaction {hash: 'tx_001'})
CREATE (w1)-[:INPUT {amount: 2000000, outputIndex: 0}]->(tx1);

MATCH (w2:Wallet {address: 'wallet_bob'}), (tx2:Transaction {hash: 'tx_002'})
CREATE (w2)-[:INPUT {amount: 1500000, outputIndex: 0}]->(tx2);

MATCH (w3:Wallet {address: 'wallet_charlie'}), (tx3:Transaction {hash: 'tx_003'})
CREATE (w3)-[:INPUT {amount: 3000000, outputIndex: 0}]->(tx3);

MATCH (w4:Wallet {address: 'wallet_diana'}), (tx4:Transaction {hash: 'tx_004'})
CREATE (w4)-[:INPUT {amount: 2500000, outputIndex: 0}]->(tx4);

MATCH (w1:Wallet {address: 'wallet_alice'}), (tx5:Transaction {hash: 'tx_005'})
CREATE (w1)-[:INPUT {amount: 1800000, outputIndex: 0}]->(tx5);

MATCH (w5:Wallet {address: 'wallet_eve'}), (tx6:Transaction {hash: 'tx_006'})
CREATE (w5)-[:INPUT {amount: 4000000, outputIndex: 0}]->(tx6);

// Crear relaciones OUTPUT (transacción envía a wallet)
MATCH (tx1:Transaction {hash: 'tx_001'}), (w2:Wallet {address: 'wallet_bob'})
CREATE (tx1)-[:OUTPUT {amount: 1990000, outputIndex: 0}]->(w2);

MATCH (tx2:Transaction {hash: 'tx_002'}), (w3:Wallet {address: 'wallet_charlie'})
CREATE (tx2)-[:OUTPUT {amount: 1490000, outputIndex: 0}]->(w3);

MATCH (tx3:Transaction {hash: 'tx_003'}), (w4:Wallet {address: 'wallet_diana'})
CREATE (tx3)-[:OUTPUT {amount: 2990000, outputIndex: 0}]->(w4);

MATCH (tx4:Transaction {hash: 'tx_004'}), (w5:Wallet {address: 'wallet_eve'})
CREATE (tx4)-[:OUTPUT {amount: 2490000, outputIndex: 0}]->(w5);

MATCH (tx5:Transaction {hash: 'tx_005'}), (w3:Wallet {address: 'wallet_charlie'})
CREATE (tx5)-[:OUTPUT {amount: 1790000, outputIndex: 0}]->(w3);

MATCH (tx6:Transaction {hash: 'tx_006'}), (w1:Wallet {address: 'wallet_alice'})
CREATE (tx6)-[:OUTPUT {amount: 3990000, outputIndex: 0}]->(w1);

// Crear patrón de Peel Chain (wallet_eve -> múltiples transacciones pequeñas)
CREATE (tx_peel1:Transaction {
    hash: 'tx_peel_001',
    chain: 'BTC',
    blockHeight: 750100,
    blockHash: 'block_peel_001',
    confirmed: '2024-01-16T10:00:00',
    totalInput: 2000000,
    totalOutput: 1980000,
    fees: 20000,
    confirmations: 50,
    doubleSpend: false
});

CREATE (tx_peel2:Transaction {
    hash: 'tx_peel_002',
    chain: 'BTC',
    blockHeight: 750101,
    blockHash: 'block_peel_002',
    confirmed: '2024-01-16T10:30:00',
    totalInput: 1900000,
    totalOutput: 1880000,
    fees: 20000,
    confirmations: 49,
    doubleSpend: false
});

CREATE (w_peel1:Wallet {
    address: 'wallet_peel_1',
    chain: 'BTC',
    balance: 80000,
    totalReceived: 100000,
    totalSent: 20000,
    txCount: 2,
    riskLevel: 'MEDIUM',
    tags: []
});

CREATE (w_peel2:Wallet {
    address: 'wallet_peel_2',
    chain: 'BTC',
    balance: 1900000,
    totalReceived: 1900000,
    totalSent: 0,
    txCount: 1,
    riskLevel: 'LOW',
    tags: []
});

// Conectar peel chain
MATCH (w5:Wallet {address: 'wallet_eve'}), (tx_peel1:Transaction {hash: 'tx_peel_001'})
CREATE (w5)-[:INPUT {amount: 2000000, outputIndex: 0}]->(tx_peel1);

MATCH (tx_peel1:Transaction {hash: 'tx_peel_001'}), (w_peel1:Wallet {address: 'wallet_peel_1'})
CREATE (tx_peel1)-[:OUTPUT {amount: 100000, outputIndex: 0}]->(w_peel1);

MATCH (tx_peel1:Transaction {hash: 'tx_peel_001'}), (w_peel2:Wallet {address: 'wallet_peel_2'})
CREATE (tx_peel1)-[:OUTPUT {amount: 1880000, outputIndex: 1}]->(w_peel2);

MATCH (w_peel2:Wallet {address: 'wallet_peel_2'}), (tx_peel2:Transaction {hash: 'tx_peel_002'})
CREATE (w_peel2)-[:INPUT {amount: 1900000, outputIndex: 0}]->(tx_peel2);

