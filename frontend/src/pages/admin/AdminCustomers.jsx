import { useEffect, useState } from 'react';
import useAdminStore from '../../store/adminStore';

function AdminCustomers() {
  const { customers, fetchCustomers, loading } = useAdminStore();
  const [search, setSearch] = useState('');
  const [viewCustomer, setViewCustomer] = useState(null);

  useEffect(() => {
    fetchCustomers();
  }, [fetchCustomers]);

  const filtered = customers.filter(
    (c) =>
      c.name.toLowerCase().includes(search.toLowerCase()) ||
      c.accountNo.includes(search) ||
      (c.username && c.username.toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <div className="page">
      <div className="page-header">
        <h2>Customers</h2>
      </div>

      {/* View customer detail modal */}
      {viewCustomer && (
        <div className="modal-overlay" onClick={() => setViewCustomer(null)}>
          <div className="modal-card credentials-modal" onClick={(e) => e.stopPropagation()}>
            <h3>Customer details</h3>
            <div className="credentials-box">
              <div className="cred-row">
                <span className="cred-label">Name</span>
                <span className="cred-value">{viewCustomer.name}</span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Username (login)</span>
                <span className="cred-value font-mono">{viewCustomer.username || '–'}</span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Account number</span>
                <span className="cred-value font-mono">{viewCustomer.accountNo}</span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Email</span>
                <span className="cred-value">{viewCustomer.email || '–'}</span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Mobile</span>
                <span className="cred-value">{viewCustomer.mobile || '–'}</span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Account type</span>
                <span className="cred-value">{viewCustomer.accountType}</span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Balance</span>
                <span className="cred-value">₹{parseFloat(viewCustomer.balance).toLocaleString('en-IN')}</span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Status</span>
                <span className={`badge badge-${viewCustomer.status === 'ACTIVE' ? 'success' : 'warning'}`}>
                  {viewCustomer.status}
                </span>
              </div>
            </div>
            <p className="text-muted" style={{ marginTop: 12, fontSize: '0.85rem' }}>
              Initial password was sent to the customer&apos;s email at approval. They can change it after login.
            </p>
            <button type="button" className="btn btn-primary" style={{ marginTop: 16 }} onClick={() => setViewCustomer(null)}>
              Close
            </button>
          </div>
        </div>
      )}

      <div className="card">
        <div className="card-header">
          <input
            type="text"
            className="search-input"
            placeholder="Search by Account Number, Name or Username"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        {loading && customers.length === 0 ? (
          <div className="loading">Loading...</div>
        ) : filtered.length === 0 ? (
          <div className="empty">No customers found</div>
        ) : (
          <div className="table-responsive">
            <table className="table admin-table">
              <thead>
                <tr>
                  <th>Customer</th>
                  <th>Account Number</th>
                  <th>Mobile</th>
                  <th>Account Type</th>
                  <th>Balance</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((c) => (
                  <tr key={c.accountNo}>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                        <div className="avatar">{c.name.charAt(0)}</div>
                        <span className="font-bold">{c.name}</span>
                      </div>
                    </td>
                    <td className="font-mono">{c.accountNo}</td>
                    <td>{c.mobile || '-'}</td>
                    <td>{c.accountType}</td>
                    <td className="font-bold">₹{parseFloat(c.balance).toLocaleString('en-IN')}</td>
                    <td>
                      <span className={`badge badge-${c.status === 'ACTIVE' ? 'success' : c.status === 'BLOCKED' ? 'danger' : 'warning'}`}>
                        {c.status}
                      </span>
                    </td>
                    <td>
                      <button className="btn btn-sm btn-primary" onClick={() => setViewCustomer(c)}>
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
    </div>
  );
}

export default AdminCustomers;
