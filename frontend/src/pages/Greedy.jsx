import React, { useState } from 'react';
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
      const response = await fetch(url);
      const data = await response.json();
      setResults(data);
    } catch (error) {
      console.error('Error:', error);
      alert('Error al ejecutar análisis greedy');
    } finally {
      setLoading(false);
    }
  };

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
          </div>
        </div>

        <button onClick={handleSearch} disabled={loading}>
          {loading ? 'Analizando...' : 'Analizar'}
        </button>
      </div>

      {results && (
        <div className="results-section">
          <h2>Resultados</h2>
          <p>
            <strong>Chains encontradas:</strong> {results.chainsFound ?? (results.chains ? results.chains.length : 0)}
          </p>
          {Array.isArray(results.chains) && results.chains.length > 0 ? (
            <div className="path-visualization">
              {results.chains.map((chain, idx) => (
                <div key={idx} className="address-node" style={{ textAlign: 'left' }}>
                  <div><strong>Tx:</strong> {chain.transactionHash || '—'}</div>
                  <div><strong>Longitud:</strong> {chain.chainLength ?? '—'}</div>
                  <div><strong>Total:</strong> {chain.totalAmount ?? '—'}</div>
                </div>
              ))}
            </div>
          ) : (
            <p>No se encontraron cadenas</p>
          )}
        </div>
      )}
    </div>
  );
};

export default Greedy;
