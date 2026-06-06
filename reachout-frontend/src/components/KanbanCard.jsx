import { Draggable } from '@hello-pangea/dnd';
import { deleteApplication } from '../api/applications';
import toast from 'react-hot-toast';

export default function KanbanCard({ app, index, onDeleted }) {

  const handleDelete = async () => {
    if (!window.confirm(`Delete ${app.company} application?`)) return;
    try {
      await deleteApplication(app.id);
      toast.success('Application deleted');
      onDeleted(app.id);
    } catch {
      toast.error('Failed to delete');
    }
  };

  return (
    <Draggable draggableId={String(app.id)} index={index}>
      {(provided, snapshot) => (
        <div
          ref={provided.innerRef}
          {...provided.draggableProps}
          {...provided.dragHandleProps}
          style={{
            ...styles.card,
            boxShadow: snapshot.isDragging ? '0 8px 24px rgba(0,0,0,0.15)' : '0 1px 4px rgba(0,0,0,0.08)',
            ...provided.draggableProps.style
          }}
        >
          <div style={styles.cardHeader}>
            <span style={styles.company}>{app.company}</span>
            <button onClick={handleDelete} style={styles.deleteBtn}>✕</button>
          </div>
          <div style={styles.role}>{app.role}</div>
          {app.location && <div style={styles.meta}>📍 {app.location}</div>}
          {app.salaryRange && <div style={styles.meta}>💰 {app.salaryRange}</div>}
          <div style={styles.meta}>📅 {app.appliedDate}</div>
          {app.notes && <div style={styles.notes}>{app.notes}</div>}
        </div>
      )}
    </Draggable>
  );
}

const styles = {
  card: { background: 'white', borderRadius: '10px', padding: '14px', marginBottom: '10px', cursor: 'grab', transition: 'box-shadow 0.2s' },
  cardHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '4px' },
  company: { fontWeight: 700, fontSize: '15px', color: '#1a1a2e' },
  role: { fontSize: '13px', color: '#4f46e5', fontWeight: 500, marginBottom: '8px' },
  meta: { fontSize: '12px', color: '#888', marginBottom: '3px' },
  notes: { fontSize: '12px', color: '#666', marginTop: '8px', padding: '8px', background: '#f8f9fa', borderRadius: '6px' },
  deleteBtn: { background: 'none', border: 'none', cursor: 'pointer', color: '#ccc', fontSize: '14px', padding: '0 2px' }
};