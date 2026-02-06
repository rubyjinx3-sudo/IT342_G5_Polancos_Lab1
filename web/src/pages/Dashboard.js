import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Dashboard.css';

const Dashboard = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handleLogout = async () => {
    try {
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Dashboard</h1>
        <button onClick={handleLogout} className="btn-logout">
          Logout
        </button>
      </div>

      <div className="profile-card">
        <div className="profile-header">
          <div className="profile-avatar">
            {user?.fullName?.charAt(0).toUpperCase()}
          </div>
          <h2>Welcome, {user?.fullName}!</h2>
        </div>

        <div className="profile-info">
          <div className="info-item">
            <label>Full Name:</label>
            <span>{user?.fullName}</span>
          </div>

          <div className="info-item">
            <label>Username:</label>
            <span>{user?.username}</span>
          </div>

          <div className="info-item">
            <label>Email:</label>
            <span>{user?.email}</span>
          </div>

          <div className="info-item">
            <label>User ID:</label>
            <span>{user?.id}</span>
          </div>
        </div>

        <div className="profile-actions">
          <button className="btn-secondary">Edit Profile</button>
          <button className="btn-secondary">Change Password</button>
        </div>
      </div>

      <div className="dashboard-stats">
        <div className="stat-card">
          <h3>Account Status</h3>
          <p className="status-active">Active</p>
        </div>

        <div className="stat-card">
          <h3>Member Since</h3>
          <p>2024</p>
        </div>

        <div className="stat-card">
          <h3>Account Type</h3>
          <p>Standard User</p>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
