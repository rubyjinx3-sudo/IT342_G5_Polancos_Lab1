import { createContext, useContext, useState, useEffect } from 'react';
import authService from '../services/authService';

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user,    setUser]    = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const currentUser = authService.getCurrentUser();
    setUser(currentUser);
    setLoading(false);
  }, []);

  // ✅ FIX: unwrap result.user before storing in state
  const login = async (email, password) => {
    const result = await authService.login(email, password);
    setUser(result.user);
    return { user: result.user };
  };

  // ✅ FIX: pass formData as a single object
  const register = async (formData) => {
    try {
      const response = await authService.register(formData);
      return response;
    } catch (err) {
      const data = err.response?.data;
      let message;
      if (typeof data === 'string') {
        message = data;
      } else if (data?.message) {
        message = data.message;
      } else if (data?.error) {
        message = data.error;
      } else if (data?.errors) {
        message = Object.values(data.errors).join(', ');
      } else {
        message = err.message || 'Registration failed. Please try again.';
      }
      throw new Error(message);
    }
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  // ✅ FIX: authService.updateProfile takes one object, not (userId, userData)
  const updateUser = async (userData) => {
    const result = await authService.updateProfile(userData);
    const merged = { ...user, ...result.user };
    localStorage.setItem('ced_user', JSON.stringify(merged));
    setUser(merged);
    return merged;
  };

  return (
    <AuthContext.Provider value={{
      user,
      login,
      register,
      logout,
      updateUser,
      isAuthenticated: !!user,
      loading,
    }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};
