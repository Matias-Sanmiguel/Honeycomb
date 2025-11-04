// Script para cargar datos de prueba en Neo4j
// Ejecutar desde Neo4j Browser o cypher-shell

// Limpiar datos existentes (opcional - comentar si no quieres borrar)
MATCH (n) DETACH DELETE n;

// ==================== CREAR WALLETS ====================

CREATE (w1:Wallet {
    address: '1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa',
    balance: 50.0,
    totalReceived: 100.0,
    totalSent: 50.0,
    txCount: 5,
    firstSeen: datetime('2023-01-01T10:00:00'),
    lastSeen: datetime('2023-12-31T15:30:00'),
    risk_score: 0.2
})

CREATE (w2:Wallet {
    address: '1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2',
    balance: 25.5,
    totalReceived: 75.5,
    totalSent: 50.0,
    txCount: 8,
    firstSeen: datetime('2023-02-15T12:00:00'),
    lastSeen: datetime('2023-12-30T14:20:00'),
    risk_score: 0.5
})

CREATE (w3:Wallet {
    address: '3J98t1WpEZ73CNmYviecrnyiWrnqRhWNLy',
    balance: 100.0,
    totalReceived: 200.0,
    totalSent: 100.0,
    txCount: 15,
    firstSeen: datetime('2023-01-10T09:00:00'),
    lastSeen: datetime('2023-12-29T16:45:00'),
    risk_score: 0.8
})

CREATE (w4:Wallet {
    address: 'bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh',
    balance: 75.25,
    totalReceived: 150.0,
    totalSent: 74.75,
    txCount: 12,
    firstSeen: datetime('2023-03-01T11:30:00'),
    lastSeen: datetime('2023-12-28T13:15:00'),
    risk_score: 0.3
})

CREATE (w5:Wallet {
    address: '1FeexV6bAHb8ybZjqQMjJrcCrHGW9sb6uF',
    balance: 10.0,
    totalReceived: 30.0,
    totalSent: 20.0,
    txCount: 6,
    firstSeen: datetime('2023-04-20T14:00:00'),
    lastSeen: datetime('2023-12-27T10:30:00'),
    risk_score: 0.6
})

CREATE (w6:Wallet {
    address: '3FZbgi29cpjq2GjdwV8eyHuJJnkLtktZc5',
    balance: 5.5,
    totalReceived: 15.5,
    totalSent: 10.0,
    txCount: 4,
    firstSeen: datetime('2023-05-10T16:20:00'),
    lastSeen: datetime('2023-12-26T12:00:00'),
    risk_score: 0.4
})

CREATE (w7:Wallet {
    address: 'bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4',
    balance: 200.0,
    totalReceived: 500.0,
    totalSent: 300.0,
    txCount: 25,
    firstSeen: datetime('2023-01-05T08:00:00'),
    lastSeen: datetime('2023-12-31T18:00:00'),
    risk_score: 0.9
})

CREATE (w8:Wallet {
    address: '1NDyJtNTjmwk5xPNhjgAMu4HDHigtobu1s',
    balance: 15.75,
    totalReceived: 40.0,
    totalSent: 24.25,
    txCount: 9,
    firstSeen: datetime('2023-06-15T10:45:00'),
    lastSeen: datetime('2023-12-25T15:30:00'),
    risk_score: 0.35
});

// ==================== CREAR TRANSACCIONES ====================

CREATE (t1:Transaction {
    hash: 'a1075db55d416d3ca199f55b6084e2115b9345e16c5cf302fc80e9d5fbf5d48d',
    confirmed: datetime('2023-03-15T14:30:00'),
    fee: 0.0001,
    size: 250,
    blockHeight: 780000,
    confirmations: 5000
})

CREATE (t2:Transaction {
    hash: 'b2186eb66e427d4da209f75c7195f2116c8456f27d6dg413gd91faeff6g6e59e',
    confirmed: datetime('2023-04-20T16:45:00'),
    fee: 0.00015,
    size: 300,
    blockHeight: 781500,
    confirmations: 4500
})

CREATE (t3:Transaction {
    hash: 'c3297fc77f538e5eb320g86d8206g3227d9567g38e7eh524he02gbfgg7h7f60f',
    confirmed: datetime('2023-05-25T11:20:00'),
    fee: 0.0002,
    size: 350,
    blockHeight: 783000,
    confirmations: 4000
})

