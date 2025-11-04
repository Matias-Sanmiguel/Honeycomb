import React, { useState } from 'react';
import './BranchBound.css';

const BranchBound = () => {
  const [sourceAddress, setSourceAddress] = useState('');
  const [targetAddress, setTargetAddress] = useState('');
  const [maxDepth, setMaxDepth] = useState(5);
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    if (!sourceAddress || !targetAddress) {
      alert('Por favor, ingresa las direcciones de origen y destino');
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8080/api/branch-bound/optimal-path?sourceAddress=${sourceAddress}&targetAddress=${targetAddress}&maxDepth=${maxDepth}`);
      const data = await response.json();
      setResults(data);
    } catch (error) {
      console.error('Error:', error);
      alert('Error al buscar ruta óptima');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="branch-bound-container">
      <h1>🌳 Branch & Bound - Ruta Óptima</h1>
      <p className="description">
        Encuentra la ruta óptima entre dos direcciones usando Branch & Bound
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
          <label htmlFor="targetAddress">Dirección de Destino:</label>
          <input
            id="targetAddress"
            type="text"
            value={targetAddress}
            onChange={(e) => setTargetAddress(e.target.value)}
            placeholder="0x..."
          />
        </div>

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

        <button onClick={handleSearch} disabled={loading}>
          {loading ? 'Buscando...' : 'Buscar Ruta Óptima'}
        </button>
      </div>

      {results && (
        <div className="results-section">
          <h2>Resultados</h2>
          {results.path ? (
            <div className="path-info">
              <p><strong>Costo Total:</strong> {results.totalCost}</p>
              <p><strong>Longitud:</strong> {results.pathLength}</p>
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

export default BranchBound;

