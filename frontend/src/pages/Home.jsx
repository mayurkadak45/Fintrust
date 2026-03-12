import { Link, useNavigate } from 'react-router-dom';

const FD_PLANS = [
  { years: 2, rate: 5.0, title: '2-Year FD', desc: 'Short tenure with stable returns. Ideal for near-term goals.' },
  { years: 3, rate: 5.5, title: '3-Year FD', desc: 'Balanced option for mid-term planning with better interest.' },
  { years: 5, rate: 6.5, title: '5-Year FD', desc: 'Higher interest for long-term disciplined savings.' },
  { years: 10, rate: 7.5, title: '10-Year FD', desc: 'Maximum growth plan for long-term wealth building.' },
];

function Home() {
  const navigate = useNavigate();

  const scrollTo = (id) => {
    const el = document.getElementById(id);
    if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' });
  };

  return (
    <div className="home">
      <header className="home-nav">
        <div className="home-nav-inner">
          <div className="home-brand" onClick={() => navigate('/')}>
            <span className="home-brand-mark">Fin</span>Trust
          </div>
          <nav className="home-links">
            <button className="home-link" onClick={() => scrollTo('features')}>Features</button>
            <button className="home-link" onClick={() => scrollTo('services')}>Services</button>
            <button className="home-link" onClick={() => scrollTo('security')}>Security</button>
            <button className="home-link" onClick={() => scrollTo('fd')}>FD Service</button>
          </nav>
          <div className="home-actions">
            <Link className="btn btn-outline btn-sm" to="/login">Login</Link>
            <Link className="btn btn-primary btn-sm" to="/register">Open Account</Link>
          </div>
        </div>
      </header>

      <main>
        <section className="home-hero">
          <div className="home-hero-inner">
            <div className="home-hero-copy">
              <h1>
                Smart Digital Banking <br />
                <span className="text-primary">For Your Future</span>
              </h1>
              <p>
                Manage accounts, transfer funds, track transactions, request a debit card, and create Fixed Deposits —
                all in one secure FinTrust experience.
              </p>
              <div className="home-hero-cta">
                <Link className="btn btn-primary" to="/register">Open Account</Link>
                <Link className="btn btn-outline" to="/login">Login</Link>
                <Link className="btn btn-outline" to="/atm" target="_blank" rel="noopener noreferrer">Virtual ATM</Link>
              </div>
              <div className="home-hero-badges">
                <span className="home-badge">Fast approvals</span>
                <span className="home-badge">Secure JWT sessions</span>
                <span className="home-badge">FD plans up to 10 years</span>
              </div>
            </div>
            <div className="home-hero-card">
              <div className="credit-card-visual" style={{ margin: 0 }}>
                <div className="card-chip" />
                <div className="card-number-display">4242 •••• ••••</div>
                <div className="card-bottom">
                  <div>
                    <small>CARD HOLDER</small>
                    <p>FinTrust Customer</p>
                  </div>
                  <div className="card-brand">
                    <strong style={{ letterSpacing: 1 }}>FINTRUST</strong>
                  </div>
                </div>
              </div>
              <div className="home-hero-card-note">
                Apply for a debit card in your dashboard, get admin approval, and use the card number on Virtual ATM.
              </div>
            </div>
          </div>
        </section>

        <section id="features" className="home-section">
          <div className="home-section-inner">
            <h2>Features built for everyday banking</h2>
            <div className="home-grid">
              <div className="home-tile">
                <strong>Account Management</strong>
                <p>View account summary, status, and balances with a clean dashboard.</p>
              </div>
              <div className="home-tile">
                <strong>Fund Transfers</strong>
                <p>Transfer money, track transaction history, and view mini statements.</p>
              </div>
              <div className="home-tile">
                <strong>Virtual ATM</strong>
                <p>Validate card, PIN, withdraw/deposit, statement and receipt generation.</p>
              </div>
              <div className="home-tile">
                <strong>Debit Card Requests</strong>
                <p>Request a card from your dashboard. Admin approval sends card details via email.</p>
              </div>
            </div>
          </div>
        </section>

        <section id="services" className="home-section home-section-alt">
          <div className="home-section-inner">
            <h2>Services</h2>
            <div className="home-grid">
              <div className="home-tile">
                <strong>Online Banking</strong>
                <p>Login, change password, and manage beneficiaries securely.</p>
              </div>
              <div className="home-tile">
                <strong>Fixed Deposits</strong>
                <p>Create FD accounts with multiple tenures and track maturity value.</p>
              </div>
              <div className="home-tile">
                <strong>Admin Console</strong>
                <p>Approve accounts, view customers/transactions, manage ATM cards and card requests.</p>
              </div>
              <div className="home-tile">
                <strong>KYC Uploads</strong>
                <p>Upload PAN/Aadhaar/Passport documents safely (stored in Cloudinary).</p>
              </div>
            </div>
          </div>
        </section>

        <section id="security" className="home-section">
          <div className="home-section-inner">
            <h2>Security</h2>
            <div className="home-grid">
              <div className="home-tile">
                <strong>Account protection</strong>
                <p>Login attempts are tracked and accounts can be locked on repeated failures.</p>
              </div>
              <div className="home-tile">
                <strong>ATM PIN safety</strong>
                <p>ATM PIN is hashed in the database. After 3 incorrect attempts the card is blocked.</p>
              </div>
              <div className="home-tile">
                <strong>Admin controls</strong>
                <p>Admins can block/unblock cards and reset attempts from the dashboard.</p>
              </div>
              <div className="home-tile">
                <strong>Secure sessions</strong>
                <p>JWT-based authentication with role headers passed through the gateway.</p>
              </div>
            </div>
          </div>
        </section>

        <section id="fd" className="home-section home-section-alt">
          <div className="home-section-inner">
            <h2>FD Service plans</h2>
            <p className="home-subtitle">
              Choose a tenure that matches your goal. Higher tenure generally gives higher interest.
            </p>

            <div className="home-grid">
              {FD_PLANS.map((p) => (
                <div key={p.years} className="home-tile home-plan">
                  <div className="home-plan-top">
                    <strong>{p.title}</strong>
                    <span className="home-rate">{p.rate}% p.a.</span>
                  </div>
                  <p>{p.desc}</p>
                  <div className="home-plan-actions">
                    <Link className="btn btn-primary btn-sm" to="/login">Create FD</Link>
                    <button className="btn btn-outline btn-sm" onClick={() => scrollTo('security')}>Learn Security</button>
                  </div>
                </div>
              ))}
            </div>

            <div className="home-footer-cta">
              <div>
                <strong>Ready to start?</strong>
                <p className="text-muted">Open an account and unlock FD + Debit Card + Virtual ATM.</p>
              </div>
              <Link className="btn btn-primary" to="/register">Open Account</Link>
            </div>
          </div>
        </section>
      </main>

      <footer className="home-footer">
        <div className="home-footer-inner">
          <div>
            <strong>FinTrust Bank</strong>
            <p className="text-muted">Secure digital banking solutions for modern customers.</p>
          </div>
          <div className="home-footer-cols">
            <div>
              <strong>Banking</strong>
              <div className="home-footer-links">
                <Link to="/login">Login</Link>
                <Link to="/register">Open Account</Link>
                <a href="/atm" target="_blank" rel="noopener noreferrer">Virtual ATM</a>
              </div>
            </div>
            <div>
              <strong>Support</strong>
              <div className="home-footer-links">
                <a href="mailto:support@fintrust.com">support@fintrust.com</a>
                <span className="text-muted">+91 9876543210</span>
              </div>
            </div>
          </div>
        </div>
        <div className="home-footer-bottom">© 2026 FinTrust Bank. All rights reserved.</div>
      </footer>
    </div>
  );
}

export default Home;

