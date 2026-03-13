import axios from 'axios';
import toast from 'react-hot-toast';

const api = axios.create({
  baseURL: '192.168.1.7:8080/api',
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Global error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const reqUrl = error.config?.url || '';
      const isAtmCall = reqUrl.includes('/atm/') || reqUrl.startsWith('/atm');
      const isAtmScreen = window.location.pathname === '/atm' || window.location.pathname === '/card';
      // ATM endpoints can legitimately return 401 (invalid PIN); do NOT log the user out.
      if (isAtmCall || isAtmScreen) {
        return Promise.reject(error);
      }

      localStorage.removeItem('token');
      localStorage.removeItem('user');
      if (window.location.pathname !== '/login' && window.location.pathname !== '/atm') {
        toast.error('Session expired. Please login again.');
        window.location.href = '/login';
      }
    }

    return Promise.reject(error);
  }
);

export default api;
