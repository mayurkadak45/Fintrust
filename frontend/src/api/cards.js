import api from './axios';

export const cardsApi = {
  myRequest: () => api.get('/accounts/cards/me'),
  request: () => api.post('/accounts/cards/request'),

  adminPending: () => api.get('/accounts/admin/cards/pending'),
  adminApprove: (id) => api.post(`/accounts/admin/cards/${id}/approve`),
  adminReject: (id) => api.post(`/accounts/admin/cards/${id}/reject`),

  adminAtmCards: (status) => api.get('/atm/admin/cards', { params: status ? { status } : {} }),
  adminBlockCard: (cardNumber) => api.post(`/atm/admin/cards/${cardNumber}/block`),
  adminUnblockCard: (cardNumber) => api.post(`/atm/admin/cards/${cardNumber}/unblock`),
  adminResetAttempts: (cardNumber) => api.post(`/atm/admin/cards/${cardNumber}/reset-attempts`),
};

