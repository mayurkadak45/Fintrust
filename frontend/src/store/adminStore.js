import { create } from 'zustand';
import { adminApi } from '../api/admin';
import toast from 'react-hot-toast';

const useAdminStore = create((set) => ({
  stats: null,
  pendingApplications: [],
  customers: [],
  searchResults: [],
  transactions: [],
  loading: false,

  fetchStats: async () => {
    set({ loading: true });
    try {
      const res = await adminApi.getDashboard();
      set({ stats: res.data.data, loading: false });
    } catch (err) {
      set({ loading: false });
      toast.error('Failed to load dashboard stats');
    }
  },

  fetchPending: async () => {
    set({ loading: true });
    try {
      const res = await adminApi.getPending();
      set({ pendingApplications: res.data.data || [], loading: false });
    } catch (err) {
      set({ loading: false });
      toast.error('Failed to load pending applications');
    }
  },

  approveApplication: async (id) => {
    try {
      const res = await adminApi.approve(id);
      const approved = res.data?.data;
      toast.success('Application approved!');
      const res2 = await adminApi.getPending();
      set({ pendingApplications: res2.data.data || [], lastApprovedCredentials: approved || null });
      return approved;
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to approve');
      return null;
    }
  },
  lastApprovedCredentials: null,
  clearLastApprovedCredentials: () => set({ lastApprovedCredentials: null }),

  rejectApplication: async (id) => {
    try {
      await adminApi.reject(id);
      toast.success('Application rejected');
      const res = await adminApi.getPending();
      set({ pendingApplications: res.data.data || [] });
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to reject');
    }
  },

  fetchCustomers: async () => {
    set({ loading: true });
    try {
      const res = await adminApi.getCustomers();
      set({ customers: res.data.data || [], loading: false });
    } catch (err) {
      set({ loading: false });
      toast.error('Failed to load customers');
    }
  },

  searchCustomers: async (query) => {
    set({ loading: true });
    try {
      const res = await adminApi.search(query);
      set({ searchResults: res.data.data || [], loading: false });
    } catch (err) {
      set({ loading: false });
      toast.error('Search failed');
    }
  },

  fetchTransactions: async () => {
    set({ loading: true });
    try {
      const res = await adminApi.getTransactions();
      set({ transactions: res.data.data || [], loading: false });
    } catch (err) {
      set({ loading: false });
      toast.error('Failed to load transactions');
    }
  },
}));

export default useAdminStore;
