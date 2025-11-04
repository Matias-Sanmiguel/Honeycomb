import React, { useState } from 'react';
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
      const response = await fetch(`http://localhost:8080/api/backtracking/cycles?sourceAddress=${sourceAddress}&maxDepth=${maxDepth}&minAmount=${minAmount}`);
      const data = await response.json();
      setResults(data);
    } catch (error) {
      console.error('Error:', error);
      alert('Error al buscar ciclos');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="backtracking-container">
      <h1>🔍 Backtracking - Búsqueda de Ciclos</h1>
      <p className="description">
        Búsqueda exhaustiva de ciclos sospechosos en transacciones de criptomonedas
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
            <label htmlFor="minAmount">Monto Mínimo:</label>
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
          {loading ? 'Buscando...' : 'Buscar Ciclos'}
        </button>
      </div>

      {results && (
        <div className="results-section">
          <h2>Resultados</h2>
          {results.cycles && results.cycles.length > 0 ? (
            <div className="cycles-list">
              {results.cycles.map((cycle, index) => (
                <div key={index} className="cycle-card">
                  <h3>Ciclo {index + 1}</h3>
                  <p><strong>Longitud:</strong> {cycle.length}</p>
                  <p><strong>Monto Total:</strong> {cycle.totalAmount}</p>
                  <div className="cycle-path">
                    {cycle.path && cycle.path.map((address, idx) => (
                      <span key={idx} className="address-node">{address}</span>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p>No se encontraron ciclos</p>
          )}
        </div>
      )}
    </div>
  );
};

export default Backtracking;

