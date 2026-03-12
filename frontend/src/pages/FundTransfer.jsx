import { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import useAccountStore from '../store/accountStore';

function FundTransfer() {
  const { accounts, beneficiaries, fetchAccounts, fetchBeneficiaries, executeTransfer, loading } = useAccountStore();
  const [form, setForm] = useState({
    srcAccount: '',
    destAccount: '',
    amount: '',
    remarks: '',
  });
  const [success, setSuccess] = useState(false);
  const [mode, setMode] = useState('BENEFICIARY'); // BENEFICIARY or OTHER
  const [selectedBeneficiaryId, setSelectedBeneficiaryId] = useState(null);

  useEffect(() => {
    fetchAccounts();
    fetchBeneficiaries();
  }, [fetchAccounts, fetchBeneficiaries]);

  const resetForm = () => {
    setForm({
      srcAccount: '',
      destAccount: '',
      amount: '',
      remarks: '',
    });
    setSelectedBeneficiaryId(null);
    setSuccess(false);
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setSuccess(false);
  };

  const handleSelectBeneficiary = (beneficiary) => {
    setMode('BENEFICIARY');
    setSelectedBeneficiaryId(beneficiary.id);
    setForm((prev) => ({
      ...prev,
      destAccount: beneficiary.accountNo,
    }));
    setSuccess(false);
  };

  const handleModeChange = (newMode) => {
    setMode(newMode);
    setSelectedBeneficiaryId(null);
    setForm((prev) => ({
      ...prev,
      destAccount: '',
      amount: '',
      remarks: '',
    }));
    setSuccess(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (form.srcAccount === form.destAccount) {
      return;
    }
    const result = await executeTransfer(
      form.srcAccount,
      form.destAccount,
      parseFloat(form.amount),
      form.remarks
    );
    if (result) {
      setSuccess(true);
      toast.success('Transfer completed successfully!');
      resetForm();
      fetchAccounts(); // refresh balances
    }
  };

  const selectedBeneficiary =
    selectedBeneficiaryId != null
      ? beneficiaries.find((b) => b.id === selectedBeneficiaryId)
      : null;

  const hasBeneficiaries = beneficiaries && beneficiaries.length > 0;

  return (
    <div className="page">
      <div className="page-header">
        <h2>Fund Transfer</h2>
        <p>Transfer money between your accounts and saved beneficiaries</p>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-header">
            <h3>Transfer Details</h3>
          </div>
          <form onSubmit={handleSubmit} className="form-body">
            <div className="form-group">
              <label>From Account</label>
              <select
                name="srcAccount"
                className="select"
                value={form.srcAccount}
                onChange={handleChange}
                required
              >
                <option value="">-- Select Source Account --</option>
                {accounts.map((acc) => (
                  <option key={acc.accountNo} value={acc.accountNo}>
                    {acc.accountNo} ({acc.accountType}) - ${parseFloat(acc.balance).toFixed(2)}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Transfer To</label>
              <div className="button-group" style={{ marginBottom: 8 }}>
                <button
                  type="button"
                  className={`btn btn-sm ${mode === 'BENEFICIARY' ? 'btn-primary' : 'btn-outline'}`}
                  onClick={() => handleModeChange('BENEFICIARY')}
                  disabled={!hasBeneficiaries}
                >
                  Saved Beneficiary
                </button>
                <button
                  type="button"
                  className={`btn btn-sm ${mode === 'OTHER' ? 'btn-primary' : 'btn-outline'}`}
                  onClick={() => handleModeChange('OTHER')}
                  style={{ marginLeft: 8 }}
                >
                  Other Account
                </button>
              </div>

              {mode === 'BENEFICIARY' && (
                <>
                  {!hasBeneficiaries && (
                    <div className="alert alert-info">
                      You have no beneficiaries yet. Add one from the Beneficiaries page.
                    </div>
                  )}
                  {hasBeneficiaries && (
                    <div className="beneficiary-list">
                      {beneficiaries.map((b) => (
                        <div
                          key={b.id}
                          className={`beneficiary-item ${selectedBeneficiaryId === b.id ? 'active' : ''}`}
                          style={{
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'space-between',
                            padding: '8px 12px',
                            borderRadius: 6,
                            border: '1px solid #e5e7eb',
                            marginBottom: 6,
                          }}
                        >
                          <div>
                            <div className="font-bold">{b.beneficiaryName}</div>
                            <div className="font-mono">{b.accountNo}</div>
                          </div>
                          <button
                            type="button"
                            className="btn btn-sm btn-primary"
                            onClick={() => handleSelectBeneficiary(b)}
                          >
                            Send Money
                          </button>
                        </div>
                      ))}
                    </div>
                  )}

                  {selectedBeneficiary && (
                    <div className="selected-summary" style={{ marginTop: 8 }}>
                      <small>
                        Sending to <strong>{selectedBeneficiary.beneficiaryName}</strong> (
                        <span className="font-mono">{selectedBeneficiary.accountNo}</span>)
                      </small>
                    </div>
                  )}
                </>
              )}

              {mode === 'OTHER' && (
                <div className="form-group" style={{ marginTop: 8 }}>
                  <label>Destination Account Number</label>
                  <input
                    type="text"
                    name="destAccount"
                    value={form.destAccount}
                    onChange={handleChange}
                    placeholder="Enter destination account number"
                    required
                  />
                </div>
              )}
            </div>

            <div className="form-group">
              <label>Amount ($)</label>
              <input
                type="number"
                name="amount"
                value={form.amount}
                onChange={handleChange}
                placeholder="Enter amount"
                min="1"
                step="0.01"
                required
              />
            </div>

            <div className="form-group">
              <label>Remarks (Optional)</label>
              <input
                type="text"
                name="remarks"
                value={form.remarks}
                onChange={handleChange}
                placeholder="e.g., Rent payment"
              />
            </div>

            <button
              type="submit"
              className="btn btn-primary btn-block"
              disabled={
                loading ||
                !form.srcAccount ||
                !form.amount ||
                (mode === 'OTHER' && !form.destAccount) ||
                (mode === 'BENEFICIARY' && !form.destAccount)
              }
            >
              {loading ? 'Processing...' : 'Transfer Now'}
            </button>

            {success && (
              <div className="alert alert-success" style={{ marginTop: 12 }}>
                Transfer completed successfully!
              </div>
            )}
          </form>
        </div>

        <div className="card">
          <div className="card-header">
            <h3>Your Accounts</h3>
          </div>
          <div className="account-list">
            {accounts.map((acc) => (
              <div key={acc.accountNo} className="account-item">
                <div>
                  <span className="font-mono">{acc.accountNo}</span>
                  <span
                    className={`badge badge-${acc.accountType === 'SAVINGS' ? 'info' : 'warning'}`}
                    style={{ marginLeft: 8 }}
                  >
                    {acc.accountType}
                  </span>
                </div>
                <span className="font-bold">
                  ${parseFloat(acc.balance).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

export default FundTransfer;
