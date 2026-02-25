import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  // ✅ FIX: role is stored lowercase, compare lowercase
  const isOrganizer = user?.role?.toLowerCase() === 'organizer';

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <span className="brand-icon">✦</span>
        <span className="brand-name">Campus Events</span>
      </div>
      <div className="navbar-links">
        {isOrganizer ? (
          <Link to="/organizer" className="nav-link">My Events</Link>
        ) : (
          <Link to="/dashboard" className="nav-link">Dashboard</Link>
        )}
        <Link to="/profile" className="nav-link">Profile</Link>
        <button className="nav-link nav-logout" onClick={handleLogout}>Logout</button>
      </div>
    </nav>
  );
}
