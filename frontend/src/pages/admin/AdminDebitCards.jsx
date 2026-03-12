import { useEffect, useState } from 'react';
import { cardsApi } from '../../api/cards';
import toast from 'react-hot-toast';

function AdminDebitCards() {
  const [loading, setLoading] = useState(true);
  const [items, setItems] = useState([]);

  const load = async () => {
    setLoading(true);
    try {
      const res = await cardsApi.adminPending();
      setItems(res.data?.data || []);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to load requests');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const approve = async (id) => {
    try {
      await cardsApi.adminApprove(id);
      toast.success('Approved');
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Approval failed');
    }
  };

  const reject = async (id) => {
    try {
      await cardsApi.adminReject(id);
      toast.success('Rejected');
      load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Reject failed');
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Debit Card Requests</h2>
          <p>Approve or reject debit card requests</p>
        </div>
        <button className="btn btn-outline" onClick={load} disabled={loading}>Refresh</button>
      </div>

      <div className="card">
        <div className="card-header"><h3>Pending</h3></div>
        {loading ? (
          <div className="loading">Loading...</div>
        ) : items.length === 0 ? (
          <div className="empty">No pending requests</div>
        ) : (
          <div className="table-responsive">
            <table className="table admin-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Account</th>
                  <th>Username</th>
                  <th>Email</th>
                  <th>Created</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {items.map((r) => (
                  <tr key={r.id}>
                    <td>{r.id}</td>
                    <td className="font-mono">{r.accountNo}</td>
                    <td>{r.username}</td>
                    <td>{r.email}</td>
                    <td>{r.createdAt ? new Date(r.createdAt).toLocaleString() : '-'}</td>
                    <td style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                      <button className="btn btn-primary btn-sm" onClick={() => approve(r.id)}>Approve</button>
                      <button className="btn btn-outline btn-sm" onClick={() => reject(r.id)}>Reject</button>
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

export default AdminDebitCards;