CREATE (t4:Transaction {
    hash: 'd4308gd88g649f6fc431h97e9317h4338e0678h49f8fi635if13hcghh8i8g71g',
    confirmed: datetime('2023-06-30T09:15:00'),
    fee: 0.00012,
    size: 280,
    blockHeight: 784500,
    confirmations: 3500
})

CREATE (t5:Transaction {
    hash: 'e5419he99h750g7gd542i08f0428i5449f1789i50g9gj746jg24idhii9j9h82h',
    confirmed: datetime('2023-07-10T13:40:00'),
    fee: 0.00018,
    size: 320,
    blockHeight: 785000,
    confirmations: 3200
})

CREATE (t6:Transaction {
    hash: 'f6520if00i861h8he653j19g1539j6550g2890j61h0hk857kh35jeijj0k0i93i',
    confirmed: datetime('2023-08-15T10:25:00'),
    fee: 0.00025,
    size: 400,
    blockHeight: 786500,
    confirmations: 2800
})

CREATE (t7:Transaction {
    hash: 'g7631jg11j972i9if764k20h2640k7661h3901k72i1il968li46kfjkk1l1j04j',
    confirmed: datetime('2023-09-20T15:50:00'),
    fee: 0.0003,
    size: 450,
    blockHeight: 788000,
    confirmations: 2400
})

CREATE (t8:Transaction {
    hash: 'h8742kh22k083j0jg875l31i3751l8772i4012l83j2jm079mj57lgkkl2m2k15k',
    confirmed: datetime('2023-10-25T12:10:00'),
    fee: 0.00022,
    size: 380,
    blockHeight: 789500,
    confirmations: 2000
})

CREATE (t9:Transaction {
    hash: 'i9853li33l194k1kh986m42j4862m9883j5123m94k3kn180nk68mhllm3n3l26l',
    confirmed: datetime('2023-11-30T14:35:00'),
    fee: 0.00016,
    size: 310,
    blockHeight: 791000,
    confirmations: 1500
})

CREATE (t10:Transaction {
    hash: 'j0964mj44m205l2li097n53k5973n0994k6234n05l4lo291ol79nimnm4o4m37m',
    confirmed: datetime('2023-12-15T16:20:00'),
    fee: 0.0002,
    size: 340,
    blockHeight: 792500,
    confirmations: 1000
});

// ==================== CREAR RELACIONES INPUT ====================

// Wallet 1 -> Transaction 1 (input de 20 BTC)
CREATE (w1)-[:INPUT {
    amount: 20.0,
    txHash: 'a1075db55d416d3ca199f55b6084e2115b9345e16c5cf302fc80e9d5fbf5d48d',
    timestamp: datetime('2023-03-15T14:30:00'),
    index: 0
}]->(t1)

// Wallet 2 -> Transaction 2 (input de 15 BTC)
CREATE (w2)-[:INPUT {
    amount: 15.0,
    txHash: 'b2186eb66e427d4da209f75c7195f2116c8456f27d6dg413gd91faeff6g6e59e',
    timestamp: datetime('2023-04-20T16:45:00'),
    index: 0
}]->(t2)

// Wallet 3 -> Transaction 3 (input de 50 BTC)
CREATE (w3)-[:INPUT {
    amount: 50.0,
    txHash: 'c3297fc77f538e5eb320g86d8206g3227d9567g38e7eh524he02gbfgg7h7f60f',
    timestamp: datetime('2023-05-25T11:20:00'),
    index: 0
}]->(t3)

// Wallet 4 -> Transaction 4 (input de 30 BTC)
CREATE (w4)-[:INPUT {
    amount: 30.0,
    txHash: 'd4308gd88g649f6fc431h97e9317h4338e0678h49f8fi635if13hcghh8i8g71g',
    timestamp: datetime('2023-06-30T09:15:00'),
    index: 0
}]->(t4)

// Wallet 5 -> Transaction 5 (input de 10 BTC)
CREATE (w5)-[:INPUT {
    amount: 10.0,
    txHash: 'e5419he99h750g7gd542i08f0428i5449f1789i50g9gj746jg24idhii9j9h82h',
    timestamp: datetime('2023-07-10T13:40:00'),
    index: 0
}]->(t5)

// Wallet 7 -> Transaction 6 (input de 100 BTC)
CREATE (w7)-[:INPUT {
    amount: 100.0,
    txHash: 'f6520if00i861h8he653j19g1539j6550g2890j61h0hk857kh35jeijj0k0i93i',
    timestamp: datetime('2023-08-15T10:25:00'),
    index: 0
}]->(t6)

