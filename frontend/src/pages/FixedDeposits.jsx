import { useEffect, useMemo, useState } from 'react';
import toast from 'react-hot-toast';
import { fdApi } from '../api/fd';

const PLANS = [
  { years: 2, rate: 5.0, title: '2 Years', desc: 'Stable returns with short tenure.' },
  { years: 3, rate: 5.5, title: '3 Years', desc: 'Balanced growth for mid-term goals.' },
  { years: 5, rate: 6.5, title: '5 Years', desc: 'Higher interest for long-term savings.' },
  { years: 10, rate: 7.5, title: '10 Years', desc: 'Maximum growth with long tenure.' },
];

function FixedDeposits() {
  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState(false);
  const [items, setItems] = useState([]);
  const [amount, setAmount] = useState('');
  const [years, setYears] = useState(2);

  const plan = useMemo(() => PLANS.find((p) => p.years === Number(years)), [years]);

  const expected = useMemo(() => {
    const a = Number(amount || 0);
    if (!a || !plan) return 0;
    return a * (1 + (plan.rate / 100) * Number(years));
  }, [amount, years, plan]);

  const load = async () => {
    setLoading(true);
    try {
      const res = await fdApi.list();
      setItems(res.data?.data || []);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to load FDs');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const create = async (e) => {
    e.preventDefault();
    setCreating(true);
    try {
      await fdApi.create({ amount: Number(amount), years: Number(years) });
      toast.success('FD created');
      setAmount('');
      await load();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to create FD');
    } finally {
      setCreating(false);
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Fixed Deposits</h2>
          <p>Create and track your FD accounts</p>
        </div>
        <button className="btn btn-outline" onClick={load} disabled={loading}>Refresh</button>
      </div>

      <div className="grid-2">
        <div className="card">
          <div className="card-header"><h3>Create FD</h3></div>
          <form className="form-body" onSubmit={create}>
            <div className="form-group">
              <label>Amount</label>
              <input
                type="number"
                min={1000}
                step="1"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                placeholder="Minimum 1000"
                required
              />
            </div>
            <div className="form-group">
              <label>Tenure (years)</label>
              <select className="select" value={years} onChange={(e) => setYears(Number(e.target.value))}>
                {PLANS.map((p) => (
                  <option key={p.years} value={p.years}>
                    {p.years} years — {p.rate}% p.a.
                  </option>
                ))}
              </select>
            </div>
            <div className="alert" style={{ background: 'var(--info-light)', color: 'var(--info)', border: '1px solid #bfdbfe' }}>
              <strong>Plan:</strong> {plan?.title} ({plan?.rate}% p.a.)<br />
              <span className="text-muted">{plan?.desc}</span><br />
              <strong>Estimated maturity:</strong> ₹{expected ? expected.toLocaleString('en-IN', { maximumFractionDigits: 2 }) : '0'}
            </div>
            <button className="btn btn-primary btn-block" disabled={creating}>
              {creating ? 'Creating...' : 'Create FD'}
            </button>
          </form>
        </div>

        <div className="card">
          <div className="card-header"><h3>Your FD Accounts</h3></div>
          {loading ? (
            <div className="loading">Loading...</div>
          ) : items.length === 0 ? (
            <div className="empty">No FDs created yet</div>
          ) : (
            <div className="table-responsive">
              <table className="table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Principal</th>
                    <th>Years</th>
                    <th>Rate</th>
                    <th>Maturity</th>
                    <th>Start</th>
                    <th>Matures</th>
                  </tr>
                </thead>
                <tbody>
                  {items.map((fd) => (
                    <tr key={fd.id}>
                      <td>{fd.id}</td>
                      <td>₹{Number(fd.principal || 0).toLocaleString('en-IN')}</td>
                      <td>{fd.years}</td>
                      <td>{Number(fd.interestRate || 0).toFixed(2)}%</td>
                      <td>₹{Number(fd.maturityAmount || 0).toLocaleString('en-IN')}</td>
                      <td>{fd.startDate}</td>
                      <td>{fd.maturityDate}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default FixedDeposits;

