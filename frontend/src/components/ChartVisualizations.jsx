import React from 'react';
import {
  BarChart,
  Bar,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  RadarChart,
  PolarGrid,
  PolarAngleAxis,
  PolarRadiusAxis,
  Radar
} from 'recharts';
import './ChartVisualizations.css';

const COLORS = ['#4ecdc4', '#ff6b6b', '#45b7d1', '#f7b731', '#5f27cd', '#00d2d3', '#ff9ff3'];

export const TransactionBarChart = ({ data }) => {
  const chartData = data.map((item, idx) => ({
    name: (item.wallet || item.address || item.transactionHash || `Item ${idx + 1}`).substring(0, 8),
    transacciones: item.transactionCount || item.chainLength || item.degree || 0,
    monto: (item.totalAmount || item.amount || 0) / 100000000 // Convertir a BTC
  }));

  return (
    <div className="chart-container">
      <h3>📊 Transacciones por Wallet</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" stroke="#444" />
          <XAxis dataKey="name" stroke="#fff" />
          <YAxis stroke="#fff" />
          <Tooltip
            contentStyle={{
              backgroundColor: 'rgba(0,0,0,0.8)',
              border: 'none',
              borderRadius: '8px',
              color: '#fff'
            }}
          />
          <Legend />
          <Bar dataKey="transacciones" fill="#4ecdc4" name="Transacciones" />
          <Bar dataKey="monto" fill="#ff6b6b" name="Monto (BTC)" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export const AmountPieChart = ({ data }) => {
  const chartData = data
    .slice(0, 7)
    .map((item, idx) => ({
      name: (item.wallet || item.address || `Item ${idx + 1}`).substring(0, 8),
      value: item.totalAmount || item.amount || 1
    }));

  return (
    <div className="chart-container">
      <h3>🥧 Distribución de Montos</h3>
      <ResponsiveContainer width="100%" height={300}>
        <PieChart>
          <Pie
            data={chartData}
            cx="50%"
            cy="50%"
            labelLine={false}
            label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
            outerRadius={100}
            fill="#8884d8"
            dataKey="value"
          >
            {chartData.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip
            contentStyle={{
              backgroundColor: 'rgba(0,0,0,0.8)',
              border: 'none',
              borderRadius: '8px',
              color: '#fff'
            }}
          />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
};

export const ActivityLineChart = ({ data }) => {
  const chartData = data.map((item, idx) => ({
    name: `#${idx + 1}`,
    actividad: item.transactionCount || item.degree || 0,
    monto: (item.totalAmount || 0) / 100000000
  }));

  return (
    <div className="chart-container">
      <h3>📈 Tendencia de Actividad</h3>
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" stroke="#444" />
          <XAxis dataKey="name" stroke="#fff" />
          <YAxis stroke="#fff" />
          <Tooltip
            contentStyle={{
              backgroundColor: 'rgba(0,0,0,0.8)',
              border: 'none',
              borderRadius: '8px',
              color: '#fff'
            }}
          />
          <Legend />
          <Line type="monotone" dataKey="actividad" stroke="#4ecdc4" strokeWidth={2} name="Actividad" />
          <Line type="monotone" dataKey="monto" stroke="#ff6b6b" strokeWidth={2} name="Monto (BTC)" />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export const NetworkRadarChart = ({ data }) => {
  const topItems = data.slice(0, 6);
  const chartData = topItems.map((item, idx) => ({
    wallet: (item.wallet || item.address || `W${idx + 1}`).substring(0, 6),
    transacciones: item.transactionCount || item.degree || 0,
    monto: Math.log10((item.totalAmount || 1) + 1), // Escala logarítmica
    conexiones: item.degree || item.connectionCount || 0
  }));

  return (
    <div className="chart-container">
      <h3>🎯 Análisis Multidimensional</h3>
      <ResponsiveContainer width="100%" height={300}>
        <RadarChart cx="50%" cy="50%" outerRadius="80%" data={chartData}>
          <PolarGrid stroke="#444" />
          <PolarAngleAxis dataKey="wallet" stroke="#fff" />
          <PolarRadiusAxis stroke="#fff" />
          <Radar name="Transacciones" dataKey="transacciones" stroke="#4ecdc4" fill="#4ecdc4" fillOpacity={0.6} />
          <Radar name="Conexiones" dataKey="conexiones" stroke="#ff6b6b" fill="#ff6b6b" fillOpacity={0.6} />
          <Tooltip
            contentStyle={{
              backgroundColor: 'rgba(0,0,0,0.8)',
              border: 'none',
              borderRadius: '8px',
              color: '#fff'
            }}
          />
          <Legend />
        </RadarChart>
      </ResponsiveContainer>
    </div>
  );
};

export const StatsCards = ({ data }) => {
  const totalTransactions = data.reduce((sum, item) => sum + (item.transactionCount || 0), 0);
  const totalAmount = data.reduce((sum, item) => sum + (item.totalAmount || 0), 0);
  const avgAmount = data.length > 0 ? totalAmount / data.length : 0;
  const maxDegree = Math.max(...data.map(item => item.degree || 0), 0);

  return (
    <div className="stats-cards-container">
      <div className="stat-card-mini">
        <div className="stat-icon">💰</div>
        <div className="stat-content">
          <div className="stat-label">Monto Total</div>
          <div className="stat-value">{(totalAmount / 100000000).toFixed(4)} BTC</div>
        </div>
      </div>
      <div className="stat-card-mini">
        <div className="stat-icon">📊</div>
        <div className="stat-content">
          <div className="stat-label">Transacciones</div>
          <div className="stat-value">{totalTransactions.toLocaleString()}</div>
        </div>
      </div>
      <div className="stat-card-mini">
        <div className="stat-icon">📈</div>
        <div className="stat-content">
          <div className="stat-label">Promedio</div>
          <div className="stat-value">{(avgAmount / 100000000).toFixed(4)} BTC</div>
        </div>
      </div>
      <div className="stat-card-mini">
        <div className="stat-icon">🔗</div>
        <div className="stat-content">
          <div className="stat-label">Max Grado</div>
          <div className="stat-value">{maxDegree}</div>
        </div>
      </div>
    </div>
  );
};

