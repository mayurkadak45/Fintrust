import { useState, useEffect } from 'react';
import useAccountStore from '../store/accountStore';
import { MdDelete, MdAdd } from 'react-icons/md';

function Beneficiaries() {
  const { beneficiaries, fetchBeneficiaries, addBeneficiary, removeBeneficiary, loading } = useAccountStore();
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ accountNo: '', beneficiaryName: '', ifsc: '' });

  useEffect(() => {
    fetchBeneficiaries();
  }, [fetchBeneficiaries]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const result = await addBeneficiary(form.accountNo, form.beneficiaryName, form.ifsc);
    if (result) {
      setForm({ accountNo: '', beneficiaryName: '', ifsc: '' });
      setShowForm(false);
      fetchBeneficiaries();
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Remove this beneficiary?')) {
      const result = await removeBeneficiary(id);
      if (result) fetchBeneficiaries();
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <h2>Beneficiaries</h2>
        <button className="btn btn-primary" onClick={() => setShowForm(!showForm)}>
          <MdAdd /> {showForm ? 'Cancel' : 'Add Beneficiary'}
        </button>
      </div>

      {showForm && (
        <div className="card" style={{ marginBottom: 20 }}>
          <div className="card-header"><h3>Add New Beneficiary</h3></div>
          <form onSubmit={handleSubmit} className="form-body">
            <div className="form-row">
              <div className="form-group">
                <label>Beneficiary Name</label>
                <input
                  type="text"
                  value={form.beneficiaryName}
                  onChange={(e) => setForm({ ...form, beneficiaryName: e.target.value })}
                  placeholder="Full name"
                  required
                />
              </div>
              <div className="form-group">
                <label>Account Number</label>
                <input
                  type="text"
                  value={form.accountNo}
                  onChange={(e) => setForm({ ...form, accountNo: e.target.value })}
                  placeholder="Account number"
                  required
                />
              </div>
              <div className="form-group">
                <label>IFSC Code</label>
                <input
                  type="text"
                  value={form.ifsc}
                  onChange={(e) => setForm({ ...form, ifsc: e.target.value })}
                  placeholder="IFSC code (optional)"
                />
              </div>
            </div>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Adding...' : 'Add Beneficiary'}
            </button>
          </form>
        </div>
      )}

      <div className="card">
        <div className="card-header"><h3>Your Beneficiaries</h3></div>
        {loading && beneficiaries.length === 0 ? (
          <div className="loading">Loading...</div>
        ) : beneficiaries.length === 0 ? (
          <div className="empty">No beneficiaries added yet</div>
        ) : (
          <div className="table-responsive">
            <table className="table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Account No</th>
                  <th>IFSC</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {beneficiaries.map((b) => (
                  <tr key={b.id}>
                    <td className="font-bold">{b.beneficiaryName}</td>
                    <td className="font-mono">{b.accountNo}</td>
                    <td>{b.ifsc || '-'}</td>
                    <td>
                      <span className={`badge badge-${b.status === 'ACTIVE' ? 'success' : 'danger'}`}>
                        {b.status}
                      </span>
                    </td>
                    <td>
                      <button className="btn btn-sm btn-danger" onClick={() => handleDelete(b.id)}>
                        <MdDelete /> Remove
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

export default Beneficiaries;
