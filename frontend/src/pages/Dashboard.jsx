import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import useAccountStore from '../store/accountStore';
import useAuthStore from '../store/authStore';
import { MdAccountBalance, MdTrendingUp, MdSwapHoriz } from 'react-icons/md';

function Dashboard() {
  const { accounts, fetchAccounts, loading } = useAccountStore();
  const { user } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    fetchAccounts();
  }, []);

  const totalBalance = accounts.reduce(
    (sum, acc) => sum + parseFloat(acc.balance || 0),
    0
  );

  return (
    <div className="page">
      <div className="page-header">
        <h2>Welcome back, {user?.username}!</h2>
        <p>Here's your account overview</p>
      </div>

      {/* Summary Cards */}
      <div className="stats-grid">
        <div className="stat-card stat-primary">
          <div className="stat-icon"><MdAccountBalance /></div>
          <div className="stat-info">
            <span className="stat-label">Total Balance</span>
            <span className="stat-value">
              ${totalBalance.toLocaleString('en-US', { minimumFractionDigits: 2 })}
            </span>
          </div>
        </div>
        <div className="stat-card stat-success">
          <div className="stat-icon"><MdTrendingUp /></div>
          <div className="stat-info">
            <span className="stat-label">Active Accounts</span>
            <span className="stat-value">{accounts.filter((a) => a.status === 'ACTIVE').length}</span>
          </div>
        </div>
        <div className="stat-card stat-info">
          <div className="stat-icon"><MdSwapHoriz /></div>
          <div className="stat-info">
            <span className="stat-label">Quick Transfer</span>
            <button className="btn btn-sm btn-primary" onClick={() => navigate('/transfer')}>
              Transfer Now
            </button>
          </div>
        </div>
      </div>

      {/* Accounts Table */}
      <div className="card">
        <div className="card-header">
          <h3>Your Accounts</h3>
        </div>
        {loading ? (
          <div className="loading">Loading accounts...</div>
        ) : accounts.length === 0 ? (
          <div className="empty">No accounts found</div>
        ) : (
          <div className="table-responsive">
            <table className="table">
              <thead>
                <tr>
                  <th>Account No</th>
                  <th>Type</th>
                  <th>Balance</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {accounts.map((acc) => (
                  <tr key={acc.accountNo}>
                    <td className="font-mono">{acc.accountNo}</td>
                    <td>
                      <span className={`badge badge-${acc.accountType === 'SAVINGS' ? 'info' : 'warning'}`}>
                        {acc.accountType}
                      </span>
                    </td>
                    <td className="font-bold">
                      ${parseFloat(acc.balance).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                    </td>
                    <td>
                      <span className={`badge badge-${acc.status === 'ACTIVE' ? 'success' : 'danger'}`}>
                        {acc.status}
                      </span>
                    </td>
                    <td>
                      <button
                        className="btn btn-sm btn-outline"
                        onClick={() => navigate(`/transactions?account=${acc.accountNo}`)}
                      >
                        View History
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

export default Dashboard;
