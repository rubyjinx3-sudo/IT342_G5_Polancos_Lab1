import axios from 'axios';
import { API_BASE_URL } from '../config/appConfig';

const api = axios.create({ baseURL: API_BASE_URL });

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
    return res.data;
  },
};

export default userService;
