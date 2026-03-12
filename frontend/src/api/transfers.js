import api from './axios';

export const transfersApi = {
  executeTransfer: (srcAccount, destAccount, amount, remarks) =>
    api.post('/transfers', { srcAccount, destAccount, amount, remarks }),

  validateTransfer: (srcAccount, destAccount, amount) =>
    api.post('/transfers/validate', { srcAccount, destAccount, amount }),

  getHistory: (accountNo) =>
    api.get('/transfers/history', { params: { accountNo } }),

  getMiniStatement: (accountNo) =>
    api.get(`/transfers/mini-statement/${accountNo}`),
};
