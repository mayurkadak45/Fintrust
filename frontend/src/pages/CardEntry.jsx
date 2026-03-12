import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useAtmStore from '../store/atmStore';
import { MdCreditCard, MdArrowBack, MdLockReset, MdDialpad } from 'react-icons/md';

function CardEntry() {
  const {
    step, cardNumber, accountNo, balance, miniStatement, loading, pinAttemptsRemaining, cardBlocked,
    validateCard, validatePin, changePin, goToPin, goToChangePin, goToOption,
    checkBalance, withdraw, deposit, fetchMiniStatement, endSession,
    lastTxn,
  } = useAtmStore();

  const navigate = useNavigate();
  const [cardInput, setCardInput] = useState('');
  const [pinInput, setPinInput] = useState('');
  const [amount, setAmount] = useState('');
  const [activeOp, setActiveOp] = useState(null);
  const [pinForm, setPinForm] = useState({
    username: '',
    password: '',
    accountNo: '',
    newPin: '',
    confirmPin: '',
  });

  const formatCardDisplay = (val) => {
    const digits = val.replace(/\D/g, '');
    return digits.replace(/(.{4})/g, '$1 ').trim();
  };

  const handleCardChange = (e) => {
    const digits = e.target.value.replace(/\D/g, '').slice(0, 12);
    setCardInput(digits);
  };

  const handleCardSubmit = async (e) => {
    e.preventDefault();
    await validateCard(cardInput);
  };

  const handlePinSubmit = async (e) => {
    e.preventDefault();
    const ok = await validatePin(pinInput);
    if (!ok) setPinInput('');
  };

  const handleChangePinSubmit = async (e) => {
    e.preventDefault();
    if (pinForm.newPin !== pinForm.confirmPin) return;
    await changePin({
      accountNo: pinForm.accountNo,
      username: pinForm.username,
      password: pinForm.password,
      newPin: pinForm.newPin,
    });
    setPinForm({ username: '', password: '', accountNo: '', newPin: '', confirmPin: '' });
  };

  const handleWithdraw = async (e) => {
    e.preventDefault();
    const ok = await withdraw(parseFloat(amount));
    if (ok) {
      setAmount('');
      setActiveOp('receipt');
    }
  };

  const handleDeposit = async (e) => {
    e.preventDefault();
    const ok = await deposit(parseFloat(amount));
    if (ok) {
      setAmount('');
      setActiveOp('receipt');
    }
  };

  const handleEndSession = async () => {
    await endSession();
    setCardInput('');
    setPinInput('');
    setAmount('');
    setActiveOp(null);
    navigate('/atm');
  };

  return (
    <div className="card-entry-page">
      {/* Card Entry Step */}
      {step === 'CARD' && (
        <div className="card-entry-container">
          <button className="btn-back" onClick={() => navigate('/atm')}>
            <MdArrowBack /> Back
          </button>
          <h2>Enter Your Card Number</h2>
          <p className="text-muted">Insert your FinTrust debit card to continue</p>

          {/* Credit Card Visual */}
          <div className="credit-card-visual">
            <div className="card-chip"></div>
            <div className="card-number-display">
              {formatCardDisplay(cardInput) || '•••• •••• ••••'}
            </div>
            <div className="card-bottom">
              <div>
                <small>CARD HOLDER</small>
                <p>FinTrust Customer</p>
              </div>
              <div className="card-brand">
                <MdCreditCard size={28} />
              </div>
            </div>
          </div>

          <form onSubmit={handleCardSubmit} className="card-entry-form">
            <label>Card Number (12 digits)</label>
            <input
              type="text"
              className="input card-input"
              value={cardInput}
              onChange={handleCardChange}
              placeholder="Enter 12-digit card number"
              maxLength={12}
              required
              autoFocus
            />
            <button type="submit" className="btn btn-primary btn-lg" disabled={loading || cardInput.length < 12}>
              {loading ? 'Verifying...' : 'Verify Card'}
            </button>
          </form>
        </div>
      )}

      {/* PIN Entry Step */}
      {step === 'OPTION' && (
        <div className="card-entry-container">
          <h2>Choose an option</h2>
          <p className="text-muted">Card: {cardNumber}</p>
          <div className="atm-menu-grid">
            <button className="atm-menu-btn" onClick={goToPin}>
              <MdDialpad /> Enter PIN
            </button>
            <button className="atm-menu-btn" onClick={goToChangePin}>
              <MdLockReset /> Change PIN
            </button>
          </div>
          <button className="btn btn-outline" onClick={handleEndSession}>Cancel</button>
        </div>
      )}

      {step === 'CHANGE_PIN' && (
        <div className="card-entry-container">
          <h2>Change ATM PIN</h2>
          <p className="text-muted">Verify online banking credentials to change PIN.</p>
          <form onSubmit={handleChangePinSubmit} className="card-entry-form">
            <input
              className="input"
              placeholder="Username"
              value={pinForm.username}
              onChange={(e) => setPinForm((p) => ({ ...p, username: e.target.value }))}
              required
              autoFocus
            />
            <input
              className="input"
              type="password"
              placeholder="Password"
              value={pinForm.password}
              onChange={(e) => setPinForm((p) => ({ ...p, password: e.target.value }))}
              required
            />
            <input
              className="input"
              placeholder="Account Number"
              value={pinForm.accountNo}
              onChange={(e) => setPinForm((p) => ({ ...p, accountNo: e.target.value }))}
              required
            />
            <div className="form-row" style={{ gap: 10 }}>
              <input
                className="input pin-input"
                placeholder="New 4-digit PIN"
                value={pinForm.newPin}
                onChange={(e) => setPinForm((p) => ({ ...p, newPin: e.target.value.replace(/\D/g, '').slice(0, 4) }))}
                maxLength={4}
                required
              />
              <input
                className="input pin-input"
                placeholder="Confirm PIN"
                value={pinForm.confirmPin}
                onChange={(e) => setPinForm((p) => ({ ...p, confirmPin: e.target.value.replace(/\D/g, '').slice(0, 4) }))}
                maxLength={4}
                required
              />
            </div>
            <button type="submit" className="btn btn-primary btn-lg" disabled={loading || pinForm.newPin !== pinForm.confirmPin}>
              {loading ? 'Updating...' : 'Update PIN'}
            </button>
            <button type="button" className="btn btn-outline" onClick={goToOption}>Back</button>
          </form>
        </div>
      )}

      {step === 'PIN' && (
        <div className="card-entry-container">
          <h2>Enter Your PIN</h2>
          <p className="text-muted">Card: {cardNumber}</p>
          {cardBlocked ? (
            <div className="alert alert-danger" style={{ marginTop: 12 }}>
              ATM locked. Card is blocked due to multiple incorrect attempts. Contact admin.
            </div>
          ) : (
            <div className="text-muted" style={{ fontSize: '0.85rem', marginTop: 6 }}>
              Attempts remaining: <strong>{pinAttemptsRemaining}</strong>
            </div>
          )}
          <form onSubmit={handlePinSubmit} className="card-entry-form">
            <input
              type="password"
              className="input pin-input"
              value={pinInput}
              onChange={(e) => setPinInput(e.target.value)}
              placeholder="Enter 4-digit PIN"
              maxLength={4}
              required
              autoFocus
              disabled={cardBlocked}
            />
            <button type="submit" className="btn btn-primary btn-lg" disabled={loading}>
              {loading ? 'Verifying...' : 'Verify PIN'}
            </button>
            <button type="button" className="btn btn-outline" onClick={handleEndSession}>Cancel</button>
          </form>
        </div>
      )}

      {/* ATM Menu */}
      {step === 'MENU' && !activeOp && (
        <div className="card-entry-container">
          <h2>Select Transaction</h2>
          <p className="text-muted">Account: {accountNo}</p>
          <div className="atm-menu-grid">
            <button className="atm-menu-btn" onClick={() => { checkBalance(); setActiveOp('balance'); }}>
              💰 Balance Inquiry
            </button>
            <button className="atm-menu-btn" onClick={() => setActiveOp('withdraw')}>
              💸 Withdraw
            </button>
            <button className="atm-menu-btn" onClick={() => setActiveOp('deposit')}>
              💵 Deposit
            </button>
            <button className="atm-menu-btn" onClick={() => { fetchMiniStatement(); setActiveOp('statement'); }}>
              📋 Mini Statement
            </button>
          </div>
          <button className="btn btn-danger btn-sm" onClick={handleEndSession}>End Session</button>
        </div>
      )}

      {/* Balance */}
      {step === 'MENU' && activeOp === 'balance' && (
        <div className="card-entry-container">
          <h2>Account Balance</h2>
          {loading ? <p>Loading...</p> : balance ? (
            <div className="atm-balance-display">
              <span className="balance-label">Available Balance</span>
              <span className="balance-amount">₹{parseFloat(balance.balance || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</span>
            </div>
          ) : <p>Unable to fetch balance</p>}
          <button className="btn btn-primary" onClick={() => setActiveOp(null)}>Back to Menu</button>
        </div>
      )}

      {/* Withdraw */}
      {step === 'MENU' && activeOp === 'withdraw' && (
        <div className="card-entry-container">
          <h2>Withdraw Cash</h2>
          <form onSubmit={handleWithdraw} className="card-entry-form">
            <input type="number" className="input" value={amount} onChange={(e) => setAmount(e.target.value)}
              placeholder="Enter amount (min 100)" min="100" step="100" required autoFocus />
            <div className="quick-amounts">
              {[500, 1000, 2000, 5000].map((a) => (
                <button key={a} type="button" className="btn btn-outline btn-sm" onClick={() => setAmount(String(a))}>₹{a}</button>
              ))}
            </div>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Processing...' : 'Withdraw'}
            </button>
          </form>
          <button className="btn btn-outline" onClick={() => { setActiveOp(null); setAmount(''); }}>Back to Menu</button>
        </div>
      )}

      {/* Deposit */}
      {step === 'MENU' && activeOp === 'deposit' && (
        <div className="card-entry-container">
          <h2>Deposit Cash</h2>
          <form onSubmit={handleDeposit} className="card-entry-form">
            <input type="number" className="input" value={amount} onChange={(e) => setAmount(e.target.value)}
              placeholder="Enter amount (min 100)" min="100" step="100" required autoFocus />
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Processing...' : 'Deposit'}
            </button>
          </form>
          <button className="btn btn-outline" onClick={() => { setActiveOp(null); setAmount(''); }}>Back to Menu</button>
        </div>
      )}

      {/* Mini Statement */}
      {step === 'MENU' && activeOp === 'statement' && (
        <div className="card-entry-container">
          <h2>Mini Statement</h2>
          {loading ? <p>Loading...</p> : miniStatement.length === 0 ? (
            <p>No recent transactions</p>
          ) : (
            <div className="atm-stmt-list">
              {miniStatement.map((txn, idx) => (
                <div key={idx} className="atm-stmt-row">
                  <span>{txn.description || txn.type}</span>
                  <span className={txn.type === 'CREDIT' ? 'text-success' : 'text-danger'}>
                    {txn.type === 'CREDIT' ? '+' : '-'}₹{parseFloat(txn.amount).toFixed(2)}
                  </span>
                </div>
              ))}
            </div>
          )}
          <button className="btn btn-primary" onClick={() => setActiveOp(null)}>Back to Menu</button>
        </div>
      )}

      {/* Receipt */}
      {step === 'MENU' && activeOp === 'receipt' && (
        <div className="card-entry-container print-receipt">
          {!lastTxn ? (
            <p className="text-muted">No transaction found.</p>
          ) : (
            <div className="receipt-wrapper">
              <div className="receipt-card">
                <div className="receipt-header">
                  <div>
                    <div className="receipt-bank-title">FinTrust Bank</div>
                    <div className="receipt-bank-subtitle">Digital Banking</div>
                  </div>
                  <div className="receipt-badge">
                    {String(lastTxn.type || '').replace('_', ' ')}:&nbsp;
                    ₹{Number(lastTxn.amount || 0).toLocaleString('en-IN')}
                  </div>
                </div>

                <h2 className="receipt-title">Transaction Receipt</h2>

                <div className="receipt-body">
                  <div className="receipt-row">
                    <span className="receipt-label">Txn ID</span>
                    <span className="receipt-value">{lastTxn.txnId ?? '-'}</span>
                  </div>
                  <div className="receipt-row">
                    <span className="receipt-label">Account Number</span>
                    <span className="receipt-value">{lastTxn.accountNo ?? accountNo}</span>
                  </div>
                  <div className="receipt-row">
                    <span className="receipt-label">Transaction Type</span>
                    <span className="receipt-value">
                      {String(lastTxn.type || '').replace('_', ' ')}
                    </span>
                  </div>
                  <div className="receipt-row">
                    <span className="receipt-label">Amount</span>
                    <span className="receipt-value">
                      ₹{Number(lastTxn.amount || 0).toLocaleString('en-IN')}
                    </span>
                  </div>
                  <div className="receipt-row">
                    <span className="receipt-label">Balance After</span>
                    <span className="receipt-value">
                      ₹{Number(lastTxn.balanceAfter || 0).toLocaleString('en-IN')}
                    </span>
                  </div>
                  <div className="receipt-row">
                    <span className="receipt-label">Date &amp; Time</span>
                    <span className="receipt-value">
                      {lastTxn.timestamp
                        ? new Date(lastTxn.timestamp).toLocaleString()
                        : '-'}
                    </span>
                  </div>
                </div>

                <div className="receipt-actions">
                  <button
                    className="btn btn-outline"
                    onClick={() => window.print()}
                    disabled={!lastTxn}
                  >
                    Generate Receipt
                  </button>
                  <button className="btn btn-primary" onClick={() => setActiveOp(null)}>
                    Back to Menu
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default CardEntry;
