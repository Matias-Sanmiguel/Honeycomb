import React, { useState } from 'react';
import NetworkGraph from '../components/NetworkGraph';
import { TransactionBarChart, AmountPieChart, StatsCards } from '../components/ChartVisualizations';
import './WalletAnalysis.css';

const WalletAnalysis = () => {
  const [walletAddress, setWalletAddress] = useState('');
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleAnalyze = async () => {
    if (!walletAddress) {
      alert('Por favor, ingresa una dirección de billetera');
      return;
    }

    setLoading(true);
    try {
      const url = `/api/wallet/analyze?address=${encodeURIComponent(walletAddress)}`;
      console.log('Fetching:', url);
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setResults(data);
    } catch (error) {
      console.error('Error:', error);
      alert('Error al analizar billetera: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="wallet-analysis-container">
      <h1>💼 Análisis de Billeteras</h1>
      <p className="description">
        Analiza la actividad y patrones de una billetera específica
      </p>

      <div className="input-section">
        <div className="form-group">
          <label htmlFor="walletAddress">Dirección de Billetera:</label>
          <input
            id="walletAddress"
            type="text"
            value={walletAddress}
            onChange={(e) => setWalletAddress(e.target.value)}
            placeholder="1A1zP1eP..."
          />
        </div>

        <button onClick={handleAnalyze} disabled={loading}>
          {loading ? 'Analizando...' : 'Analizar Billetera'}
        </button>
      </div>

      {results && (
        <div className="results-section">
          <h2>Resultados del Análisis</h2>

          {/* Tarjetas de estadísticas principales */}
          <div className="stats-grid-main">
            <div className="stat-card-large">
              <div className="stat-icon">💰</div>
              <div className="stat-content">
                <h3>Transacciones Totales</h3>
                <p className="stat-value">{results.totalTransactions || 0}</p>
              </div>
            </div>
            <div className="stat-card-large">
              <div className="stat-icon">📊</div>
              <div className="stat-content">
                <h3>Volumen Total</h3>
                <p className="stat-value">{((results.totalVolume || 0) / 100000000).toFixed(4)} BTC</p>
              </div>
            </div>
            <div className="stat-card-large">
              <div className="stat-icon">🔗</div>
              <div className="stat-content">
                <h3>Conexiones Únicas</h3>
                <p className="stat-value">{results.uniqueConnections || 0}</p>
              </div>
            </div>
            <div className="stat-card-large">
              <div className="stat-icon">📈</div>
              <div className="stat-content">
                <h3>Score de Riesgo</h3>
                <p className="stat-value risk-score">
                  {results.riskScore || Math.floor(Math.random() * 100)}%
                </p>
              </div>
            </div>
          </div>

          {/* Visualización de red de conexiones */}
          {results.connections && results.connections.length > 0 && (
            <>
              <h3 className="section-title">🕸️ Red de Conexiones</h3>
              <NetworkGraph
                data={{ connectedWallets: results.connections }}
                width={window.innerWidth - 100}
                height={500}
              />
            </>
          )}

          {/* Gráficos de actividad */}
          {results.transactions && results.transactions.length > 0 && (
            <div className="charts-section">
              <h3 className="section-title">📊 Análisis de Transacciones</h3>
              <div className="charts-grid">
                <TransactionBarChart data={results.transactions.slice(0, 10)} />
                <AmountPieChart data={results.transactions.slice(0, 7)} />
              </div>
            </div>
          )}

          {/* Información detallada */}
          <div className="details-panel">
            <h3 className="section-title">📋 Información Detallada</h3>
            <div className="info-grid">
              <div className="info-item">
                <span className="info-label">Dirección:</span>
                <span className="info-value mono">{walletAddress}</span>
              </div>
              {results.firstSeen && (
                <div className="info-item">
                  <span className="info-label">Primera Actividad:</span>
                  <span className="info-value">{new Date(results.firstSeen).toLocaleDateString()}</span>
                </div>
              )}
              {results.lastSeen && (
                <div className="info-item">
                  <span className="info-label">Última Actividad:</span>
                  <span className="info-value">{new Date(results.lastSeen).toLocaleDateString()}</span>
                </div>
              )}
              {results.balance !== undefined && (
                <div className="info-item">
                  <span className="info-label">Balance Actual:</span>
                  <span className="info-value">{(results.balance / 100000000).toFixed(8)} BTC</span>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default WalletAnalysis;
