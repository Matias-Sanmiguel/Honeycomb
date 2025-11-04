import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Backtracking from './pages/Backtracking';
import BranchBound from './pages/BranchBound';
import Greedy from './pages/Greedy';
import GraphAlgorithms from './pages/GraphAlgorithms';
import PatternMatching from './pages/PatternMatching';
import WalletAnalysis from './pages/WalletAnalysis';
import './styles/App.css';

function App() {
  return (
    <div className="app">
      <Navbar />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/backtracking" element={<Backtracking />} />
          <Route path="/branch-bound" element={<BranchBound />} />
          <Route path="/greedy" element={<Greedy />} />
          <Route path="/graph" element={<GraphAlgorithms />} />
          <Route path="/patterns" element={<PatternMatching />} />
          <Route path="/wallet" element={<WalletAnalysis />} />
        </Routes>
      </main>
    </div>
  );
}

export default App;
