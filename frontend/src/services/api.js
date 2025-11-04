import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para manejo de errores
api.interceptors.response.use(
  response => response,
  error => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);

// Backtracking
export const backtrackingAPI = {
  detectSuspiciousChains: (startWallet, maxDepth = 5) =>
    api.post('/forensic/backtracking/suspicious-chains', { startWallet, maxDepth }),

  detectCycles: (startWallet, maxDepth = 5) =>
    api.post('/forensic/backtracking/detect-cycles', { startWallet, maxDepth }),
};

// Branch & Bound
export const branchBoundAPI = {
  findOptimalPath: (sourceWallet, targetWallet, maxCost) =>
    api.post('/forensic/branch-bound/optimal-path', { sourceWallet, targetWallet, maxCost }),

  findBestPaths: (sourceWallet, targetWallet, maxCost, topN = 5) =>
    api.post('/forensic/branch-bound/best-paths', { sourceWallet, targetWallet, maxCost, topN }),
};

// Greedy Algorithms
export const greedyAPI = {
  detectPeelChains: (threshold = 0.7, limit = 10) =>
    api.get('/algorithms/greedy/peel-chains', { params: { threshold, limit } }),

  clusterPeelChains: (threshold = 0.7, minChainLength = 3, limit = 10) =>
    api.get('/algorithms/greedy/peel-chain-clusters', {
      params: { threshold, minChainLength, limit }
    }),
};

// Dynamic Programming
export const dynamicProgrammingAPI = {
  findMaxFlowPath: (sourceWallet, targetWallet, maxHops = 10) =>
    api.post('/algorithms/dynamic-programming/max-flow-path', {
      sourceWallet, targetWallet, maxHops
    }),
};

// Graph Algorithms
export const graphAPI = {
  calculateBetweennessCentrality: (topN = 10) =>
    api.get('/network/betweenness-centrality', { params: { topN } }),

  detectCommunities: (minClusterSize = 3) =>
    api.get('/network/communities', { params: { minClusterSize } }),

  calculateNodeImportance: (topN = 10) =>
    api.get('/network/node-importance', { params: { topN } }),
};

// Pattern Matching
export const patternAPI = {
  detectMixingPatterns: (depth = 3) =>
    api.get('/network/mixing-patterns', { params: { depth } }),

  detectCycles: (maxDepth = 5) =>
    api.get('/network/cycles', { params: { maxDepth } }),

  detectRapidTransactions: (timeWindowSeconds = 3600) =>
    api.get('/network/rapid-transactions', { params: { timeWindowSeconds } }),

  detectOutliers: () =>
    api.get('/network/outliers'),
};

// Wallet Analysis
export const walletAPI = {
  getWalletInfo: (address) =>
    api.get(`/wallets/${address}`),

  getWalletTransactions: (address, limit = 20) =>
    api.get(`/wallets/${address}/transactions`, { params: { limit } }),

  analyzeNetwork: (address) =>
    api.get(`/wallets/${address}/network`),
};

// Path Analysis
export const pathAPI = {
  findShortestPath: (address1, address2) =>
    api.get('/path-analysis/shortest-path', { params: { address1, address2 } }),

  findAllShortPaths: (address1, address2, maxLength = 5) =>
    api.get('/path-analysis/all-short-paths', { params: { address1, address2, maxLength } }),
};

export default api;
