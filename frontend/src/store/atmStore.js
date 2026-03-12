import { create } from 'zustand';
import { atmApi } from '../api/atm';
import toast from 'react-hot-toast';

const useAtmStore = create((set, get) => ({
  sessionToken: null,
  cardNumber: null,
  rawCardNumber: null,
  accountNo: null,
  pinVerified: false,
  balance: null,
  miniStatement: [],
  lastTxn: null,
  loading: false,
  step: 'CARD', // CARD -> OPTION -> PIN -> MENU
  pinAttemptsRemaining: 3,
  cardBlocked: false,

  validateCard: async (cardNumber) => {
    set({ loading: true });
    try {
      const res = await atmApi.validateCard(cardNumber);
      const data = res.data.data;
      set({
        sessionToken: data.sessionToken,
        cardNumber: data.cardNumber,
        rawCardNumber: cardNumber,
        step: 'OPTION',
        pinAttemptsRemaining: 3,
        cardBlocked: false,
        loading: false,
      });
      toast.success('Card validated.');
      return true;
    } catch (err) {
      set({ loading: false });
      toast.error(err.response?.data?.message || 'Card validation failed');
      return false;
    }
  },

  goToPin: () => set({ step: 'PIN' }),
  goToChangePin: () => set({ step: 'CHANGE_PIN' }),
  goToOption: () => set({ step: 'OPTION' }),

  validatePin: async (pin) => {
    const { sessionToken } = get();
    set({ loading: true });
    try {
      const res = await atmApi.validatePin(sessionToken, pin);
      const data = res.data.data;
      set({
        sessionToken: data.sessionToken,
        cardNumber: data.cardNumber,
        accountNo: data.accountNo,
        pinVerified: true,
        step: 'MENU',
        pinAttemptsRemaining: 3,
        cardBlocked: false,
        loading: false,
      });
      toast.success('PIN verified!');
      return true;
    } catch (err) {
      let remaining = get().pinAttemptsRemaining - 1;
      if (remaining < 0) remaining = 0;
      const blocked = remaining <= 0;

      set({ loading: false, pinAttemptsRemaining: remaining, cardBlocked: blocked });

      if (blocked) {
        toast.error('ATM locked. Card is blocked due to multiple incorrect attempts. Contact admin.');
      } else {
        toast.error(`Invalid PIN. Attempts remaining: ${remaining}`);
      }
      return false;
    }
  },

  changePin: async ({ accountNo, username, password, newPin }) => {
    const { rawCardNumber } = get();
    set({ loading: true });
    try {
      await atmApi.changePin({ cardNumber: rawCardNumber, accountNo, username, password, newPin });
      set({ loading: false, step: 'OPTION' });
      toast.success('PIN changed. You can now enter your PIN.');
      return true;
    } catch (err) {
      set({ loading: false });
      toast.error(err.response?.data?.message || 'Failed to change PIN');
      return false;
    }
  },

  checkBalance: async () => {
    const { sessionToken } = get();
    set({ loading: true });
    try {
      const res = await atmApi.getBalance(sessionToken);
      set({ balance: res.data.data, loading: false });
      return res.data.data;
    } catch (err) {
      set({ loading: false });
      toast.error(err.response?.data?.message || 'Failed to get balance');
      return null;
    }
  },

  withdraw: async (amount) => {
    const { sessionToken } = get();
    set({ loading: true });
    try {
      const res = await atmApi.withdraw(sessionToken, amount);
      toast.success(`Withdrawn: ${amount}`);
      set({ loading: false, lastTxn: res.data?.data || null });
      return true;
    } catch (err) {
      set({ loading: false });
      toast.error(err.response?.data?.message || 'Withdrawal failed');
      return false;
    }
  },

  deposit: async (amount) => {
    const { sessionToken } = get();
    set({ loading: true });
    try {
      const res = await atmApi.deposit(sessionToken, amount);
      toast.success(`Deposited: ${amount}`);
      set({ loading: false, lastTxn: res.data?.data || null });
      return true;
    } catch (err) {
      set({ loading: false });
      toast.error(err.response?.data?.message || 'Deposit failed');
      return false;
    }
  },

  fetchMiniStatement: async () => {
    const { sessionToken } = get();
    set({ loading: true });
    try {
      const res = await atmApi.getMiniStatement(sessionToken);
      set({ miniStatement: res.data.data || [], loading: false });
    } catch (err) {
      set({ loading: false });
      toast.error(err.response?.data?.message || 'Failed to get statement');
    }
  },

  endSession: async () => {
    const { sessionToken } = get();
    try {
      if (sessionToken) {
        await atmApi.endSession(sessionToken);
      }
    } catch {
      // ignore
    }
    set({
      sessionToken: null,
      cardNumber: null,
      rawCardNumber: null,
      accountNo: null,
      pinVerified: false,
      balance: null,
      miniStatement: [],
      lastTxn: null,
      step: 'CARD',
      pinAttemptsRemaining: 3,
      cardBlocked: false,
    });
    toast.success('Session ended. Collect your card.');
  },
}));

export default useAtmStore;
