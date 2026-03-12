import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import useAuthStore from '../../store/authStore';
import {
  MdDashboard,
  MdPendingActions,
  MdPeople,
  MdSwapHoriz,
  MdSearch,
  MdCreditCard,
  MdAtm,
  MdLogout,
} from 'react-icons/md';

const adminNav = [
  { to: '/admin/dashboard', icon: <MdDashboard />, label: 'Dashboard' },
  { to: '/admin/pending-accounts', icon: <MdPendingActions />, label: 'Pending Accounts' },
  { to: '/admin/all-accounts', icon: <MdPeople />, label: 'Customers' },
  { to: '/admin/transactions', icon: <MdSwapHoriz />, label: 'Transactions' },
  { to: '/admin/search-account', icon: <MdSearch />, label: 'Search Account' },
  { to: '/admin/debit-cards', icon: <MdCreditCard />, label: 'Debit Card Requests' },
  { to: '/admin/atm-cards', icon: <MdAtm />, label: 'ATM Cards' },
];

function AdminLayout() {
  const { logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div className="layout">
      <aside className="sidebar admin-sidebar">
        <div className="sidebar-brand">
          <h2>FinTrust Admin</h2>
        </div>
        <nav className="sidebar-nav">
          {adminNav.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                `sidebar-link ${isActive ? 'active' : ''}`
              }
            >
              <span className="sidebar-icon">{item.icon}</span>
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-footer">
          <button className="sidebar-link" onClick={handleLogout} style={{ border: 'none', background: 'none', cursor: 'pointer', width: '100%', textAlign: 'left' }}>
            <span className="sidebar-icon"><MdLogout /></span>
            Logout
          </button>
        </div>
      </aside>
      <div className="layout-main">
        <main className="layout-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default AdminLayout;
