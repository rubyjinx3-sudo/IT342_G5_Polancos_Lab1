import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { APP_NAME } from '../config/appConfig';
import logo from '../assets/campus-logo.svg';
import './LoginPage.css';

const AlertCircle = ({ size = 16 }) => (
  <svg xmlns="http://www.w3.org/2000/svg" width={size} height={size} viewBox="0 0 24 24"
    fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <circle cx="12" cy="12" r="10"/>
    <line x1="12" x2="12" y1="8" y2="12"/>
    <line x1="12" x2="12.01" y1="16" y2="16"/>
  </svg>
);

const EyeIcon = ({ open }) => (
  <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24"
    fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    {open ? (
      <>
        <path d="M2.06 12.35a1 1 0 0 1 0-.7C3.9 7.18 7.6 4 12 4s8.1 3.18 9.94 7.65a1 1 0 0 1 0 .7C20.1 16.82 16.4 20 12 20S3.9 16.82 2.06 12.35Z" />
        <circle cx="12" cy="12" r="3" />
      </>
    ) : (
      <>
        <path d="m3 3 18 18" />
        <path d="M10.58 10.58a2 2 0 0 0 2.83 2.83" />
        <path d="M9.88 5.09A9.77 9.77 0 0 1 12 4c4.4 0 8.1 3.18 9.94 7.65a1 1 0 0 1 0 .7 10.45 10.45 0 0 1-4.24 5.1" />
        <path d="M6.61 6.61A10.45 10.45 0 0 0 2.06 11.65a1 1 0 0 0 0 .7C3.9 16.82 7.6 20 12 20a9.77 9.77 0 0 0 5.39-1.61" />
      </>
    )}
  </svg>
);

const LoginPage = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const { user } = await login(email, password);
      navigate(user.role?.toUpperCase() === 'ORGANIZER' ? '/organizer' : '/dashboard');
    } catch (err) {
      setError(err.response?.data || err.message || 'Invalid email or password.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="auth-showcase">
        <div className="auth-pill">Built for campus teams</div>
        <div className="auth-brand-block">
          <img src={logo} alt={`${APP_NAME} logo`} className="auth-brand-logo" />
          <div>
            <h2 className="auth-showcase-title">{APP_NAME}</h2>
            <p className="auth-showcase-copy">
              Manage campus happenings, registrations, and student participation in one place.
            </p>
          </div>
        </div>
        <div className="auth-feature-list">
          <div className="auth-feature-item">
            <strong>Discover faster</strong>
            <span>Upcoming activities, schedules, and locations are easier to scan.</span>
          </div>
          <div className="auth-feature-item">
            <strong>Register with less friction</strong>
            <span>Cleaner forms and clearer state feedback reduce drop-off.</span>
          </div>
          <div className="auth-feature-item">
            <strong>Responsive by default</strong>
            <span>The layout adapts from phone screens to wider desktop workspaces.</span>
          </div>
        </div>
      </div>

      <div className="login-card">
        <div className="login-header">
          <img src={logo} alt={`${APP_NAME} logo`} className="login-logo-image" />
          <h1 className="login-title">{APP_NAME}</h1>
          <p className="login-subtitle">Sign in to your account</p>
        </div>

        <form onSubmit={handleSubmit} className="login-form">
          {error && (
            <div className="error-box">
              <AlertCircle size={16} />
              <span>{error}</span>
            </div>
          )}

          <div className="form-group">
            <label htmlFor="email" className="form-label">Email</label>
            <input
              id="email"
              type="email"
              className="form-input"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="password" className="form-label">Password</label>
            <div className="password-field">
              <input
                id="password"
                type={showPassword ? 'text' : 'password'}
                className="form-input password-input"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                disabled={loading}
              />
              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowPassword((value) => !value)}
                aria-label={showPassword ? 'Hide password' : 'Show password'}
              >
                <EyeIcon open={showPassword} />
              </button>
            </div>
          </div>

          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? 'Signing in...' : 'Login'}
          </button>
        </form>

        <p className="login-footer">
          Don&apos;t have an account?{' '}
          <Link to="/register" className="link">Register</Link>
        </p>
      </div>
    </div>
  );
};

export default LoginPage;
