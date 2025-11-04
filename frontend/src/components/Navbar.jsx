import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  const location = useLocation();

  const isActive = (path) => location.pathname === path;

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/">🐝 Honeycomb</Link>
      </div>
      <ul className="navbar-menu">
        <li>
          <Link to="/" className={isActive('/') ? 'active' : ''}>
            Inicio
          </Link>
        </li>
        <li>
          <Link to="/backtracking" className={isActive('/backtracking') ? 'active' : ''}>
            Backtracking
          </Link>
        </li>
        <li>
          <Link to="/branch-bound" className={isActive('/branch-bound') ? 'active' : ''}>
            Branch & Bound
          </Link>
        </li>
        <li>
          <Link to="/greedy" className={isActive('/greedy') ? 'active' : ''}>
            Greedy
          </Link>
        </li>
        <li>
          <Link to="/graph" className={isActive('/graph') ? 'active' : ''}>
            Grafos
          </Link>
        </li>
        <li>
          <Link to="/patterns" className={isActive('/patterns') ? 'active' : ''}>
            Patrones
          </Link>
        </li>
        <li>
          <Link to="/wallet" className={isActive('/wallet') ? 'active' : ''}>
            Billeteras
          </Link>
        </li>
      </ul>
    </nav>
  );
};

export default Navbar;

