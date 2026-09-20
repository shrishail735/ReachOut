import React, { useState } from 'react';
import toast from 'react-hot-toast';
import { createApplication } from '../api/applications';

const STATUSES = ['APPLIED', 'PHONE_SCREEN', 'INTERVIEW', 'OFFER', 'REJECTED'];

export default function AddApplicationModal({ onClose, onAdded }) {
  const [form, setForm] = useState({
    company: '', role: '', status: 'APPLIED',
    notes: '', location: '', salaryRange: '',
    appliedDate: new Date().toISOString().split('T')[0]
  });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await createApplication(form);
      toast.success('Application added!');
      onAdded(res.data);
      onClose();
    } catch {
      toast.error('Failed to add application');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.overlay}>
      <div style={styles.modal}>
        <h2 style={styles.title}>Add Application</h2>
        <form onSubmit={handleSubmit} style={styles.form}>
          <input style={styles.input} placeholder="Company *"
            value={form.company} onChange={e => setForm({ ...form, company: e.target.value })} required />
          <input style={styles.input} placeholder="Role *"
            value={form.role} onChange={e => setForm({ ...form, role: e.target.value })} required />
          <select style={styles.input} value={form.status}
            onChange={e => setForm({ ...form, status: e.target.value })}>
            {STATUSES.map(s => <option key={s} value={s}>{s.replace('_', ' ')}</option>)}
          </select>
          <input style={styles.input} placeholder="Location"
            value={form.location} onChange={e => setForm({ ...form, location: e.target.value })} />
          <input style={styles.input} placeholder="Salary Range"
            value={form.salaryRange} onChange={e => setForm({ ...form, salaryRange: e.target.value })} />
          <input style={styles.input} type="date"
            value={form.appliedDate} onChange={e => setForm({ ...form, appliedDate: e.target.value })} required />
          <textarea style={{ ...styles.input, height: '80px' }} placeholder="Notes"
            value={form.notes} onChange={e => setForm({ ...form, notes: e.target.value })} />
          <div style={styles.buttons}>
            <button type="button" onClick={onClose} style={styles.cancelBtn}>Cancel</button>
            <button type="submit" style={styles.submitBtn} disabled={loading}>
              {loading ? 'Adding...' : 'Add Application'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

const styles = {
  overlay: { position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 },
  modal: { background: 'white', borderRadius: '12px', padding: '2rem', width: '100%', maxWidth: '480px', maxHeight: '90vh', overflowY: 'auto' },
  title: { margin: '0 0 1.5rem', fontSize: '1.3rem', fontWeight: 700 },
  form: { display: 'flex', flexDirection: 'column', gap: '12px' },
  input: { padding: '10px 12px', borderRadius: '8px', border: '1px solid #ddd', fontSize: '14px', outline: 'none', width: '100%', boxSizing: 'border-box' },
  buttons: { display: 'flex', gap: '10px', justifyContent: 'flex-end', marginTop: '8px' },
  cancelBtn: { padding: '10px 20px', borderRadius: '8px', border: '1px solid #ddd', background: 'white', cursor: 'pointer', fontWeight: 500 },
  submitBtn: { padding: '10px 20px', borderRadius: '8px', border: 'none', background: '#4f46e5', color: 'white', cursor: 'pointer', fontWeight: 600 }
};