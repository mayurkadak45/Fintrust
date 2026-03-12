import { useNavigate } from 'react-router-dom';
import { MdCreditCard, MdSecurity, MdAccountBalance } from 'react-icons/md';

function VirtualAtm() {
  const navigate = useNavigate();

  return (
    <div className="atm-welcome">
      <div className="atm-welcome-card">
        <div className="atm-welcome-header">
          <MdAccountBalance size={40} />
          <h1>FinTrust <span className="text-primary">ATM</span></h1>
          <p>Virtual ATM Simulation</p>
        </div>

        <div className="atm-promo-banner">
          <h3>🎉 Welcome to FinTrust ATM</h3>
          <p>Access your account securely. Withdraw, deposit, and check your balance with ease.</p>
        </div>

        <button className="btn btn-primary btn-lg atm-proceed-btn" onClick={() => navigate('/card')}>
          <MdCreditCard /> Proceed with Card
        </button>

        <div className="atm-security-tip">
          <MdSecurity />
          <span>Your session is encrypted and secure. Never share your PIN with anyone.</span>
        </div>

        <div className="atm-welcome-footer">
          <button className="link" onClick={() => navigate('/login')}>Go to Online Banking</button>
        </div>
      </div>
    </div>
  );
}

export default VirtualAtm;
