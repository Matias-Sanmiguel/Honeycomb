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
          url = `http://localhost:8080/api/greedy/peel-chains?threshold=0.95&minChainLength=3&limit=20`;
          break;
        case 'large-transactions':
          // Usar el mismo endpoint que funciona pero con diferente interpretación
          url = `http://localhost:8080/api/greedy/peel-chains?threshold=0.90&minChainLength=1&limit=50`;
          break;
        case 'suspicious-patterns':
          url = `http://localhost:8080/api/greedy/peel-chains?threshold=0.95&minChainLength=3&limit=20`;
          break;
        case 'statistics':
          // Usar el mismo endpoint con filtros diferentes
          url = `http://localhost:8080/api/greedy/peel-chains?threshold=0.80&minChainLength=2&limit=30`;
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
      console.log('Response data:', data);

      // Extraer los datos correctos según el tipo de patrón
      if (data.chains) {
        // Filtrar y adaptar los datos según el tipo
        let processedData = data.chains;

        if (patternType === 'large-transactions') {
          // Filtrar solo transacciones con montos grandes
          processedData = data.chains
            .filter(chain => chain.totalAmount && chain.totalAmount > 1000000)
            .sort((a, b) => b.totalAmount - a.totalAmount);
        } else if (patternType === 'statistics') {
          // Agrupar por wallet para mostrar estadísticas
          const walletStats = {};
          data.chains.forEach(chain => {
            if (chain.transactionHash) {
              const wallet = chain.transactionHash.substring(0, 10);
              if (!walletStats[wallet]) {
                walletStats[wallet] = {
                  wallet: wallet,
                  transactionCount: 0,
                  totalAmount: 0,
                  avgAmount: 0
                };
              }
              walletStats[wallet].transactionCount++;
              walletStats[wallet].totalAmount += chain.totalAmount || 0;
            }
          });
          // Calcular promedios
          Object.values(walletStats).forEach(stat => {
            stat.avgAmount = stat.totalAmount / stat.transactionCount;
          });
          processedData = Object.values(walletStats)
            .sort((a, b) => b.transactionCount - a.transactionCount)
            .slice(0, 20);
        }

        setResults(processedData);
      } else {
        setResults(data);
      }
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
