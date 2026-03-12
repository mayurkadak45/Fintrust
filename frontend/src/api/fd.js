import api from './axios';

export const fdApi = {
  list: () => api.get('/accounts/fd'),
  create: ({ amount, years }) => api.post('/accounts/fd', { amount, years }),
};

