import React, { useState } from 'react';
import './Greedy.css';

const Greedy = () => {
  const [sourceAddress, setSourceAddress] = useState('');
  const [targetAmount, setTargetAmount] = useState(0);
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    if (!sourceAddress) {
      alert('Por favor, ingresa una dirección de origen');
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8080/api/greedy/max-value-path?sourceAddress=${sourceAddress}&targetAmount=${targetAmount}`);
      const data = await response.json();
      setResults(data);
    } catch (error) {
      console.error('Error:', error);
      alert('Error al buscar ruta');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="greedy-container">
      <h1>⚡ Greedy - Ruta de Máximo Valor</h1>
      <p className="description">
        Encuentra rutas de transacciones usando un algoritmo voraz
      </p>

      <div className="input-section">
        <div className="form-group">
          <label htmlFor="sourceAddress">Dirección de Origen:</label>
          <input
            id="sourceAddress"
            type="text"
            value={sourceAddress}
            onChange={(e) => setSourceAddress(e.target.value)}
            placeholder="0x..."
          />
        </div>

        <div className="form-group">
          <label htmlFor="targetAmount">Monto Objetivo:</label>
          <input
            id="targetAmount"
            type="number"
            value={targetAmount}
            onChange={(e) => setTargetAmount(parseFloat(e.target.value))}
            min="0"
            step="0.01"
          />
        </div>

        <button onClick={handleSearch} disabled={loading}>
          {loading ? 'Buscando...' : 'Buscar Ruta'}
        </button>
      </div>

      {results && (
        <div className="results-section">
          <h2>Resultados</h2>
          {results.path ? (
            <div className="path-info">
              <p><strong>Valor Total:</strong> {results.totalValue}</p>
              <p><strong>Transacciones:</strong> {results.transactionCount}</p>
              <div className="path-visualization">
                {results.path.map((address, idx) => (
                  <span key={idx} className="address-node">{address}</span>
                ))}
              </div>
            </div>
          ) : (
            <p>No se encontró una ruta</p>
          )}
        </div>
      )}
    </div>
  );
};

export default Greedy;

