import { useEffect, useState } from 'react';
import { cardsApi } from '../api/cards';
import toast from 'react-hot-toast';

function DebitCard() {
  const [loading, setLoading] = useState(true);
  const [request, setRequest] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      const res = await cardsApi.myRequest();
      setRequest(res.data?.data || null);
    } catch (e) {
      setRequest(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const submit = async () => {
    setSubmitting(true);
    try {
      const res = await cardsApi.request();
      setRequest(res.data?.data || null);
      toast.success('Debit card request submitted');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to submit request');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Debit Card</h2>
          <p>Request and track your debit card approval</p>
        </div>
        <button className="btn btn-outline" onClick={load} disabled={loading}>Refresh</button>
      </div>

      <div className="card" style={{ maxWidth: 720 }}>
        <div className="card-header"><h3>Status</h3></div>
        <div className="form-body">
          {loading ? (
            <div className="loading">Loading...</div>
          ) : !request ? (
            <>
              <p className="text-muted">You don’t have a debit card yet. Submit a request to get a card number.</p>
              <button className="btn btn-primary" onClick={submit} disabled={submitting}>
                {submitting ? 'Submitting...' : 'Request Debit Card'}
              </button>
            </>
          ) : (
            <>
              <div className="cred-row">
                <span className="cred-label">Request ID</span>
                <span className="cred-value">{request.id}</span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Account</span>
                <span className="cred-value">{request.accountNo}</span>
              </div>
              <div className="cred-row">
                <span className="cred-label">Status</span>
                <span className="cred-value">{request.status}</span>
              </div>
              {request.status === 'APPROVED' && (
                <>
                  <div className="cred-row">
                    <span className="cred-label">Card Number</span>
                    <span className="cred-value font-mono">{request.approvedCardNumber || '-'}</span>
                  </div>
                  <div className="cred-row">
                    <span className="cred-label">Expiry</span>
                    <span className="cred-value">{request.approvedExpiryDate || '-'}</span>
                  </div>
                  <div className="alert alert-success">
                    Your card is approved. Use the Virtual ATM with your card number. PIN is sent by email.
                  </div>
                </>
              )}
              {request.status === 'PENDING' && (
                <div className="alert" style={{ background: 'var(--warning-light)', color: 'var(--warning)', border: '1px solid #fde68a' }}>
                  Pending admin approval. You’ll receive an email when approved.
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default DebitCard;

