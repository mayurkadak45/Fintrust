import api from './axios';

export const authApi = {
  login: (username, password) =>
    api.post('/auth/login', { username, password }),

  changePassword: (currentPassword, newPassword) =>
    api.post('/auth/change-password', { currentPassword, newPassword }),

  logout: () => api.post('/auth/logout'),
};