// Wallet 3 -> Transaction 7 (input de 75 BTC - peel chain)
CREATE (w3)-[:INPUT {
    amount: 75.0,
    txHash: 'g7631jg11j972i9if764k20h2640k7661h3901k72i1il968li46kfjkk1l1j04j',
    timestamp: datetime('2023-09-20T15:50:00'),
    index: 0
}]->(t7)

// Wallet 4 -> Transaction 8 (input de 40 BTC)
CREATE (w4)-[:INPUT {
    amount: 40.0,
    txHash: 'h8742kh22k083j0jg875l31i3751l8772i4012l83j2jm079mj57lgkkl2m2k15k',
    timestamp: datetime('2023-10-25T12:10:00'),
    index: 0
}]->(t8)

// Wallet 1 -> Transaction 9 (input de 25 BTC)
CREATE (w1)-[:INPUT {
    amount: 25.0,
    txHash: 'i9853li33l194k1kh986m42j4862m9883j5123m94k3kn180nk68mhllm3n3l26l',
    timestamp: datetime('2023-11-30T14:35:00'),
    index: 0
}]->(t9)

// Wallet 8 -> Transaction 10 (input de 20 BTC)
CREATE (w8)-[:INPUT {
    amount: 20.0,
    txHash: 'j0964mj44m205l2li097n53k5973n0994k6234n05l4lo291ol79nimnm4o4m37m',
    timestamp: datetime('2023-12-15T16:20:00'),
    index: 0
}]->(t10);

// ==================== CREAR RELACIONES OUTPUT ====================

// Transaction 1 -> Wallet 2 (output de 10 BTC)
CREATE (t1)-[:OUTPUT {
    amount: 10.0,
    txHash: 'a1075db55d416d3ca199f55b6084e2115b9345e16c5cf302fc80e9d5fbf5d48d',
    timestamp: datetime('2023-03-15T14:30:00'),
    index: 0
}]->(w2)

// Transaction 1 -> Wallet 3 (output de 9.5 BTC)
CREATE (t1)-[:OUTPUT {
    amount: 9.5,
    txHash: 'a1075db55d416d3ca199f55b6084e2115b9345e16c5cf302fc80e9d5fbf5d48d',
    timestamp: datetime('2023-03-15T14:30:00'),
    index: 1
}]->(w3)

// Transaction 2 -> Wallet 4 (output de 8 BTC)
CREATE (t2)-[:OUTPUT {
    amount: 8.0,
    txHash: 'b2186eb66e427d4da209f75c7195f2116c8456f27d6dg413gd91faeff6g6e59e',
    timestamp: datetime('2023-04-20T16:45:00'),
    index: 0
}]->(w4)

// Transaction 2 -> Wallet 5 (output de 6.5 BTC)
CREATE (t2)-[:OUTPUT {
    amount: 6.5,
    txHash: 'b2186eb66e427d4da209f75c7195f2116c8456f27d6dg413gd91faeff6g6e59e',
    timestamp: datetime('2023-04-20T16:45:00'),
    index: 1
}]->(w5)

// Transaction 3 -> Wallet 6 (output de 25 BTC)
CREATE (t3)-[:OUTPUT {
    amount: 25.0,
    txHash: 'c3297fc77f538e5eb320g86d8206g3227d9567g38e7eh524he02gbfgg7h7f60f',
    timestamp: datetime('2023-05-25T11:20:00'),
    index: 0
}]->(w6)

// Transaction 3 -> Wallet 7 (output de 24.5 BTC)
CREATE (t3)-[:OUTPUT {
    amount: 24.5,
    txHash: 'c3297fc77f538e5eb320g86d8206g3227d9567g38e7eh524he02gbfgg7h7f60f',
    timestamp: datetime('2023-05-25T11:20:00'),
    index: 1
}]->(w7)

// Transaction 4 -> Wallet 1 (output de 15 BTC - ciclo)
CREATE (t4)-[:OUTPUT {
    amount: 15.0,
    txHash: 'd4308gd88g649f6fc431h97e9317h4338e0678h49f8fi635if13hcghh8i8g71g',
    timestamp: datetime('2023-06-30T09:15:00'),
    index: 0
}]->(w1)

// Transaction 4 -> Wallet 8 (output de 14.5 BTC)
CREATE (t4)-[:OUTPUT {
    amount: 14.5,
    txHash: 'd4308gd88g649f6fc431h97e9317h4338e0678h49f8fi635if13hcghh8i8g71g',
    timestamp: datetime('2023-06-30T09:15:00'),
    index: 1
}]->(w8)

