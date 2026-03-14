import axios from 'axios';
import { API_BASE_URL, AUTH_STORAGE_KEY } from '../config/appConfig';
import { getProfilePreferences, saveProfilePreferences } from './profilePreferences';

const api = axios.create({ baseURL: API_BASE_URL });

const withProfilePreferences = (user) => ({
  ...user,
  ...getProfilePreferences(user.email),
});

const authService = {
  login: async (email, password) => {
    const res = await api.post('/auth/login', { email, password });
    const data = res.data;

    const user = withProfilePreferences({
      userId: data.userId,
      id: data.userId,
      email: data.email,
      name: data.fullName,
      fullName: data.fullName,
      role: data.role?.toLowerCase(),
    });

    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(user));
    return { success: true, user };
  },

  register: async (userData) => {
    const res = await api.post('/auth/register', {
      fullName: userData.fullName,
      email: userData.email,
      password: userData.password,
      role: (userData.role || 'student').toUpperCase(),
    });
    const data = res.data;

    const user = withProfilePreferences({
      userId: data.userId,
      id: data.userId,
      email: data.email,
      name: data.fullName,
      fullName: data.fullName,
      role: data.role?.toLowerCase(),
    });

    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(user));
    return { success: true, user };
  },

  logout: () => {
    localStorage.removeItem(AUTH_STORAGE_KEY);
    return { success: true };
  },

  getCurrentUser: () => {
    try {
      const user = JSON.parse(localStorage.getItem(AUTH_STORAGE_KEY) || 'null');
      return user ? withProfilePreferences(user) : null;
    } catch {
      return null;
    }
  },

  isAuthenticated: () => !!authService.getCurrentUser(),

  updateProfile: async (userData) => {
    const currentUser = authService.getCurrentUser();
    if (!currentUser) throw new Error('Not logged in');

    await api.put(`/user/${currentUser.userId}`, {
      fullName: userData.fullName || userData.name,
      currentPassword: userData.currentPassword || undefined,
      newPassword: userData.newPassword || undefined,
    });

    const profilePreferences = saveProfilePreferences(currentUser.email, {
      avatarUrl: userData.avatarUrl ?? currentUser.avatarUrl ?? null,
    });

    const updatedUser = {
      ...currentUser,
      ...profilePreferences,
      name: userData.fullName || userData.name || currentUser.name,
      fullName: userData.fullName || userData.name || currentUser.fullName,
    };

    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(updatedUser));
    return { success: true, user: updatedUser };
  },

  getFullProfile: async (email) => {
    const res = await api.get(`/user/me?email=${encodeURIComponent(email)}`);
    return res.data;
  },
};

export default authService;
