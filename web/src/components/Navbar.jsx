import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const isOrganizer = user?.role?.toLowerCase() === 'organizer';
  const avatarSrc = user?.avatarUrl || user?.avatarDataUrl || '';

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <div className="navbar-brand">
          <span className="brand-icon">CE</span>
          <div className="brand-copy">
            <Link to={isOrganizer ? '/organizer' : '/dashboard'} className="brand-name">Campus Events</Link>
            <span className="brand-subtitle">{isOrganizer ? 'Organizer workspace' : 'Student workspace'}</span>
          </div>
        </div>
        <div className="navbar-links">
          <Link to="/profile" className="nav-avatar-link" aria-label="Open profile">
            {avatarSrc ? (
              <img src={avatarSrc} alt="Profile" className="nav-avatar-image" />
            ) : (
              <span className="nav-avatar-fallback">
                {(user?.fullName || user?.name || user?.email || 'U').charAt(0).toUpperCase()}
              </span>
            )}
          </Link>
          {isOrganizer ? (
            <NavLink to="/organizer" className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>My Events</NavLink>
          ) : (
            <NavLink to="/dashboard" className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>Dashboard</NavLink>
          )}
          <NavLink to="/profile" className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}>Profile</NavLink>
          <button className="nav-link nav-logout" onClick={handleLogout}>Logout</button>
        </div>
      </div>
    </nav>
  );
}
