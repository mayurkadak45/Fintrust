import api from './axios';

export const accountsApi = {
  getSummary: () => api.get('/accounts/summary'),

  getBalance: (accountNo) => api.get(`/accounts/${accountNo}/balance`),
};
