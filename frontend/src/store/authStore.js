import { create } from 'zustand';
import { authApi } from '../api/auth';
import toast from 'react-hot-toast';

const useAuthStore = create((set) => ({
  user: JSON.parse(localStorage.getItem('user') || 'null'),
  token: localStorage.getItem('token') || null,
  loading: false,

  login: async (username, password) => {
    set({ loading: true });
    try {
      const res = await authApi.login(username, password);
      const { token, username: user, role } = res.data.data;
      const userData = { username: user, role };

      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(userData));

      set({ user: userData, token, loading: false });
      toast.success('Login successful!');
      return true;
    } catch (err) {
      set({ loading: false });
      const msg = err.response?.data?.message || 'Login failed';
      toast.error(msg);
      return false;
    }
  },

  logout: async () => {
    try {
      await authApi.logout();
    } catch {
      // ignore logout errors
    }
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    set({ user: null, token: null });
    toast.success('Logged out');
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  },
}));

export default useAuthStore;
