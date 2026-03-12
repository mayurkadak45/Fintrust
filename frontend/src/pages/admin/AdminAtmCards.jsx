import { useEffect, useState } from 'react';
import { cardsApi } from '../../api/cards';
import toast from 'react-hot-toast';

function AdminAtmCards() {
  const [loading, setLoading] = useState(true);
  const [status, setStatus] = useState('');
  const [cards, setCards] = useState([]);

  const load = async () => {
    setLoading(true);
    try {
      const res = await cardsApi.adminAtmCards(status || undefined);
      setCards(res.data?.data || []);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to load cards');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, [status]);

  const doAction = async (fn, msg) => {
    try {
      await fn();
      toast.success(msg);
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Action failed');
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>ATM Cards</h2>
          <p>Block/unblock cards and reset PIN attempts</p>
        </div>
        <div style={{ display: 'flex', gap: 10, alignItems: 'center', flexWrap: 'wrap' }}>
          <select className="select" value={status} onChange={(e) => setStatus(e.target.value)} style={{ minWidth: 200 }}>
            <option value="">All</option>
            <option value="ACTIVE">Active</option>
            <option value="BLOCKED">Blocked</option>
            <option value="EXPIRED">Expired</option>
          </select>
          <button className="btn btn-outline" onClick={load} disabled={loading}>Refresh</button>
        </div>
      </div>

      <div className="card">
        <div className="card-header"><h3>Cards</h3></div>
        {loading ? (
          <div className="loading">Loading...</div>
        ) : cards.length === 0 ? (
          <div className="empty">No cards found</div>
        ) : (
          <div className="table-responsive">
            <table className="table admin-table">
              <thead>
                <tr>
                  <th>Card Number</th>
                  <th>Account</th>
                  <th>Status</th>
                  <th>Incorrect Attempts</th>
                  <th>Expiry</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {cards.map((c) => (
                  <tr key={c.cardId}>
                    <td className="font-mono">{c.cardNumber}</td>
                    <td className="font-mono">{c.accountNo}</td>
                    <td>{c.status}</td>
                    <td>{c.incorrectAttempts}</td>
                    <td>{c.expiryDate}</td>
                    <td style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                      {c.status !== 'BLOCKED' ? (
                        <button className="btn btn-danger btn-sm" onClick={() => doAction(() => cardsApi.adminBlockCard(c.cardNumber), 'Card blocked')}>
                          Block
                        </button>
                      ) : (
                        <button className="btn btn-primary btn-sm" onClick={() => doAction(() => cardsApi.adminUnblockCard(c.cardNumber), 'Card unblocked')}>
                          Unblock
                        </button>
                      )}
                      <button className="btn btn-outline btn-sm" onClick={() => doAction(() => cardsApi.adminResetAttempts(c.cardNumber), 'Attempts reset')}>
                        Reset Attempts
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

export default AdminAtmCards;

