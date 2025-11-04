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
      const url = `/api/backtracking/suspicious-chains?sourceAddress=${encodeURIComponent(
        sourceAddress
      )}&maxDepth=${maxDepth}`;
      const response = await fetch(url);
      const data = await response.json();
      setResults(data);
    } catch (error) {
      console.error('Error:', error);
      alert('Error al buscar cadenas sospechosas');
    } finally {
      setLoading(false);
    }
  };

  // Helpers para soportar tanto 'chains' como 'cycles' si existieran
  const getItems = (res) => {
    if (!res) return [];
    if (Array.isArray(res.chains)) return res.chains;
    if (Array.isArray(res.cycles)) return res.cycles;
    return [];
  };

  const items = getItems(results);

  return (
    <div className="backtracking-container">
      <h1>🔍 Backtracking - Búsqueda de Cadenas Sospechosas</h1>
      <p className="description">
        Exploración en profundidad para detectar cadenas/ciclos sospechosos de transacciones
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
          <h2>Resultados</h2>
          {items.length > 0 ? (
            <div className="cycles-list">
              {items.map((item, index) => (
                <div key={index} className="cycle-card">
                  <h3>Cadena {index + 1}</h3>
                  {/* Render genérico por si el shape varía */}
                  <pre>{JSON.stringify(item, null, 2)}</pre>
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

export default Backtracking;
