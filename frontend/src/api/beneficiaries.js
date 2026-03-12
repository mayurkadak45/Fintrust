import api from './axios';

export const beneficiariesApi = {
  getAll: () => api.get('/beneficiaries'),

  add: (accountNo, beneficiaryName, ifsc) =>
    api.post('/beneficiaries', { accountNo, beneficiaryName, ifsc }),

  remove: (id) => api.delete(`/beneficiaries/${id}`),
};
