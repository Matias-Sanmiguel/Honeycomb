import React, { useState } from 'react';
import NetworkGraph from '../components/NetworkGraph';
import { TransactionBarChart, StatsCards, NetworkRadarChart } from '../components/ChartVisualizations';
import './Backtracking.css';

const Backtracking = () => {
  const [sourceAddress, setSourceAddress] = useState('');
  const [maxDepth, setMaxDepth] = useState(5);
  const [minAmount, setMinAmount] = useState(0);
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    if (!sourceAddress) {
      alert('Por favor, ingresa una dirección de origen');
      return;
    }

    setLoading(true);
    try {
      // Usar el endpoint correcto de Network Analysis
      const url = `http://localhost:8080/api/forensic/network/${sourceAddress}`;
      console.log('Fetching:', url);
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      console.log('Response data:', data);
      setResults(data);
    } catch (error) {
      console.error('Error:', error);
      alert('Error al analizar la red: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  // Helpers para extraer los items correctamente
  const getItems = (res) => {
    if (!res) return [];
    // Para Network Analysis, los datos están en connectedWallets
    if (res.connectedWallets && Array.isArray(res.connectedWallets)) return res.connectedWallets;
    if (res.topCentralWallets && Array.isArray(res.topCentralWallets)) return res.topCentralWallets;
    if (res.walletNodes && Array.isArray(res.walletNodes)) return res.walletNodes;
    if (res.connections && Array.isArray(res.connections)) return res.connections;
    if (Array.isArray(res.suspiciousChains)) return res.suspiciousChains;
    if (Array.isArray(res.chains)) return res.chains;
    if (Array.isArray(res.cycles)) return res.cycles;
    return [];
  };

  const items = getItems(results);

  return (
    <div className="backtracking-container">
      <h1>🔍 Análisis de Red - Wallets Centrales</h1>
      <p className="description">
        Identifica las wallets más centrales y conectadas en la red de transacciones
      </p>

      <div className="input-section">
        <div className="form-group">
          <label htmlFor="sourceAddress">Dirección de Origen:</label>
          <input
            id="sourceAddress"
            type="text"
            value={sourceAddress}
            onChange={(e) => setSourceAddress(e.target.value)}
            placeholder="1A1zP1eP..."
          />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="maxDepth">Profundidad Máxima:</label>
            <input
              id="maxDepth"
              type="number"
              value={maxDepth}
              onChange={(e) => setMaxDepth(parseInt(e.target.value))}
              min="1"
              max="10"
            />
          </div>

          <div className="form-group">
            <label htmlFor="minAmount">Monto Mínimo (opcional):</label>
            <input
              id="minAmount"
              type="number"
              value={minAmount}
              onChange={(e) => setMinAmount(parseFloat(e.target.value))}
              min="0"
              step="0.01"
            />
          </div>
        </div>

        <button onClick={handleSearch} disabled={loading}>
          {loading ? 'Buscando...' : 'Buscar Cadenas'}
        </button>
      </div>

      {results && (
        <div className="results-section">
          <h2>Análisis de Centralidad de Red</h2>
          <div className="results-info">
            <p><strong>Algoritmo:</strong> {results.algorithm}</p>
            <p><strong>Total de Wallets:</strong> {results.resultCount}</p>
          </div>

          {items.length > 0 ? (
            <>
              {/* Tarjetas de estadísticas */}
              <StatsCards data={items} />

              {/* Visualización del grafo de red */}
              <NetworkGraph data={results} width={window.innerWidth - 100} height={600} />

              {/* Gráficos adicionales */}
              <div className="charts-grid">
                <TransactionBarChart data={items.slice(0, 10)} />
                <NetworkRadarChart data={items} />
              </div>

              {/* Lista de detalles */}
              <div className="details-section">
                <h3>📋 Detalles de Wallets</h3>
                <div className="cycles-list">
                  {items.map((item, index) => (
                    <div key={index} className="cycle-card">
                      <div className="card-header">
                        <h3>Wallet {index + 1}</h3>
                        <span className="badge">{item.transactionCount || item.degree || 0} tx</span>
                      </div>
                      <div className="card-body">
                        <div className="info-row">
                          <span className="label">Dirección:</span>
                          <span className="value">{item.address || item.wallet}</span>
                        </div>
                        {item.transactionCount && (
                          <div className="info-row">
                            <span className="label">Transacciones:</span>
                            <span className="value">{item.transactionCount}</span>
                          </div>
                        )}
                        {item.degree && (
                          <div className="info-row">
                            <span className="label">Grado:</span>
                            <span className="value">{item.degree}</span>
                          </div>
                        )}
                        {item.totalAmount && (
                          <div className="info-row">
                            <span className="label">Monto Total:</span>
                            <span className="value">{(item.totalAmount / 100000000).toFixed(8)} BTC</span>
                          </div>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </>
          ) : (
            <div className="no-results-message">
              <p>No se encontraron resultados para el análisis.</p>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default Backtracking;
