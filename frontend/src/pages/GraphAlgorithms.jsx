import React, { useState } from 'react';
import NetworkGraph from '../components/NetworkGraph';
import { ActivityLineChart, StatsCards } from '../components/ChartVisualizations';
import './GraphAlgorithms.css';

const GraphAlgorithms = () => {
  const [algorithm, setAlgorithm] = useState('dijkstra');
  const [sourceAddress, setSourceAddress] = useState('');
  const [targetAddress, setTargetAddress] = useState('');
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    if (!sourceAddress) {
      alert('Por favor, ingresa una dirección de origen');
      return;
    }

    setLoading(true);
    try {
      let url = `/api/graph/${algorithm}?sourceAddress=${encodeURIComponent(sourceAddress)}`;
      if (targetAddress && (algorithm === 'dijkstra' || algorithm === 'bellman-ford')) {
        url += `&targetAddress=${encodeURIComponent(targetAddress)}`;
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
      alert('Error al ejecutar algoritmo: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="graph-algorithms-container">
      <h1>📊 Algoritmos de Grafos</h1>
      <p className="description">
        Análisis de redes de transacciones usando algoritmos clásicos de grafos
      </p>

      <div className="input-section">
        <div className="form-group">
          <label htmlFor="algorithm">Algoritmo:</label>
          <select
            id="algorithm"
            value={algorithm}
            onChange={(e) => setAlgorithm(e.target.value)}
          >
            <option value="dijkstra">Dijkstra (Camino más corto)</option>
            <option value="bellman-ford">Bellman-Ford</option>
            <option value="floyd-warshall">Floyd-Warshall</option>
            <option value="prim">Prim (MST)</option>
            <option value="kruskal">Kruskal (MST)</option>
          </select>
        </div>

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

        {(algorithm === 'dijkstra' || algorithm === 'bellman-ford') && (
          <div className="form-group">
            <label htmlFor="targetAddress">Dirección de Destino (opcional):</label>
            <input
              id="targetAddress"
              type="text"
              value={targetAddress}
              onChange={(e) => setTargetAddress(e.target.value)}
              placeholder="1BvBMSE..."
            />
          </div>
        )}

        <button onClick={handleSearch} disabled={loading}>
          {loading ? 'Ejecutando...' : 'Ejecutar Algoritmo'}
        </button>
      </div>

      {results && (
        <div className="results-section">
          <h2>Resultados del Algoritmo: {algorithm.toUpperCase()}</h2>

          {/* Información general */}
          {results.distance !== undefined && (
            <div className="algorithm-info">
              <div className="info-card">
                <span className="info-label">Distancia:</span>
                <span className="info-value">{results.distance}</span>
              </div>
              {results.cost !== undefined && (
                <div className="info-card">
                  <span className="info-label">Costo:</span>
                  <span className="info-value">{results.cost.toFixed(2)}</span>
                </div>
              )}
              {results.pathLength && (
                <div className="info-card">
                  <span className="info-label">Longitud del Camino:</span>
                  <span className="info-value">{results.pathLength}</span>
                </div>
              )}
            </div>
          )}

          {/* Visualización del grafo */}
          <NetworkGraph
            data={results}
            width={window.innerWidth - 100}
            height={600}
          />

          {/* Camino encontrado */}
          {results.path && results.path.length > 0 && (
            <div className="path-section">
              <h3>🛤️ Camino Encontrado</h3>
              <div className="path-visualization">
                {results.path.map((node, idx) => (
                  <React.Fragment key={idx}>
                    <div className="path-node">
                      <div className="node-number">{idx + 1}</div>
                      <div className="node-address">
                        {(node.address || node).substring(0, 10)}...
                      </div>
                    </div>
                    {idx < results.path.length - 1 && (
                      <div className="path-arrow">→</div>
                    )}
                  </React.Fragment>
                ))}
              </div>
            </div>
          )}

          {/* Datos sin procesar en formato mejorado */}
          <div className="raw-data-section">
            <details>
              <summary>Ver datos completos (JSON)</summary>
              <pre className="json-display">{JSON.stringify(results, null, 2)}</pre>
            </details>
          </div>
        </div>
      )}
    </div>
  );
};

export default GraphAlgorithms;
