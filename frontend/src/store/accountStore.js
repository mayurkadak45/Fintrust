import { create } from 'zustand';
import { accountsApi } from '../api/accounts';
import { transfersApi } from '../api/transfers';
import { beneficiariesApi } from '../api/beneficiaries';
import toast from 'react-hot-toast';

const useAccountStore = create((set) => ({
  accounts: [],
  transactions: [],
  beneficiaries: [],
  loading: false,

  fetchAccounts: async () => {
    set({ loading: true });
    try {
      const res = await accountsApi.getSummary();
      set({ accounts: res.data.data || [], loading: false });
    } catch (err) {
      set({ loading: false });
      toast.error('Failed to load accounts');
    }
  },

  fetchTransactionHistory: async (accountNo) => {
    set({ loading: true });
    try {
      const res = await transfersApi.getHistory(accountNo);
      set({ transactions: res.data.data || [], loading: false });
    } catch (err) {
      set({ loading: false });
      toast.error('Failed to load transactions');
    }
  },

  fetchMiniStatement: async (accountNo) => {
    set({ loading: true });
    try {
      const res = await transfersApi.getMiniStatement(accountNo);
      set({ transactions: res.data.data || [], loading: false });
    } catch (err) {
      set({ loading: false });
      toast.error('Failed to load mini statement');
    }
  },

  executeTransfer: async (srcAccount, destAccount, amount, remarks) => {
    set({ loading: true });
    try {
      await transfersApi.executeTransfer(srcAccount, destAccount, amount, remarks);
      toast.success('Transfer successful!');
      set({ loading: false });
      return true;
    } catch (err) {
      set({ loading: false });
      const msg = err.response?.data?.message || 'Transfer failed';
      toast.error(msg);
      return false;
    }
  },

  fetchBeneficiaries: async () => {
    set({ loading: true });
    try {
      const res = await beneficiariesApi.getAll();
      set({ beneficiaries: res.data.data || [], loading: false });
    } catch (err) {
      set({ loading: false });
      toast.error('Failed to load beneficiaries');
    }
  },

  addBeneficiary: async (accountNo, beneficiaryName, ifsc) => {
    try {
      await beneficiariesApi.add(accountNo, beneficiaryName, ifsc);
      toast.success('Beneficiary added!');
      return true;
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to add beneficiary';
      toast.error(msg);
      return false;
    }
  },

  removeBeneficiary: async (id) => {
    try {
      await beneficiariesApi.remove(id);
      toast.success('Beneficiary removed');
      return true;
    } catch (err) {
      toast.error('Failed to remove beneficiary');
      return false;
    }
  },
}));

export default useAccountStore;
