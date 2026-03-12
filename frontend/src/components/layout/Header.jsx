import { useNavigate } from 'react-router-dom';
import useAuthStore from '../../store/authStore';
import { MdLogout, MdPerson } from 'react-icons/md';

function Header() {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <header className="header">
      <h1 className="header-title">Digital Banking</h1>
      <div className="header-user">
        <MdPerson className="header-icon" />
        <span>{user?.username || 'Guest'}</span>
        <span className="header-role">{user?.role}</span>
        <button className="btn-logout" onClick={handleLogout}>
          <MdLogout /> Logout
        </button>
      </div>
    </header>
  );
}

export default Header;
