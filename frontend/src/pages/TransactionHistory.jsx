import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import useAccountStore from '../store/accountStore';

function TransactionHistory() {
  const { accounts, transactions, fetchAccounts, fetchTransactionHistory, loading } = useAccountStore();
  const [searchParams] = useSearchParams();
  const [selectedAccount, setSelectedAccount] = useState(searchParams.get('account') || '');

  useEffect(() => {
    fetchAccounts();
  }, [fetchAccounts]);

  useEffect(() => {
    if (selectedAccount) {
      fetchTransactionHistory(selectedAccount);
    }
  }, [selectedAccount, fetchTransactionHistory]);

  // Auto-select first account if none selected
  useEffect(() => {
    if (!selectedAccount && accounts.length > 0) {
      setSelectedAccount(accounts[0].accountNo);
    }
  }, [accounts, selectedAccount]);

  return (
    <div className="page">
      <div className="page-header">
        <h2>Transaction History</h2>
      </div>

      <div className="card">
        <div className="card-header">
          <h3>Select Account</h3>
          <select
            className="select"
            value={selectedAccount}
            onChange={(e) => setSelectedAccount(e.target.value)}
          >
            <option value="">-- Select Account --</option>
            {accounts.map((acc) => (
              <option key={acc.accountNo} value={acc.accountNo}>
                {acc.accountNo} ({acc.accountType}) - ${parseFloat(acc.balance).toFixed(2)}
              </option>
            ))}
          </select>
        </div>

        {loading ? (
          <div className="loading">Loading transactions...</div>
        ) : !selectedAccount ? (
          <div className="empty">Please select an account to view transactions</div>
        ) : transactions.length === 0 ? (
          <div className="empty">No transactions found for this account</div>
        ) : (
          <div className="table-responsive">
            <table className="table">
              <thead>
                <tr>
                  <th>TXN ID</th>
                  <th>Date</th>
                  <th>Type</th>
                  <th>Amount</th>
                  <th>Balance After</th>
                  <th>Status</th>
                  <th>Description</th>
                </tr>
              </thead>
              <tbody>
                {transactions.map((txn) => (
                  <tr key={txn.txnId}>
                    <td className="font-mono">#{txn.txnId}</td>
                    <td>{txn.timestamp ? new Date(txn.timestamp).toLocaleString() : '-'}</td>
                    <td>
                      <span className={`badge badge-${txn.type === 'CREDIT' ? 'success' : 'danger'}`}>
                        {txn.type}
                      </span>
                    </td>
                    <td className={txn.type === 'CREDIT' ? 'text-success' : 'text-danger'}>
                      {txn.type === 'CREDIT' ? '+' : '-'}${parseFloat(txn.amount).toFixed(2)}
                    </td>
                    <td>${txn.balanceAfter ? parseFloat(txn.balanceAfter).toFixed(2) : '-'}</td>
                    <td>
                      <span className={`badge badge-${txn.status === 'SUCCESS' ? 'success' : 'danger'}`}>
                        {txn.status}
                      </span>
                    </td>
                    <td>{txn.description || '-'}</td>
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

export default TransactionHistory;
