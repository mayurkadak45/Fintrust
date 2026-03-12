import { useState } from 'react';
import useAdminStore from '../../store/adminStore';
import { MdSearch } from 'react-icons/md';

function SearchAccount() {
  const { searchResults, searchCustomers, loading } = useAdminStore();
  const [query, setQuery] = useState('');

  const handleSearch = (e) => {
    e.preventDefault();
    if (query.trim()) searchCustomers(query);
  };

  return (
    <div className="search-account-page">
      <div className="search-center-card">
        <h3>Search Bank Account</h3>
        <form onSubmit={handleSearch} className="search-form-row">
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Enter Account Number"
            className="search-account-input"
          />
          <button type="submit" className="btn btn-primary btn-icon" disabled={loading}>
            <MdSearch size={18} />
          </button>
        </form>
      </div>

      {searchResults.length > 0 && (
        <div className="card" style={{ marginTop: 24 }}>
          <div className="table-responsive">
            <table className="table admin-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Account No</th>
                  <th>Email</th>
                  <th>Type</th>
                  <th>Balance</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {searchResults.map((c) => (
                  <tr key={c.accountNo}>
                    <td className="font-bold">{c.name}</td>
                    <td className="font-mono">{c.accountNo}</td>
                    <td>{c.email}</td>
                    <td>{c.accountType}</td>
                    <td>₹{parseFloat(c.balance).toLocaleString('en-IN')}</td>
                    <td>
                      <span className={`badge badge-${c.status === 'ACTIVE' ? 'success' : 'danger'}`}>
                        {c.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}

export default SearchAccount;
