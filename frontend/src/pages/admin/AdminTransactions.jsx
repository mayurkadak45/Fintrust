import { useEffect, useState } from 'react';
import useAdminStore from '../../store/adminStore';

function AdminTransactions() {
  const { transactions, fetchTransactions, loading } = useAdminStore();
  const [selectedTxn, setSelectedTxn] = useState(null);

  useEffect(() => {
    fetchTransactions();
  }, [fetchTransactions]);

  return (
    <div className="page">
      <div className="page-header">
        <h2>Transaction History</h2>
      </div>

      <div className="card">
        {loading && transactions.length === 0 ? (
          <div className="loading">Loading...</div>
        ) : transactions.length === 0 ? (
          <div className="empty">No transactions found</div>
        ) : (
          <div className="table-responsive">
            <table className="table admin-table">
              <thead>
                <tr>
                  <th>Txn ID</th>
                  <th>Account</th>
                  <th>Action</th>
                  <th>Amount</th>
                  <th>Status</th>
                  <th>Date</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {transactions.map((txn) => (
                  <tr key={txn.txnId}>
                    <td className="font-bold">TXN{txn.txnId}</td>
                    <td className="font-mono">{txn.accountNo}</td>
                    <td>
                      {txn.type === 'CREDIT'
                        ? 'DEPOSIT'
                        : txn.description?.includes('Transfer')
                        ? 'TRANSFER'
                        : 'WITHDRAW'}
                    </td>
                    <td>₹{parseFloat(txn.amount).toLocaleString('en-IN')}</td>
                    <td>
                      <span className={`badge badge-${txn.status === 'SUCCESS' ? 'success' : 'danger'}`}>
                        {txn.status}
                      </span>
                    </td>
                    <td>{txn.timestamp ? new Date(txn.timestamp).toLocaleDateString() : '-'}</td>
                    <td>
                      <button
                        className="btn btn-sm btn-primary"
                        onClick={() => setSelectedTxn(txn)}
                      >
                        View
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {selectedTxn && (
        <div className="modal-overlay" onClick={() => setSelectedTxn(null)}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()}>
            <h3 style={{ marginBottom: 12 }}>Transaction Details</h3>
            <div className="credentials-box" style={{ marginTop: 8 }}>
              <div className="cred-row">
                <span className="cred-label">Txn ID</span>
                <span className="cred-value font-mono">TXN{selectedTxn.txnId}</span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Account</span>
                <span className="cred-value font-mono">{selectedTxn.accountNo}</span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Type</span>
                <span className="cred-value">{selectedTxn.type}</span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Amount</span>
                <span className="cred-value">
                  ₹{parseFloat(selectedTxn.amount).toLocaleString('en-IN')}
                </span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Balance After</span>
                <span className="cred-value">
                  {selectedTxn.balanceAfter != null
                    ? `₹${parseFloat(selectedTxn.balanceAfter).toLocaleString('en-IN')}`
                    : '-'}
                </span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Status</span>
                <span className="cred-value">{selectedTxn.status}</span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Timestamp</span>
                <span className="cred-value">
                  {selectedTxn.timestamp
                    ? new Date(selectedTxn.timestamp).toLocaleString()
                    : '-'}
                </span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Description</span>
                <span className="cred-value">{selectedTxn.description || '-'}</span>
              </div>
            </div>
            <div style={{ marginTop: 16, textAlign: 'right' }}>
              <button className="btn btn-outline" onClick={() => setSelectedTxn(null)}>
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default AdminTransactions;
