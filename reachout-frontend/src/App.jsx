import { useState, useEffect } from 'react';
import { DragDropContext } from '@hello-pangea/dnd';
import { useNavigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import { getApplications, updateApplication } from './api/applications';
import KanbanColumn from './components/KanbanColumn';
import AddApplicationModal from './components/AddApplicationModal';
import Dashboard from './pages/Dashboard';
import toast from 'react-hot-toast';

const STATUSES = ['APPLIED', 'PHONE_SCREEN', 'INTERVIEW', 'OFFER', 'REJECTED'];

export default function App() {
  const [applications, setApplications] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(true);
  const { user, logout } = useAuth();
  const [activeView, setActiveView] = useState('kanban');
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) { navigate('/login'); return; }
    fetchApplications();
  }, []);

  const fetchApplications = async () => {
    try {
      const res = await getApplications();
      setApplications(res.data);
    } catch {
      toast.error('Failed to load applications');
    } finally {
      setLoading(false);
    }
  };

  const handleDragEnd = async (result) => {
    const { destination, source, draggableId } = result;
    if (!destination) return;
    if (destination.droppableId === source.droppableId) return;

    const appId = parseInt(draggableId);
    const newStatus = destination.droppableId;
    const app = applications.find(a => a.id === appId);

    // optimistic update — update UI immediately, then call API
    setApplications(prev =>
      prev.map(a => a.id === appId ? { ...a, status: newStatus } : a)
    );

    try {
      await updateApplication(appId, { ...app, status: newStatus });
      toast.success(`Moved to ${newStatus.replace('_', ' ')}`);
    } catch {
      // revert on failure
      setApplications(prev =>
        prev.map(a => a.id === appId ? { ...a, status: app.status } : a)
      );
      toast.error('Failed to update status');
    }
  };

  const handleAdded = (newApp) => {
    setApplications(prev => [...prev, newApp]);
  };

  const handleDeleted = (id) => {
    setApplications(prev => prev.filter(a => a.id !== id));
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const byStatus = (status) => applications.filter(a => a.status === status);

  if (loading) return <div style={styles.loading}>Loading your applications...</div>;

  return (
    <div style={styles.page}>
     {/* Navbar */}
<div style={styles.navbar}>
  <span style={styles.logo}>ReachOut 🚀</span>
  <div style={styles.navTabs}>
    <button
      onClick={() => setActiveView('kanban')}
      style={{ ...styles.navTab, ...(activeView === 'kanban' ? styles.navTabActive : {}) }}>
      📋 Board
    </button>
    <button
      onClick={() => setActiveView('dashboard')}
      style={{ ...styles.navTab, ...(activeView === 'dashboard' ? styles.navTabActive : {}) }}>
      📊 Analytics
    </button>
  </div>
  <div style={styles.navRight}>
    <span style={styles.welcome}>Hi, {user?.name}</span>
    <button onClick={() => setShowModal(true)} style={styles.addBtn}>+ Add Application</button>
    <button onClick={handleLogout} style={styles.logoutBtn}>Logout</button>
  </div>
</div>

{/* Content */}
{activeView === 'kanban' ? (
  <div style={styles.board}>
    <DragDropContext onDragEnd={handleDragEnd}>
      <div style={styles.columns}>
        {STATUSES.map(status => (
          <KanbanColumn
            key={status}
            status={status}
            applications={byStatus(status)}
            onDeleted={handleDeleted}
          />
        ))}
      </div>
    </DragDropContext>
  </div>
) : (
  <Dashboard />
)}

    

      {showModal && (
        <AddApplicationModal
          onClose={() => setShowModal(false)}
          onAdded={handleAdded}
        />
      )}
    </div>
  );
}

const styles = {
  page: { minHeight: '100vh', background: '#f0f4f8', fontFamily: 'system-ui, sans-serif' },
  navbar: { background: 'white', padding: '1rem 2rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', boxShadow: '0 1px 4px rgba(0,0,0,0.08)' },
  logo: { fontWeight: 800, fontSize: '1.3rem', color: '#1a1a2e' },
  navRight: { display: 'flex', alignItems: 'center', gap: '16px' },
  welcome: { fontSize: '14px', color: '#666' },
  appCount: { fontSize: '13px', background: '#eef2ff', color: '#4f46e5', padding: '4px 10px', borderRadius: '20px', fontWeight: 600 },
  addBtn: { padding: '8px 18px', background: '#4f46e5', color: 'white', border: 'none', borderRadius: '8px', fontSize: '14px', fontWeight: 600, cursor: 'pointer' },
  logoutBtn: { padding: '8px 14px', background: 'none', border: '1px solid #ddd', borderRadius: '8px', fontSize: '14px', cursor: 'pointer', color: '#666' },
  board: { padding: '2rem', overflowX: 'auto' },
  columns: { display: 'flex', gap: '16px', minWidth: 'max-content' },
  loading: { display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh', fontSize: '16px', color: '#666' },
  navTabs: { display: 'flex', gap: '4px', background: '#f0f4f8', padding: '4px', borderRadius: '10px' },
navTab: { padding: '7px 16px', borderRadius: '8px', border: 'none', background: 'none', cursor: 'pointer', fontSize: '13px', fontWeight: 500, color: '#666' },
navTabActive: { background: 'white', color: '#4f46e5', boxShadow: '0 1px 4px rgba(0,0,0,0.1)' },
};