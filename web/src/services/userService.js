import axios from 'axios';

const API = 'http://localhost:8080/api';
const api = axios.create({ baseURL: API });

const userService = {
  updateProfile: async (userId, { fullName, currentPassword, newPassword }) => {
    const res = await api.put(`/user/${userId}`, {
      fullName,
      currentPassword,
      newPassword,
    });
    return res.data;
  },

  getProfile: async (email) => {
    const res = await api.get(`/user/me?email=${encodeURIComponent(email)}`);
    return res.data; // { userId, fullName, email, role, createdAt, lastLogin }
  },
};

export default userService;