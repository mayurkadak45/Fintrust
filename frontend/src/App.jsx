import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/layout/Layout';
import AdminLayout from './components/layout/AdminLayout';
import ProtectedRoute from './components/common/ProtectedRoute';
import Login from './pages/Login';
import Register from './pages/Register';
import Home from './pages/Home';
import Dashboard from './pages/Dashboard';
import UserDashboard from './pages/UserDashboard';
import TransactionHistory from './pages/TransactionHistory';
import FundTransfer from './pages/FundTransfer';
import Beneficiaries from './pages/Beneficiaries';
import MiniStatement from './pages/MiniStatement';
import ChangePassword from './pages/ChangePassword';
import VirtualAtm from './pages/VirtualAtm';
import CardEntry from './pages/CardEntry';
import DebitCard from './pages/DebitCard';
import FixedDeposits from './pages/FixedDeposits';
import AdminDashboard from './pages/admin/AdminDashboard';
import PendingAccounts from './pages/admin/PendingAccounts';
import AdminCustomers from './pages/admin/AdminCustomers';
import AdminTransactions from './pages/admin/AdminTransactions';
import SearchAccount from './pages/admin/SearchAccount';
import AdminDebitCards from './pages/admin/AdminDebitCards';
import AdminAtmCards from './pages/admin/AdminAtmCards';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/atm" element={<VirtualAtm />} />
      <Route path="/card" element={<CardEntry />} />

      {/* Customer standalone route */}
      <Route path="/user" element={
        <ProtectedRoute><UserDashboard /></ProtectedRoute>
      } />

      {/* Customer routes with sidebar layout */}
      <Route
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/transactions" element={<TransactionHistory />} />
        <Route path="/transfer" element={<FundTransfer />} />
        <Route path="/beneficiaries" element={<Beneficiaries />} />
        <Route path="/mini-statement" element={<MiniStatement />} />
        <Route path="/change-password" element={<ChangePassword />} />
        <Route path="/debit-card" element={<DebitCard />} />
        <Route path="/fd" element={<FixedDeposits />} />
      </Route>

      {/* Admin routes */}
      <Route
        element={
          <ProtectedRoute>
            <AdminLayout />
          </ProtectedRoute>
        }
      >
        <Route path="/admin/dashboard" element={<AdminDashboard />} />
        <Route path="/admin/pending-accounts" element={<PendingAccounts />} />
        <Route path="/admin/all-accounts" element={<AdminCustomers />} />
        <Route path="/admin/transactions" element={<AdminTransactions />} />
        <Route path="/admin/search-account" element={<SearchAccount />} />
        <Route path="/admin/debit-cards" element={<AdminDebitCards />} />
        <Route path="/admin/atm-cards" element={<AdminAtmCards />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;