// Transaction 5 -> Wallet 3 (output de 5 BTC)
CREATE (t5)-[:OUTPUT {
    amount: 5.0,
    txHash: 'e5419he99h750g7gd542i08f0428i5449f1789i50g9gj746jg24idhii9j9h82h',
    timestamp: datetime('2023-07-10T13:40:00'),
    index: 0
}]->(w3)

// Transaction 5 -> Wallet 6 (output de 4.8 BTC)
CREATE (t5)-[:OUTPUT {
    amount: 4.8,
    txHash: 'e5419he99h750g7gd542i08f0428i5449f1789i50g9gj746jg24idhii9j9h82h',
    timestamp: datetime('2023-07-10T13:40:00'),
    index: 1
}]->(w6)

// Transaction 6 -> Wallet 2 (output de 50 BTC - mixing)
CREATE (t6)-[:OUTPUT {
    amount: 50.0,
    txHash: 'f6520if00i861h8he653j19g1539j6550g2890j61h0hk857kh35jeijj0k0i93i',
    timestamp: datetime('2023-08-15T10:25:00'),
    index: 0
}]->(w2)

// Transaction 6 -> Wallet 5 (output de 49 BTC)
CREATE (t6)-[:OUTPUT {
    amount: 49.0,
    txHash: 'f6520if00i861h8he653j19g1539j6550g2890j61h0hk857kh35jeijj0k0i93i',
    timestamp: datetime('2023-08-15T10:25:00'),
    index: 1
}]->(w5)

// Transaction 7 -> Wallet 8 (output de 37 BTC - peel chain)
CREATE (t7)-[:OUTPUT {
    amount: 37.0,
    txHash: 'g7631jg11j972i9if764k20h2640k7661h3901k72i1il968li46kfjkk1l1j04j',
    timestamp: datetime('2023-09-20T15:50:00'),
    index: 0
}]->(w8)

// Transaction 7 -> Wallet 4 (output de 37.5 BTC)
CREATE (t7)-[:OUTPUT {
    amount: 37.5,
    txHash: 'g7631jg11j972i9if764k20h2640k7661h3901k72i1il968li46kfjkk1l1j04j',
    timestamp: datetime('2023-09-20T15:50:00'),
    index: 1
}]->(w4)

// Transaction 8 -> Wallet 7 (output de 20 BTC)
CREATE (t8)-[:OUTPUT {
    amount: 20.0,
    txHash: 'h8742kh22k083j0jg875l31i3751l8772i4012l83j2jm079mj57lgkkl2m2k15k',
    timestamp: datetime('2023-10-25T12:10:00'),
    index: 0
}]->(w7)

// Transaction 8 -> Wallet 6 (output de 19.5 BTC)
CREATE (t8)-[:OUTPUT {
    amount: 19.5,
    txHash: 'h8742kh22k083j0jg875l31i3751l8772i4012l83j2jm079mj57lgkkl2m2k15k',
    timestamp: datetime('2023-10-25T12:10:00'),
    index: 1
}]->(w6)

// Transaction 9 -> Wallet 5 (output de 12 BTC)
CREATE (t9)-[:OUTPUT {
    amount: 12.0,
    txHash: 'i9853li33l194k1kh986m42j4862m9883j5123m94k3kn180nk68mhllm3n3l26l',
    timestamp: datetime('2023-11-30T14:35:00'),
    index: 0
}]->(w5)

// Transaction 9 -> Wallet 2 (output de 12.5 BTC)
CREATE (t9)-[:OUTPUT {
    amount: 12.5,
    txHash: 'i9853li33l194k1kh986m42j4862m9883j5123m94k3kn180nk68mhllm3n3l26l',
    timestamp: datetime('2023-11-30T14:35:00'),
    index: 1
}]->(w2)

// Transaction 10 -> Wallet 3 (output de 10 BTC)
CREATE (t10)-[:OUTPUT {
    amount: 10.0,
    txHash: 'j0964mj44m205l2li097n53k5973n0994k6234n05l4lo291ol79nimnm4o4m37m',
    timestamp: datetime('2023-12-15T16:20:00'),
    index: 0
}]->(w3)

// Transaction 10 -> Wallet 7 (output de 9.5 BTC)
CREATE (t10)-[:OUTPUT {
    amount: 9.5,
    txHash: 'j0964mj44m205l2li097n53k5973n0994k6234n05l4lo291ol79nimnm4o4m37m',
    timestamp: datetime('2023-12-15T16:20:00'),
    index: 1
}]->(w7);

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

