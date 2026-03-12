import api from './axios';

export const adminApi = {
  getDashboard: () => api.get('/accounts/admin/dashboard'),

  getPending: () => api.get('/accounts/admin/pending'),

  getPendingById: (id) => api.get(`/accounts/admin/pending/${id}`),

  approve: (id) => api.post(`/accounts/admin/pending/${id}/approve`),

  reject: (id) => api.post(`/accounts/admin/pending/${id}/reject`),

  getCustomers: () => api.get('/accounts/admin/customers'),

  search: (q) => api.get('/accounts/admin/search', { params: { q } }),

  getTransactions: () => api.get('/transfers/all'),
};
