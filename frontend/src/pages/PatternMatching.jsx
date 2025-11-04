import React, { useState } from 'react';
import './PatternMatching.css';

const PatternMatching = () => {
  const [pattern, setPattern] = useState('');
  const [text, setText] = useState('');
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    if (!pattern || !text) {
      alert('Por favor, ingresa tanto el patrón como el texto');
      return;
    }

    setLoading(true);
    try {
      // Aquí iría la llamada a la API cuando esté implementada
      // Por ahora, solo mostramos un mensaje
      setResults({
        message: 'Funcionalidad de búsqueda de patrones en desarrollo'
      });
    } catch (error) {
      console.error('Error:', error);
      alert('Error al buscar patrones');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="pattern-matching-container">
      <h1>Búsqueda de Patrones</h1>
      <div className="pattern-matching-content">
        <div className="input-section">
          <div className="form-group">
            <label htmlFor="pattern">Patrón a buscar:</label>
            <input
              id="pattern"
              type="text"
              value={pattern}
              onChange={(e) => setPattern(e.target.value)}
              placeholder="Ingresa el patrón"
            />
          </div>
          <div className="form-group">
            <label htmlFor="text">Texto donde buscar:</label>
            <textarea
              id="text"
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="Ingresa el texto"
              rows={10}
            />
          </div>
          <button onClick={handleSearch} disabled={loading}>
            {loading ? 'Buscando...' : 'Buscar Patrón'}
          </button>
        </div>

        {results && (
          <div className="results-section">
            <h2>Resultados</h2>
            <p>{results.message}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default PatternMatching;

