import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './RegisterPage.css';

// ── Inline SVG icons ──────────────────────
const CalendarDays = () => (
  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
    fill="none" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <rect width="18" height="18" x="3" y="4" rx="2" ry="2"/>
    <line x1="16" x2="16" y1="2" y2="6"/><line x1="8" x2="8" y1="2" y2="6"/>
    <line x1="3" x2="21" y1="10" y2="10"/>
    <path d="M8 14h.01M12 14h.01M16 14h.01M8 18h.01M12 18h.01M16 18h.01"/>
  </svg>
);
const AlertCircle = () => (
  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"
    fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"
    className="error-icon">
    <circle cx="12" cy="12" r="10"/>
    <line x1="12" x2="12" y1="8" y2="12"/>
    <line x1="12" x2="12.01" y1="16" y2="16"/>
  </svg>
);
// ─────────────────────────────────────────

export const RegisterPage = () => {
  const navigate = useNavigate();
  const { register } = useAuth();

  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'STUDENT',
    department: '',
    year: '',
  });
  const [error,   setError]   = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleRoleChange = (e) => {
    setFormData({ ...formData, role: e.target.value });
  };

  const handleYearChange = (e) => {
    setFormData({ ...formData, year: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }
    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }

    setLoading(true);
    try {
      await register(formData);
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data || err.message || 'Failed to register. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-container">
      <div className="register-card">

        {/* Header */}
        <div className="register-header">
          <div className="register-logo">
            <div className="logo-icon">
              <CalendarDays />
            </div>
          </div>
          <h1 className="register-title">Create Account</h1>
          <p className="register-subtitle">Fill in the details to get started</p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="register-form">
          {error && (
            <div className="error-message">
              <AlertCircle />
              <span>{error}</span>
            </div>
          )}

          <div className="form-group">
            <label htmlFor="fullName" className="form-label">Full Name</label>
            <input
              id="fullName" name="fullName" type="text"
              className="form-input" placeholder="Enter your full name"
              value={formData.fullName} onChange={handleChange}
              required disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="email" className="form-label">Email</label>
            <input
              id="email" name="email" type="email"
              className="form-input" placeholder="Enter your email"
              value={formData.email} onChange={handleChange}
              required disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="password" className="form-label">Password</label>
            <input
              id="password" name="password" type="password"
              className="form-input" placeholder="Create a password"
              value={formData.password} onChange={handleChange}
              required disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword" className="form-label">Confirm Password</label>
            <input
              id="confirmPassword" name="confirmPassword" type="password"
              className="form-input" placeholder="Confirm your password"
              value={formData.confirmPassword} onChange={handleChange}
              required disabled={loading}
            />
          </div>

        

          {formData.role === 'STUDENT' && (
            <>
              <div className="form-group">
                <label htmlFor="department" className="form-label">
                  Department <span className="optional">(Optional)</span>
                </label>
                <input
                  id="department" name="department" type="text"
                  className="form-input" placeholder="e.g., Computer Science"
                  value={formData.department} onChange={handleChange}
                  disabled={loading}
                />
              </div>

              <div className="form-group">
                <label htmlFor="year" className="form-label">
                  Year <span className="optional">(Optional)</span>
                </label>
                <select
                  id="year" className="form-input form-select"
                  value={formData.year} onChange={handleYearChange}
                  disabled={loading}
                >
                  <option value="">Select year</option>
                  <option value="freshman">Freshman</option>
                  <option value="sophomore">Sophomore</option>
                  <option value="junior">Junior</option>
                  <option value="senior">Senior</option>
                  <option value="graduate">Graduate</option>
                </select>
              </div>
            </>
          )}

          <button type="submit" className="register-button" disabled={loading}>
            {loading ? 'Creating Account...' : 'Create Account'}
          </button>
        </form>

        <div className="register-footer">
          Already have an account?{' '}
          <Link to="/" className="login-link">Login</Link>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
