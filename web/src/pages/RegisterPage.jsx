import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { APP_NAME } from '../config/appConfig';
import logo from '../assets/campus-logo.svg';
import './RegisterPage.css';

const AlertCircle = () => (
  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"
    fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"
    className="error-icon">
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
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
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
      <div className="register-showcase">
        <div className="register-pill">New account</div>
        <div className="register-brand-block">
          <img src={logo} alt={`${APP_NAME} logo`} className="register-logo-image" />
          <div>
            <h2 className="register-showcase-title">{APP_NAME}</h2>
            <p className="register-showcase-copy">
              Join the campus event platform with a cleaner onboarding flow and a branded first impression.
            </p>
          </div>
        </div>
      </div>

      <div className="register-card">
        <div className="register-header">
          <div className="register-logo">
            <img src={logo} alt={`${APP_NAME} logo`} className="register-header-logo" />
          </div>
          <h1 className="register-title">Create Account</h1>
          <p className="register-subtitle">Fill in the details to get started</p>
        </div>

        <form onSubmit={handleSubmit} className="register-form">
          {error && (
            <div className="error-message">
              <AlertCircle />
              <span>{error}</span>
            </div>
          )}

          <div className="register-grid">
            <div className="form-group">
              <label htmlFor="fullName" className="form-label">Full Name</label>
              <input
                id="fullName"
                name="fullName"
                type="text"
                className="form-input"
                placeholder="Enter your full name"
                value={formData.fullName}
                onChange={handleChange}
                required
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="email" className="form-label">Email</label>
              <input
                id="email"
                name="email"
                type="email"
                className="form-input"
                placeholder="Enter your email"
                value={formData.email}
                onChange={handleChange}
                required
                disabled={loading}
              />
            </div>
          </div>

          <div className="register-grid">
            <div className="form-group">
              <label htmlFor="password" className="form-label">Password</label>
              <div className="password-field">
                <input
                  id="password"
                  name="password"
                  type={showPassword ? 'text' : 'password'}
                  className="form-input password-input"
                  placeholder="Create a password"
                  value={formData.password}
                  onChange={handleChange}
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

            <div className="form-group">
              <label htmlFor="confirmPassword" className="form-label">Confirm Password</label>
              <div className="password-field">
                <input
                  id="confirmPassword"
                  name="confirmPassword"
                  type={showConfirmPassword ? 'text' : 'password'}
                  className="form-input password-input"
                  placeholder="Confirm your password"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  required
                  disabled={loading}
                />
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowConfirmPassword((value) => !value)}
                  aria-label={showConfirmPassword ? 'Hide password' : 'Show password'}
                >
                  <EyeIcon open={showConfirmPassword} />
                </button>
              </div>
            </div>
          </div>

          {formData.role === 'STUDENT' && (
            <div className="register-grid">
              <div className="form-group">
                <label htmlFor="department" className="form-label">
                  Department <span className="optional">(Optional)</span>
                </label>
                <input
                  id="department"
                  name="department"
                  type="text"
                  className="form-input"
                  placeholder="e.g., Computer Science"
                  value={formData.department}
                  onChange={handleChange}
                  disabled={loading}
                />
              </div>

              <div className="form-group">
                <label htmlFor="year" className="form-label">
                  Year <span className="optional">(Optional)</span>
                </label>
                <select
                  id="year"
                  className="form-input form-select"
                  value={formData.year}
                  onChange={handleYearChange}
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
            </div>
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
