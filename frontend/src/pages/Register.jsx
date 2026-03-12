import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import toast from 'react-hot-toast';
import { MdCloudUpload } from 'react-icons/md';
import { documentsApi } from '../api/documents';

function Register() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [form, setForm] = useState({
    fullName: '',
    dateOfBirth: '',
    mobile: '',
    email: '',
    aadhaar: '',
    pan: '',
    accountType: '',
    address: '',
    panPhotoUrl: '',
    aadhaarPhotoUrl: '',
    passportPhotoUrl: '',
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.panPhotoUrl || !form.aadhaarPhotoUrl || !form.passportPhotoUrl) {
      toast.error('Please upload PAN, Aadhaar, and Passport photo before submitting.');
      return;
    }
    setLoading(true);
    try {
      const payload = {
        ...form,
        dateOfBirth: form.dateOfBirth || null,
      };
      await api.post('/accounts/register', payload);
      toast.success('Application submitted! Pending admin approval.');
      navigate('/login');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-page">
      <div className="register-card">
        <div className="register-header">
          <h1><span style={{ color: '#1a56db' }}>Fin</span>Trust</h1>
          <p>Open your account securely in a few minutes</p>
        </div>

        <form onSubmit={handleSubmit} className="register-form">
          <div className="form-group">
            <label>Full Name</label>
            <input
              type="text"
              name="fullName"
              value={form.fullName}
              onChange={handleChange}
              placeholder="Enter full name"
              required
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Date of Birth</label>
              <input
                type="date"
                name="dateOfBirth"
                value={form.dateOfBirth}
                onChange={handleChange}
                placeholder="dd-mm-yyyy"
              />
            </div>
            <div className="form-group">
              <label>Mobile</label>
              <input
                type="text"
                name="mobile"
                value={form.mobile}
                onChange={handleChange}
                placeholder="Enter mobile number"
                required
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Email</label>
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="Enter email address"
                required
              />
            </div>
            <div className="form-group">
              <label>Aadhaar</label>
              <input
                type="text"
                name="aadhaar"
                value={form.aadhaar}
                onChange={handleChange}
                placeholder="Enter Aadhaar number"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>PAN</label>
              <input
                type="text"
                name="pan"
                value={form.pan}
                onChange={handleChange}
                placeholder="Enter PAN number"
              />
            </div>
            <div className="form-group">
              <label>Account Type</label>
              <select
                name="accountType"
                className="select"
                value={form.accountType}
                onChange={handleChange}
                required
              >
                <option value="">Select</option>
                <option value="SAVINGS">Savings</option>
                <option value="CURRENT">Current</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label>Address</label>
            <textarea
              name="address"
              value={form.address}
              onChange={handleChange}
              placeholder="Enter full address"
              rows={3}
            />
          </div>

          <div className="upload-row">
            <UploadBox
              label="PAN Card"
              docType="PAN"
              valueUrl={form.panPhotoUrl}
              onUploaded={(url) => setForm((p) => ({ ...p, panPhotoUrl: url }))}
            />
            <UploadBox
              label="Aadhaar Card"
              docType="AADHAAR"
              valueUrl={form.aadhaarPhotoUrl}
              onUploaded={(url) => setForm((p) => ({ ...p, aadhaarPhotoUrl: url }))}
            />
            <UploadBox
              label="Passport Photo"
              docType="PASSPORT"
              valueUrl={form.passportPhotoUrl}
              onUploaded={(url) => setForm((p) => ({ ...p, passportPhotoUrl: url }))}
            />
          </div>

          <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
            {loading ? 'Submitting...' : 'Submit Application'}
          </button>
        </form>

        <div className="register-footer">
          <Link to="/login" className="link">Already have an account? Login</Link>
        </div>
      </div>
    </div>
  );
}

function UploadBox({ label, docType, onUploaded, valueUrl }) {
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);

  const handleFile = async (picked) => {
    if (!picked) return;
    if (picked.size > 2 * 1024 * 1024) {
      toast.error('Max file size is 2MB');
      return;
    }
    setFile(picked);
    setUploading(true);
    try {
      const res = await documentsApi.upload({ file: picked, docType });
      const url = res.data?.data?.url;
      if (!url) throw new Error('Upload failed');
      onUploaded?.(url);
      toast.success(`${label} uploaded`);
    } catch (err) {
      onUploaded?.('');
      setFile(null);
      toast.error(err.response?.data?.message || 'Upload failed');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="upload-box">
      <strong>{label}</strong>
      <label className="upload-area">
        <MdCloudUpload size={28} color="#1a56db" />
        <span>
          {uploading ? 'Uploading...' : file ? file.name : valueUrl ? 'Uploaded' : 'Drag & Drop file'}
        </span>
        <small>{valueUrl ? 'Upload complete' : 'or click to upload (Max 2MB)'}</small>
        <input
          type="file"
          accept="image/*,.pdf"
          style={{ display: 'none' }}
          onChange={(e) => handleFile(e.target.files?.[0] || null)}
        />
      </label>
    </div>
  );
}

export default Register;
