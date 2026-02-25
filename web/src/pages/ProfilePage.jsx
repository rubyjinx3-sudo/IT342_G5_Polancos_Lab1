import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import authService from '../services/authService';
import eventService from '../services/eventService';
import { User, Mail, Shield, Calendar, CheckCircle, AlertCircle } from 'lucide-react';
import './ProfilePage.css';

function formatDate(d) {
  if (!d) return 'N/A';
  return new Date(d).toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' });
}

const ProfilePage = () => {
  const { user, updateUser } = useAuth();
  const [profile, setProfile] = useState(null);
  const [regCount, setRegCount] = useState(0);
  const [pageLoading, setPageLoading] = useState(true);

  // Form state
  const [fullName, setFullName] = useState('');
  const [currentPw, setCurrentPw] = useState('');
  const [newPw, setNewPw] = useState('');
  const [confirmPw, setConfirmPw] = useState('');
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState(null); // { type, text }

  useEffect(() => {
    if (user) loadProfile();
  }, [user]);

  const loadProfile = async () => {
    try {
      setPageLoading(true);
      const [profileData, registered] = await Promise.all([
        authService.getFullProfile(user.email),
        eventService.getMyRegisteredEvents(),
      ]);
      setProfile(profileData);
      setFullName(profileData.fullName || user.name || '');
      setRegCount(registered.length);
    } catch {
      setFullName(user.name || '');
    } finally {
      setPageLoading(false);
    }
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setMsg(null);

    if (newPw && newPw !== confirmPw) {
      setMsg({ type: 'error', text: 'New passwords do not match.' });
      return;
    }
    if (newPw && newPw.length < 6) {
      setMsg({ type: 'error', text: 'New password must be at least 6 characters.' });
      return;
    }

    setSaving(true);
    try {
      await updateUser({
        fullName,
        name: fullName,
        currentPassword: currentPw || undefined,
        newPassword: newPw || undefined,
      });
      setCurrentPw(''); setNewPw(''); setConfirmPw('');
      setMsg({ type: 'success', text: 'Profile updated successfully!' });
    } catch (err) {
      setMsg({ type: 'error', text: err.response?.data || err.message || 'Update failed.' });
    } finally {
      setSaving(false);
    }
  };

  if (pageLoading) return (
    <div className="page-loading">
      <div className="spinner" />
      <p>Loading profile...</p>
    </div>
  );

  const displayRole = (profile?.role || user?.role || 'student');
  const displayName = fullName || user?.name || '';

  return (
    <div className="profile-page">
      <div className="profile-page-header">
        <h2 className="profile-page-title">Profile</h2>
        <p className="profile-page-subtitle">Manage your account information</p>
      </div>

      <div className="profile-layout">
        {/* Left: Info */}
        <div className="profile-left">
          <div className="profile-info-card">
            <div className="avatar">
              <span className="avatar-initials">
                {displayName.charAt(0).toUpperCase()}
              </span>
            </div>
            <h3 className="profile-name">{displayName}</h3>
            <p className="profile-email">{user?.email}</p>
            <span className="role-badge">
              {displayRole.charAt(0).toUpperCase() + displayRole.slice(1).toLowerCase()}
            </span>
          </div>

          <div className="profile-stats-card">
            <h4 className="stats-heading">Account Stats</h4>
            <div className="stats-list">
              <div className="stat-row">
                <Calendar size={16} className="stat-icon" />
                <div>
                  <p className="stat-val">{regCount}</p>
                  <p className="stat-lbl">Events Registered</p>
                </div>
              </div>
              <div className="stat-row">
                <Shield size={16} className="stat-icon" />
                <div>
                  <p className="stat-val">{displayRole.charAt(0).toUpperCase() + displayRole.slice(1).toLowerCase()}</p>
                  <p className="stat-lbl">Account Type</p>
                </div>
              </div>
              <div className="stat-row">
                <User size={16} className="stat-icon" />
                <div>
                  <p className="stat-val">{formatDate(profile?.createdAt)}</p>
                  <p className="stat-lbl">Member Since</p>
                </div>
              </div>
              {profile?.lastLogin && (
                <div className="stat-row">
                  <Mail size={16} className="stat-icon" />
                  <div>
                    <p className="stat-val">{formatDate(profile.lastLogin)}</p>
                    <p className="stat-lbl">Last Login</p>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Right: Edit Form */}
        <div className="profile-form-card">
          <h3 className="form-card-title">Edit Profile</h3>

          <form onSubmit={handleSave} className="profile-form">
            {msg && (
              <div className={`profile-msg ${msg.type === 'success' ? 'msg-success' : 'msg-error'}`}>
                {msg.type === 'success'
                  ? <CheckCircle size={15} />
                  : <AlertCircle size={15} />}
                <span>{msg.text}</span>
              </div>
            )}

            <div className="form-section">
              <h4 className="form-section-title">Personal Information</h4>
              <div className="form-group">
                <label className="form-label">Full Name</label>
                <input className="form-input" value={fullName}
                  onChange={(e) => setFullName(e.target.value)} required />
              </div>
              <div className="form-group">
                <label className="form-label">Email</label>
                <input className="form-input form-input-disabled" value={user?.email || ''} disabled />
                <p className="field-hint">Email cannot be changed.</p>
              </div>
            </div>

            <div className="form-section">
              <h4 className="form-section-title">Change Password</h4>
              <p className="section-hint">Leave blank to keep your current password.</p>
              <div className="form-group">
                <label className="form-label">Current Password</label>
                <input type="password" className="form-input" placeholder="Enter current password"
                  value={currentPw} onChange={(e) => setCurrentPw(e.target.value)} />
              </div>
              <div className="form-group">
                <label className="form-label">New Password</label>
                <input type="password" className="form-input" placeholder="Enter new password"
                  value={newPw} onChange={(e) => setNewPw(e.target.value)} />
              </div>
              <div className="form-group">
                <label className="form-label">Confirm New Password</label>
                <input type="password" className="form-input" placeholder="Confirm new password"
                  value={confirmPw} onChange={(e) => setConfirmPw(e.target.value)} />
              </div>
            </div>

            <button type="submit" className="save-btn" disabled={saving}>
              {saving ? 'Saving...' : 'Save Changes'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
