import React, { useState } from 'react';
import NetworkGraph from '../components/NetworkGraph';
import { TransactionBarChart, AmountPieChart, ActivityLineChart, StatsCards } from '../components/ChartVisualizations';
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
            <h2>Resultados del Análisis</h2>
            {Array.isArray(results) && results.length > 0 ? (
              <>
                {/* Tarjetas de estadísticas */}
                <StatsCards data={results} />

                {/* Visualización de grafo si hay datos relacionales */}
                {patternType === 'peel-chains' && (
                  <NetworkGraph
                    data={{ chains: results }}
                    width={window.innerWidth - 100}
                    height={500}
                  />
                )}

                {/* Gráficos de análisis */}
                <div className="charts-grid">
                  <TransactionBarChart data={results.slice(0, 15)} />
                  <AmountPieChart data={results.slice(0, 7)} />
                  <ActivityLineChart data={results.slice(0, 20)} />
                </div>

                {/* Tabla de detalles */}
                <div className="patterns-table-container">
                  <h3>📊 Detalles de Patrones Detectados</h3>
                  <div className="patterns-list">
                    {results.map((item, idx) => (
                      <div key={idx} className="pattern-card">
                        <div className="card-header">
                          <h3>
                            {patternType === 'peel-chains' ? '🔗 Peel Chain' :
                             patternType === 'large-transactions' ? '💰 Transacción Grande' :
                             patternType === 'suspicious-patterns' ? '⚠️ Patrón Sospechoso' :
                             '📈 Estadística'} #{idx + 1}
                          </h3>
                          {item.totalAmount && (
                            <span className="badge">
                              {(item.totalAmount / 100000000).toFixed(4)} BTC
                            </span>
                          )}
                        </div>
                        <div className="card-body">
                          {item.wallet && (
                            <div className="info-row">
                              <span className="label">Wallet:</span>
                              <span className="value">{item.wallet}</span>
                            </div>
                          )}
                          {item.transactionHash && (
                            <div className="info-row">
                              <span className="label">Hash:</span>
                              <span className="value">{item.transactionHash}</span>
                            </div>
                          )}
                          {item.transactionCount && (
                            <div className="info-row">
                              <span className="label">Transacciones:</span>
                              <span className="value">{item.transactionCount}</span>
                            </div>
                          )}
                          {item.chainLength && (
                            <div className="info-row">
                              <span className="label">Longitud Cadena:</span>
                              <span className="value">{item.chainLength}</span>
                            </div>
                          )}
                          {item.avgAmount && (
                            <div className="info-row">
                              <span className="label">Monto Promedio:</span>
                              <span className="value">{(item.avgAmount / 100000000).toFixed(8)} BTC</span>
                            </div>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </>
            ) : (
              <div className="no-results-message">
                <p>No se encontraron patrones</p>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default PatternMatching;
