import { useEffect } from 'react';
import useAdminStore from '../../store/adminStore';
import { MdPeople, MdPendingActions, MdSwapHoriz, MdAccountBalance } from 'react-icons/md';

function AdminDashboard() {
  const { stats, fetchStats, loading } = useAdminStore();

  useEffect(() => {
    fetchStats();
  }, [fetchStats]);

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h2>Admin Dashboard</h2>
          <p>Overview of banking system activity</p>
        </div>
      </div>

      <div className="stats-grid">
        <StatCard
          label="Total Customers"
          value={stats?.totalCustomers?.toLocaleString() || '0'}
          icon={<MdPeople />}
          color="primary"
        />
        <StatCard
          label="Pending Accounts"
          value={stats?.pendingAccounts?.toString() || '0'}
          icon={<MdPendingActions />}
          color="warning"
        />
        <StatCard
          label="Total Transactions"
          value={stats?.totalTransactions?.toLocaleString() || '0'}
          icon={<MdSwapHoriz />}
          color="info"
        />
        <StatCard
          label="Bank Balance"
          value={`₹${(stats?.bankBalance || 0).toLocaleString('en-IN')} Cr`}
          icon={<MdAccountBalance />}
          color="success"
        />
      </div>

      <div className="card">
        <div className="card-header">
          <h3>Recent Activities</h3>
        </div>
        <div className="activity-list">
          <ActivityItem
            title="New Account Created"
            desc="Rahul Sharma opened a savings account"
            time="2 min ago"
          />
          <ActivityItem
            title="Large Transaction"
            desc="₹50,000 transferred from account 501034429881"
            time="10 min ago"
          />
          <ActivityItem
            title="Account Blocked"
            desc="Suspicious activity detected"
            time="1 hour ago"
          />
          <ActivityItem
            title="Deposit"
            desc="₹10,000 deposited"
            time="2 hours ago"
          />
        </div>
      </div>
    </div>
  );
}

function StatCard({ label, value, icon, color }) {
  return (
    <div className={`stat-card stat-${color}`}>
      <div className="stat-details">
        <span className="stat-label">{label}</span>
        <span className="stat-value">{value}</span>
      </div>
      <div className={`stat-icon stat-icon-${color}`}>{icon}</div>
    </div>
  );
}

function ActivityItem({ title, desc, time }) {
  return (
    <div className="activity-item">
      <div>
        <strong>{title}</strong>
        <p>{desc}</p>
      </div>
      <span className="activity-time">{time}</span>
    </div>
  );
}

export default AdminDashboard;
