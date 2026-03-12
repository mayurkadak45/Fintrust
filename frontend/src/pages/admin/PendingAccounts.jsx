import { useEffect, useState } from 'react';
import useAdminStore from '../../store/adminStore';
import toast from 'react-hot-toast';

function PendingAccounts() {
  const {
    pendingApplications,
    fetchPending,
    approveApplication,
    rejectApplication,
    loading,
    lastApprovedCredentials,
    clearLastApprovedCredentials,
  } = useAdminStore();
  const [approvingId, setApprovingId] = useState(null);

  useEffect(() => {
    fetchPending();
  }, [fetchPending]);

  const handleApprove = async (app) => {
    setApprovingId(app.id);
    const creds = await approveApplication(app.id);
    setApprovingId(null);
  };

  const copyCredentials = () => {
    if (!lastApprovedCredentials) return;
    const text = `Username: ${lastApprovedCredentials.username}\nTemporary Password: ${lastApprovedCredentials.temporaryPassword}\nAccount No: ${lastApprovedCredentials.accountNo}`;
    navigator.clipboard.writeText(text).then(() => toast.success('Copied to clipboard'));
  };

  return (
    <div className="page">
      <div className="page-header">
        <h2>Pending Account Requests</h2>
      </div>

      {/* Modal: show login credentials after approval */}
      {lastApprovedCredentials && (
        <div className="modal-overlay" onClick={clearLastApprovedCredentials}>
          <div className="modal-card credentials-modal" onClick={(e) => e.stopPropagation()}>
            <h3>Account approved – share credentials with customer</h3>
            <p className="text-muted" style={{ marginBottom: 16 }}>
              An approval email with these details has been sent to the customer&apos;s registered email (if mail is configured).
            </p>
            <div className="credentials-box">
              <div className="cred-row">
                <span className="cred-label">Username</span>
                <span className="cred-value font-mono">{lastApprovedCredentials.username}</span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Temporary password</span>
                <span className="cred-value font-mono">{lastApprovedCredentials.temporaryPassword}</span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Account number</span>
                <span className="cred-value font-mono">{lastApprovedCredentials.accountNo}</span>
              </div>
            </div>
            <div style={{ display: 'flex', gap: 8, marginTop: 16 }}>
              <button type="button" className="btn btn-primary" onClick={copyCredentials}>
                Copy credentials
              </button>
              <button type="button" className="btn btn-outline" onClick={clearLastApprovedCredentials}>
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      <div className="card">
        {loading && pendingApplications.length === 0 ? (
          <div className="loading">Loading...</div>
        ) : pendingApplications.length === 0 ? (
          <div className="empty">No pending applications</div>
        ) : (
          <div className="table-responsive">
            <table className="table admin-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Mobile</th>
                  <th>Account Type</th>
                  <th>Created</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {pendingApplications.map((app) => (
                  <tr key={app.id}>
                    <td className="font-bold">{app.fullName}</td>
                    <td>{app.email}</td>
                    <td>{app.mobile}</td>
                    <td>{app.accountType}</td>
                    <td>{app.createdAt ? new Date(app.createdAt).toLocaleDateString() : '-'}</td>
                    <td>
                      <div style={{ display: 'flex', gap: 8 }}>
                        <button
                          className="btn btn-sm btn-primary"
                          onClick={() => handleApprove(app)}
                          disabled={loading || approvingId === app.id}
                        >
                          {approvingId === app.id ? 'Approving...' : 'Approve'}
                        </button>
                        <button
                          className="btn btn-sm btn-danger"
                          onClick={() => rejectApplication(app.id)}
                          disabled={loading}
                        >
                          Reject
                        </button>
                      </div>
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

export default PendingAccounts;
