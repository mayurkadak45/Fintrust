import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import useAccountStore from '../store/accountStore';
import useAuthStore from '../store/authStore';
import { MdAtm, MdLogout, MdArrowDownward, MdArrowUpward, MdSwapHoriz, MdLock, MdReceipt } from 'react-icons/md';

function UserDashboard() {
  const { accounts, transactions, fetchAccounts, fetchMiniStatement, loading } = useAccountStore();
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    fetchAccounts();
  }, []);

  const account = accounts[0] || null;

  useEffect(() => {
    if (account) {
      fetchMiniStatement(account.accountNo);
    }
  }, [account, fetchMiniStatement]);

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div className="user-dashboard">
      {/* Top Header */}
      <div className="user-header">
        <div>
          <h1><span className="text-primary">Welcome,</span> {account?.customerName || user?.username}</h1>
          <p className="text-muted">Your account summary at FinTrust Bank</p>
        </div>
        <div className="user-header-actions">
          <button className="btn btn-primary btn-sm" onClick={() => navigate('/atm')}>
            <MdAtm /> Access ATM
          </button>
          <button className="btn btn-danger btn-sm" onClick={handleLogout}>
            <MdLogout /> Logout
          </button>
        </div>
      </div>

      {/* Info Cards Row */}
      <div className="user-info-cards">
        <div className="user-info-card profile-card">
          <div className="user-avatar-lg">{(account?.customerName || 'U').charAt(0)}</div>
          <strong>{account?.customerName || user?.username}</strong>
          <span className={`badge badge-${account?.status === 'ACTIVE' ? 'success' : 'danger'}`}>
            {account?.status || 'N/A'}
          </span>
        </div>
        <div className="user-info-card">
          <span className="info-label">Account Type</span>
          <strong className="info-value">{account?.accountType || 'N/A'}</strong>
        </div>
        <div className="user-info-card">
          <span className="info-label">IFSC</span>
          <strong className="info-value">FIN0001234</strong>
        </div>
        <div className="user-info-card mini-stmt-card" onClick={() => navigate('/mini-statement')}>
          <MdReceipt size={24} />
          <strong>Mini Statement</strong>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="user-actions">
        <button className="action-btn action-withdraw" onClick={() => navigate('/transfer')}>
          <MdArrowDownward /> Withdraw
        </button>
        <button className="action-btn action-deposit" onClick={() => navigate('/transfer')}>
          <MdArrowUpward /> Deposit
        </button>
        <button className="action-btn action-transfer" onClick={() => navigate('/transfer')}>
          <MdSwapHoriz /> Transfer
        </button>
        <button className="action-btn action-pin" onClick={() => navigate('/change-password')}>
          <MdLock /> Change PIN
        </button>
      </div>

      {/* Recent Transactions */}
      <div className="card user-txn-card">
        <div className="card-header">
          <h3>Recent Transactions</h3>
        </div>
        {loading ? (
          <div className="loading">Loading...</div>
        ) : transactions.length === 0 ? (
          <div className="empty">No recent transactions</div>
        ) : (
          <div className="table-responsive">
            <table className="table">
              <thead>
                <tr>
                  <th>Type</th>
                  <th>Amount</th>
                  <th>Date</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {transactions.slice(0, 5).map((txn, idx) => (
                  <tr key={txn.txnId || idx}>
                    <td className={txn.type === 'CREDIT' ? 'text-success font-bold' : 'text-danger font-bold'}>
                      {txn.description || (txn.type === 'CREDIT' ? 'Deposit' : 'Withdraw')}
                    </td>
                    <td>{parseFloat(txn.amount).toLocaleString('en-IN')}</td>
                    <td>{txn.timestamp ? new Date(txn.timestamp).toLocaleDateString() : '-'}</td>
                    <td>
                      <span className={`badge badge-${txn.status === 'SUCCESS' ? 'success' : 'warning'}`}>
                        {txn.status === 'SUCCESS' ? 'Completed' : 'Pending'}
                      </span>
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

export default UserDashboard;
