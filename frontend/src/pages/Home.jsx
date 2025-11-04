import React from 'react';
import { Link } from 'react-router-dom';
import './Home.css';

const Home = () => {
  return (
    <div className="home-container">
      <div className="hero-section">
        <h1>🐝 Honeycomb</h1>
        <p className="subtitle">Sistema de Análisis Forense de Criptomonedas</p>
        <p className="description">
          Herramienta avanzada para el análisis y detección de patrones sospechosos en transacciones de criptomonedas
        </p>
      </div>

      <div className="features-grid">
        <Link to="/backtracking" className="feature-card">
          <h3>🔍 Backtracking</h3>
          <p>Búsqueda exhaustiva de patrones y ciclos en transacciones</p>
        </Link>

        <Link to="/branch-bound" className="feature-card">
          <h3>🌳 Branch & Bound</h3>
          <p>Optimización en la búsqueda de rutas sospechosas</p>
        </Link>

        <Link to="/greedy" className="feature-card">
          <h3>⚡ Greedy</h3>
          <p>Algoritmos voraces para análisis rápido de transacciones</p>
        </Link>

        <Link to="/graph" className="feature-card">
          <h3>📊 Algoritmos de Grafos</h3>
          <p>Análisis de redes de transacciones y caminos críticos</p>
        </Link>

        <Link to="/patterns" className="feature-card">
          <h3>🔎 Búsqueda de Patrones</h3>
          <p>Detección de patrones específicos en cadenas de transacciones</p>
        </Link>

        <Link to="/wallet" className="feature-card">
          <h3>💼 Análisis de Billeteras</h3>
          <p>Análisis detallado de actividad de billeteras específicas</p>
        </Link>
      </div>
    </div>
  );
};

export default Home;

