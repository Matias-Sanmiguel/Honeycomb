import React, { useState } from 'react';
import NetworkGraph from '../components/NetworkGraph';
import { TransactionBarChart, AmountPieChart, ActivityLineChart, StatsCards } from '../components/ChartVisualizations';
import './Greedy.css';

const Greedy = () => {
  const [threshold, setThreshold] = useState(0.7);
  const [minChainLength, setMinChainLength] = useState(2);
  const [limit, setLimit] = useState(10);
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    setLoading(true);
    try {
      const url = `/api/greedy/peel-chains?threshold=${threshold}&minChainLength=${minChainLength}&limit=${limit}`;
      console.log('Fetching:', url);
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setResults(data);
    } catch (error) {
      console.error('Error:', error);
      alert('Error al ejecutar análisis greedy: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const chains = results?.chains || [];

  return (
    <div className="greedy-container">
      <h1>⚡ Greedy - Peel Chains</h1>
      <p className="description">
        Analiza posibles peel chains usando un enfoque voraz (greedy)
      </p>

      <div className="input-section">
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="threshold">Umbral (0-1):</label>
            <input
              id="threshold"
              type="number"
              value={threshold}
              onChange={(e) => setThreshold(parseFloat(e.target.value))}
              min="0"
              max="1"
              step="0.01"
            />
            <small className="help-text">Proporción mínima de fondos transferidos</small>
          </div>

          <div className="form-group">
            <label htmlFor="minChainLength">Longitud mínima:</label>
            <input
              id="minChainLength"
              type="number"
              value={minChainLength}
              onChange={(e) => setMinChainLength(parseInt(e.target.value))}
              min="1"
              max="10"
            />
            <small className="help-text">Número mínimo de transacciones en cadena</small>
          </div>

          <div className="form-group">
            <label htmlFor="limit">Límite:</label>
            <input
              id="limit"
              type="number"
              value={limit}
              onChange={(e) => setLimit(parseInt(e.target.value))}
              min="1"
              max="50"
            />
            <small className="help-text">Máximo de cadenas a retornar</small>
          </div>
        </div>

        <button onClick={handleSearch} disabled={loading}>
          {loading ? 'Analizando...' : 'Analizar Peel Chains'}
        </button>
      </div>

      {results && (
        <div className="results-section">
          <h2>Resultados del Análisis Greedy</h2>

          {/* Resumen de resultados */}
          <div className="summary-card">
            <div className="summary-item">
              <div className="summary-icon">🔗</div>
              <div className="summary-content">
                <div className="summary-label">Chains Encontradas</div>
                <div className="summary-value">{results.chainsFound ?? chains.length}</div>
              </div>
            </div>
            <div className="summary-item">
              <div className="summary-icon">📊</div>
              <div className="summary-content">
                <div className="summary-label">Algoritmo</div>
                <div className="summary-value">{results.algorithm || 'Greedy'}</div>
              </div>
            </div>
            <div className="summary-item">
              <div className="summary-icon">⚙️</div>
              <div className="summary-content">
                <div className="summary-label">Umbral Aplicado</div>
                <div className="summary-value">{(threshold * 100).toFixed(0)}%</div>
              </div>
            </div>
          </div>

          {chains.length > 0 ? (
            <>
              {/* Tarjetas de estadísticas */}
              <StatsCards data={chains} />

              {/* Visualización del grafo */}
              <NetworkGraph
                data={{ chains: chains }}
                width={window.innerWidth - 100}
                height={500}
              />

              {/* Gráficos de análisis */}
              <div className="charts-grid">
                <TransactionBarChart data={chains.slice(0, 15)} />
                <AmountPieChart data={chains.slice(0, 7)} />
                <ActivityLineChart data={chains.slice(0, 20)} />
              </div>

              {/* Lista detallada de cadenas */}
              <div className="chains-detail-section">
                <h3>📋 Detalle de Peel Chains</h3>
                <div className="chains-grid">
                  {chains.map((chain, idx) => (
                    <div key={idx} className="chain-card-greedy">
                      <div className="chain-header-greedy">
                        <div className="chain-number">#{idx + 1}</div>
                        <div className="chain-badge">
                          {chain.chainLength || 'N/A'} saltos
                        </div>
                      </div>
                      <div className="chain-body-greedy">
                        <div className="chain-info-row">
                          <span className="label">🔑 Hash:</span>
                          <span className="value mono">{(chain.transactionHash || '—').substring(0, 16)}...</span>
                        </div>
                        <div className="chain-info-row">
                          <span className="label">💰 Monto Total:</span>
                          <span className="value highlight">
                            {chain.totalAmount ? (chain.totalAmount / 100000000).toFixed(8) : '—'} BTC
                          </span>
                        </div>
                        {chain.sourceWallet && (
                          <div className="chain-info-row">
                            <span className="label">📤 Origen:</span>
                            <span className="value mono">{chain.sourceWallet.substring(0, 16)}...</span>
                          </div>
                        )}
                        {chain.mainRecipient && (
                          <div className="chain-info-row">
                            <span className="label">📥 Destino:</span>
                            <span className="value mono">{chain.mainRecipient.substring(0, 16)}...</span>
                          </div>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </>
          ) : (
            <div className="no-results-greedy">
              <div className="no-results-icon">🔍</div>
              <p>No se encontraron peel chains con los criterios especificados</p>
              <small>Intenta ajustar el umbral o la longitud mínima de cadena</small>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default Greedy;
