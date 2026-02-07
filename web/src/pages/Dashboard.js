import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import authService from '../services/authService';
import './Auth.css';

function Dashboard() {
  const [user, setUser] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    // Get user from localStorage instead of API call
    const userData = authService.getCurrentUser();
    if (!userData) {
      navigate('/login');
    } else {
      setUser(userData);
    }
  }, [navigate]);

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  if (!user) {
    return <div>Loading...</div>;
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Dashboard</h2>
        <div className="user-info">
          <p><strong>Welcome, {user.username}!</strong></p>
          <p>Helloooo~~.You are successfully logged in.</p>
        </div>
        <button onClick={handleLogout} className="btn-primary">
          Logout
        </button>
      </div>
    </div>
  );
}

export default Dashboard;