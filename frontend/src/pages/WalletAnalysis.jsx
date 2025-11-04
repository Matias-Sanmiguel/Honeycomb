import React, { useState } from 'react';
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
      const response = await fetch(url);
      const data = await response.json();
      setResults(data);
    } catch (error) {
      console.error('Error:', error);
      alert('Error al analizar billetera');
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
          <div className="stats-grid">
            <div className="stat-card">
              <h3>Transacciones Totales</h3>
              <p className="stat-value">{results.totalTransactions || 0}</p>
            </div>
            <div className="stat-card">
              <h3>Volumen Total</h3>
              <p className="stat-value">{results.totalVolume || 0}</p>
            </div>
            <div className="stat-card">
              <h3>Conexiones Únicas</h3>
              <p className="stat-value">{results.uniqueConnections || 0}</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default WalletAnalysis;
