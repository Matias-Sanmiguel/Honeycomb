import React, { useState } from 'react';
import './PatternMatching.css';

const PatternMatching = () => {
  const [patternType, setPatternType] = useState('peel-chains');
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    setLoading(true);
    try {
      let url = '';
      
      switch (patternType) {
        case 'peel-chains':
          url = `/api/forensic/peel-chains/detailed?threshold=0.95&limit=20`;
          break;
        case 'large-transactions':
          url = `/api/network/large-transactions?minAmount=1000000&limit=50`;
          break;
        case 'suspicious-patterns':
          url = `/api/forensic/suspicious-patterns`;
          break;
        case 'statistics':
          url = `/api/network/large-transactions?minAmount=100000&limit=100`;
          break;
        default:
          return;
      }

      console.log('Fetching:', url);
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setResults(data);
    } catch (error) {
      console.error('Error:', error);
      alert('Error al ejecutar análisis de patrones: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="pattern-matching-container">
      <h1>🔍 Detección de Patrones</h1>
      <p className="description">
        Detecta diversos patrones y anomalías en la red de transacciones
      </p>
      <div className="pattern-matching-content">
        <div className="input-section">
          <div className="form-group">
            <label htmlFor="patternType">Tipo de Patrón:</label>
            <select
              id="patternType"
              value={patternType}
              onChange={(e) => setPatternType(e.target.value)}
            >
              <option value="peel-chains">Peel Chains</option>
              <option value="large-transactions">Transacciones Grandes</option>
              <option value="suspicious-patterns">Patrones Sospechosos</option>
              <option value="statistics">Estadísticas de Red</option>
            </select>
          </div>

          <button onClick={handleSearch} disabled={loading}>
            {loading ? 'Analizando...' : 'Buscar Patrones'}
          </button>
        </div>

        {results && (
          <div className="results-section">
            <h2>Resultados</h2>
            {Array.isArray(results) ? (
              <div className="patterns-list">
                {results.length > 0 ? (
                  results.map((item, idx) => (
                    <div key={idx} className="pattern-card">
                      <h3>Patrón {idx + 1}</h3>
                      <pre>{JSON.stringify(item, null, 2)}</pre>
                    </div>
                  ))
                ) : (
                  <p>No se encontraron patrones</p>
                )}
              </div>
            ) : (
              <pre>{JSON.stringify(results, null, 2)}</pre>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default PatternMatching;

