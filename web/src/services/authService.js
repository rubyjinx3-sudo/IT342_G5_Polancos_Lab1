import axios from 'axios';

const API = 'http://localhost:8080/api';
const STORAGE_KEY = 'ced_user';

const api = axios.create({ baseURL: API });

const authService = {
  // ── LOGIN ─────────────────────────────────────────────
  login: async (email, password) => {
    const res = await api.post('/auth/login', { email, password });
    const data = res.data;

    const user = {
      userId: data.userId,
      id: data.userId,       // alias kept for compatibility
      email: data.email,
      name: data.fullName,
      fullName: data.fullName,
      role: data.role?.toLowerCase(), // 'student' | 'organizer'
    };

    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    return { success: true, user };
  },

  // ── REGISTER ──────────────────────────────────────────
  register: async (userData) => {
    const res = await api.post('/auth/register', {
      fullName: userData.fullName,
      email: userData.email,
      password: userData.password,
      role: (userData.role || 'student').toUpperCase(),
    });
    const data = res.data;

    const user = {
      userId: data.userId,
      id: data.userId,
      email: data.email,
      name: data.fullName,
      fullName: data.fullName,
      role: data.role?.toLowerCase(),
    };

    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    return { success: true, user };
  },

  // ── LOGOUT ────────────────────────────────────────────
  logout: () => {
    localStorage.removeItem(STORAGE_KEY);
    return { success: true };
  },

  // ── GET CURRENT USER ──────────────────────────────────
  getCurrentUser: () => {
    try {
      return JSON.parse(localStorage.getItem(STORAGE_KEY) || 'null');
    } catch {
      return null;
    }
  },

  // ── IS AUTHENTICATED ──────────────────────────────────
  isAuthenticated: () => !!authService.getCurrentUser(),

  // ── UPDATE PROFILE (calls PUT /api/user/{id}) ─────────
  updateProfile: async (userData) => {
    const currentUser = authService.getCurrentUser();
    if (!currentUser) throw new Error('Not logged in');

    await api.put(`/user/${currentUser.userId}`, {
      fullName: userData.fullName || userData.name,
      currentPassword: userData.currentPassword || undefined,
      newPassword: userData.newPassword || undefined,
    });

    const updatedUser = {
      ...currentUser,
      name: userData.fullName || userData.name || currentUser.name,
      fullName: userData.fullName || userData.name || currentUser.fullName,
    };

    localStorage.setItem(STORAGE_KEY, JSON.stringify(updatedUser));
    return { success: true, user: updatedUser };
  },

  // ── GET FULL PROFILE FROM BACKEND ─────────────────────
  getFullProfile: async (email) => {
    const res = await api.get(`/user/me?email=${encodeURIComponent(email)}`);
    return res.data; // UserResponse: { userId, fullName, email, role, createdAt, lastLogin }
  },
};

export default authService;