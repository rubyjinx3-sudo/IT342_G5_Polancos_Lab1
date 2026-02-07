import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const authService = {
  register: async (username, email, password) => {
    const response = await axios.post(`${API_URL}/auth/register`, {
      username,
      email,
      password,
    });
    return response.data;
  },

  login: async (username, password) => {
    const response = await axios.post(`${API_URL}/auth/login`, {
      username,
      password,
    }, {
      auth: {
        username,
        password,
      },
    });
    
    if (response.data) {
      localStorage.setItem('user', JSON.stringify({ username, password }));
    }
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('user');
  },

  getCurrentUser: () => {
    return JSON.parse(localStorage.getItem('user'));
  },

  getAuthHeader: () => {
    const user = JSON.parse(localStorage.getItem('user'));
    return user ? { username: user.username, password: user.password } : null;
  },
};

export default authService;