import { useEffect, useRef, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import authService from '../services/authService';
import eventService from '../services/eventService';
import { uploadProfilePhoto } from '../services/supabaseStorage';
import { User, Mail, Shield, Calendar, CheckCircle, AlertCircle, ImagePlus } from 'lucide-react';
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
  const [fullName, setFullName] = useState('');
  const [avatarUrl, setAvatarUrl] = useState('');
  const [avatarPreview, setAvatarPreview] = useState('');
  const [selectedAvatarFile, setSelectedAvatarFile] = useState(null);
  const [currentPw, setCurrentPw] = useState('');
  const [newPw, setNewPw] = useState('');
  const [confirmPw, setConfirmPw] = useState('');
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState(null);
  const fileInputRef = useRef(null);

  useEffect(() => {
    if (!user) return;

    const run = async () => {
      try {
        setPageLoading(true);
        const [profileData, registered] = await Promise.all([
          authService.getFullProfile(user.email),
          eventService.getMyRegisteredEvents(),
        ]);
        setProfile(profileData);
        setFullName(profileData.fullName || user.name || '');
        const savedAvatar = user.avatarUrl || user.avatarDataUrl || '';
        setAvatarUrl(savedAvatar);
        setAvatarPreview(savedAvatar);
        setRegCount(registered.length);
      } catch {
        setFullName(user.name || '');
        const savedAvatar = user.avatarUrl || user.avatarDataUrl || '';
        setAvatarUrl(savedAvatar);
        setAvatarPreview(savedAvatar);
      } finally {
        setPageLoading(false);
      }
    };

    run();
  }, [user]);

  const handleAvatarChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setSelectedAvatarFile(file);
    const reader = new FileReader();
    reader.onload = () => {
      setAvatarPreview(reader.result?.toString() || '');
      setMsg(null);
    };
    reader.readAsDataURL(file);
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
      let uploadedAvatarUrl = avatarUrl;
      if (selectedAvatarFile) {
        uploadedAvatarUrl = await uploadProfilePhoto(selectedAvatarFile, user?.email);
      }

      await updateUser({
        fullName,
        name: fullName,
        avatarUrl: uploadedAvatarUrl,
        currentPassword: currentPw || undefined,
        newPassword: newPw || undefined,
      });
      setAvatarUrl(uploadedAvatarUrl);
      setAvatarPreview(uploadedAvatarUrl);
      setSelectedAvatarFile(null);
      setCurrentPw('');
      setNewPw('');
      setConfirmPw('');
      setMsg({ type: 'success', text: 'Profile updated successfully!' });
    } catch (err) {
      setMsg({ type: 'error', text: err.response?.data || err.message || 'Update failed.' });
    } finally {
      setSaving(false);
    }
  };

  if (pageLoading) {
    return (
      <div className="page-loading">
        <div className="spinner" />
        <p>Loading profile...</p>
      </div>
    );
  }

  const displayRole = profile?.role || user?.role || 'student';
  const displayName = fullName || user?.name || '';
  const displayAvatar = avatarPreview || avatarUrl || user?.avatarUrl || user?.avatarDataUrl || '';

  return (
    <div className="profile-page">
      <div className="profile-page-header">
        <h2 className="profile-page-title">Profile</h2>
        <p className="profile-page-subtitle">Manage your account information</p>
      </div>

      <div className="profile-layout">
        <div className="profile-left">
          <div className="profile-info-card">
            <div className="avatar">
              {displayAvatar ? (
                <img src={displayAvatar} alt={`${displayName} avatar`} className="avatar-image" />
              ) : (
                <span className="avatar-initials">{displayName.charAt(0).toUpperCase()}</span>
              )}
            </div>
            <button type="button" className="avatar-upload-btn" onClick={() => fileInputRef.current?.click()}>
              <ImagePlus size={16} />
              Change Photo
            </button>
            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              className="avatar-file-input"
              onChange={handleAvatarChange}
            />
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

        <div className="profile-form-card">
          <h3 className="form-card-title">Edit Profile</h3>

          <form onSubmit={handleSave} className="profile-form">
            {msg && (
              <div className={`profile-msg ${msg.type === 'success' ? 'msg-success' : 'msg-error'}`}>
                {msg.type === 'success' ? <CheckCircle size={15} /> : <AlertCircle size={15} />}
                <span>{msg.text}</span>
              </div>
            )}

            <div className="form-section">
              <h4 className="form-section-title">Personal Information</h4>
              <div className="form-group">
                <label className="form-label">Full Name</label>
                <input className="form-input" value={fullName} onChange={(e) => setFullName(e.target.value)} required />
              </div>
              <div className="form-group">
                <label className="form-label">Email</label>
                <input className="form-input form-input-disabled" value={user?.email || ''} disabled />
                <p className="field-hint">Email cannot be changed.</p>
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
