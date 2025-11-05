import React, { useState } from 'react';
import NetworkGraph from '../components/NetworkGraph';
import { StatsCards } from '../components/ChartVisualizations';
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
      const url = `/api/branch-bound/optimal-path?sourceAddress=${encodeURIComponent(sourceAddress)}&targetAddress=${encodeURIComponent(targetAddress)}&maxDepth=${maxDepth}`;
      const response = await fetch(url);
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
          <h2>Ruta Óptima Encontrada</h2>
          {results.path ? (
            <>
              {/* Métricas principales */}
              <div className="metrics-cards">
                <div className="metric-card-bb">
                  <div className="metric-icon">💰</div>
                  <div className="metric-content">
                    <div className="metric-label">Costo Total</div>
                    <div className="metric-value">{results.totalCost || 0}</div>
                  </div>
                </div>
                <div className="metric-card-bb">
                  <div className="metric-icon">📏</div>
                  <div className="metric-content">
                    <div className="metric-label">Longitud del Camino</div>
                    <div className="metric-value">{results.pathLength || results.path.length}</div>
                  </div>
                </div>
                <div className="metric-card-bb">
                  <div className="metric-icon">🎯</div>
                  <div className="metric-content">
                    <div className="metric-label">Nodos Explorados</div>
                    <div className="metric-value">{results.nodesExplored || '—'}</div>
                  </div>
                </div>
              </div>

              {/* Visualización del grafo */}
              <NetworkGraph
                data={results}
                width={window.innerWidth - 100}
                height={500}
              />

              {/* Visualización del camino paso a paso */}
              <div className="path-section">
                <h3>🛤️ Camino Óptimo</h3>
                <div className="path-visualization-bb">
                  {results.path.map((address, idx) => (
                    <React.Fragment key={idx}>
                      <div className="path-step">
                        <div className="step-number">{idx + 1}</div>
                        <div className="step-address">
                          <span className="address-label">
                            {idx === 0 ? '🟢 Origen' : idx === results.path.length - 1 ? '🔴 Destino' : '🔵 Intermedio'}
                          </span>
                          <span className="address-value">{address.substring(0, 12)}...</span>
                        </div>
                      </div>
                      {idx < results.path.length - 1 && (
                        <div className="path-connector">
                          <div className="connector-line"></div>
                          <div className="connector-arrow">→</div>
                        </div>
                      )}
                    </React.Fragment>
                  ))}
                </div>
              </div>

              {/* Información adicional */}
              {results.algorithm && (
                <div className="algorithm-info-panel">
                  <h3>ℹ️ Información del Algoritmo</h3>
                  <div className="info-grid-bb">
                    <div className="info-item-bb">
                      <span className="label">Algoritmo:</span>
                      <span className="value">{results.algorithm}</span>
                    </div>
                    {results.executionTime && (
                      <div className="info-item-bb">
                        <span className="label">Tiempo de Ejecución:</span>
                        <span className="value">{results.executionTime}ms</span>
                      </div>
                    )}
                  </div>
                </div>
              )}
            </>
          ) : (
            <div className="no-path-found">
              <p>❌ No se encontró una ruta entre las direcciones especificadas</p>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default BranchBound;
