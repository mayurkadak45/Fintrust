import { useState, useEffect } from 'react';
import useAccountStore from '../store/accountStore';

function MiniStatement() {
  const { accounts, transactions, fetchAccounts, fetchMiniStatement, loading } = useAccountStore();
  const [selectedAccount, setSelectedAccount] = useState('');

  useEffect(() => {
    fetchAccounts();
  }, [fetchAccounts]);

  useEffect(() => {
    if (selectedAccount) {
      fetchMiniStatement(selectedAccount);
    }
  }, [selectedAccount, fetchMiniStatement]);

  useEffect(() => {
    if (!selectedAccount && accounts.length > 0) {
      setSelectedAccount(accounts[0].accountNo);
    }
  }, [accounts, selectedAccount]);

  return (
    <div className="page">
      <div className="page-header">
        <h2>Mini Statement</h2>
        <p>Last 10 transactions</p>
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
                {acc.accountNo} ({acc.accountType})
              </option>
            ))}
          </select>
        </div>

        {loading ? (
          <div className="loading">Loading...</div>
        ) : !selectedAccount ? (
          <div className="empty">Select an account</div>
        ) : transactions.length === 0 ? (
          <div className="empty">No recent transactions</div>
        ) : (
          <div className="mini-statement">
            {transactions.map((txn, idx) => (
              <div key={txn.txnId || idx} className="mini-statement-item">
                <div className="mini-statement-left">
                  <span className={`mini-indicator ${txn.type === 'CREDIT' ? 'credit' : 'debit'}`}>
                    {txn.type === 'CREDIT' ? '▲' : '▼'}
                  </span>
                  <div>
                    <div className="mini-desc">{txn.description || txn.type}</div>
                    <div className="mini-date">
                      {txn.timestamp ? new Date(txn.timestamp).toLocaleString() : '-'}
                    </div>
                  </div>
                </div>
                <div className={`mini-amount ${txn.type === 'CREDIT' ? 'text-success' : 'text-danger'}`}>
                  {txn.type === 'CREDIT' ? '+' : '-'}${parseFloat(txn.amount).toFixed(2)}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default MiniStatement;
