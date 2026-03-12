import { NavLink } from 'react-router-dom';
import {
  MdDashboard,
  MdHistory,
  MdSwapHoriz,
  MdPeople,
  MdReceipt,
  MdAtm,
  MdLock,
  MdCreditCard,
  MdSavings,
} from 'react-icons/md';

const navItems = [
  { to: '/dashboard', icon: <MdDashboard />, label: 'Dashboard' },
  { to: '/transactions', icon: <MdHistory />, label: 'Transactions' },
  { to: '/transfer', icon: <MdSwapHoriz />, label: 'Fund Transfer' },
  { to: '/beneficiaries', icon: <MdPeople />, label: 'Beneficiaries' },
  { to: '/mini-statement', icon: <MdReceipt />, label: 'Mini Statement' },
  { to: '/debit-card', icon: <MdCreditCard />, label: 'Debit Card' },
  { to: '/fd', icon: <MdSavings />, label: 'Fixed Deposit' },
  { to: '/change-password', icon: <MdLock />, label: 'Change Password' },
];

function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <span className="sidebar-logo">🏦</span>
        <h2>FinTrust Bank</h2>
      </div>
      <nav className="sidebar-nav">
        {navItems.map((item) => (
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
        <a href="/atm" className="sidebar-link" target="_blank" rel="noopener noreferrer">
          <span className="sidebar-icon"><MdAtm /></span>
          Virtual ATM
        </a>
      </nav>
    </aside>
  );
}

export default Sidebar;
