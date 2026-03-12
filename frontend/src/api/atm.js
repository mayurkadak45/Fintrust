import api from './axios';

export const atmApi = {
  validateCard: (cardNumber) =>
    api.post('/atm/validate-card', { cardNumber }),

  validatePin: (sessionToken, pin) =>
    api.post('/atm/validate-pin', { sessionToken, pin }),

  getBalance: (sessionToken) =>
    api.get('/atm/balance', { params: { sessionToken } }),

  withdraw: (sessionToken, amount) =>
    api.post('/atm/withdraw', { sessionToken, amount }),

  deposit: (sessionToken, amount) =>
    api.post('/atm/deposit', { sessionToken, amount }),

  getMiniStatement: (sessionToken) =>
    api.get('/atm/mini-statement', { params: { sessionToken } }),

  endSession: (sessionToken) =>
    api.post('/atm/end-session', null, { params: { sessionToken } }),

  changePin: ({ cardNumber, accountNo, username, password, newPin }) =>
    api.post('/atm/change-pin', { cardNumber, accountNo, username, password, newPin }),
};
