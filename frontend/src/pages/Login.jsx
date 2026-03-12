import { useState, useCallback } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import useAuthStore from '../store/authStore';
import toast from 'react-hot-toast';

function generateCaptcha() {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789';
  let result = '';
  for (let i = 0; i < 6; i++) result += chars.charAt(Math.floor(Math.random() * chars.length));
  return result;
}

function Login() {
  const [role, setRole] = useState('ADMIN');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [captcha, setCaptcha] = useState(generateCaptcha());
  const [captchaInput, setCaptchaInput] = useState('');
  const { login, loading } = useAuthStore();
  const navigate = useNavigate();

  const refreshCaptcha = useCallback(() => {
    setCaptcha(generateCaptcha());
    setCaptchaInput('');
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (captchaInput !== captcha) {
      toast.error('Invalid captcha');
      refreshCaptcha();
      return;
    }
    const success = await login(username, password);
    if (success) {
      const user = JSON.parse(localStorage.getItem('user'));
      if (user?.role === 'ADMIN') {
        navigate('/admin/dashboard');
      } else {
        navigate('/dashboard');
      }
    } else {
      refreshCaptcha();
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-header">
          <h1><span style={{ color: '#1a56db' }}>Fin</span>Trust</h1>
          <p>Secure Admin & Customer Login</p>
        </div>

        <div className="role-toggle">
          <button
            type="button"
            className={`role-btn ${role === 'ADMIN' ? 'active' : ''}`}
            onClick={() => setRole('ADMIN')}
          >
            Admin
          </button>
          <button
            type="button"
            className={`role-btn ${role === 'CUSTOMER' ? 'active' : ''}`}
            onClick={() => setRole('CUSTOMER')}
          >
            Customer
          </button>
        </div>

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Enter username"
              required
              autoFocus
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter password"
              required
            />
          </div>

          <div className="form-group">
            <label>Captcha</label>
            <div className="captcha-row">
              <span className="captcha-display">{captcha}</span>
              <button type="button" className="captcha-refresh" onClick={refreshCaptcha}>&#x21bb;</button>
            </div>
            <input
              type="text"
              value={captchaInput}
              onChange={(e) => setCaptchaInput(e.target.value)}
              placeholder="Enter captcha"
              required
            />
          </div>

          <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
            {loading ? 'Signing in...' : 'Login'}
          </button>
        </form>

        <div className="login-footer">
          <Link to="/register" className="link">Open New Account</Link>
          <div className="login-demo">
            <small>Admin: <strong>admin</strong> / <strong>admin123</strong></small><br />
            <small>Customer: <strong>john.doe</strong> / <strong>password123</strong></small>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;
