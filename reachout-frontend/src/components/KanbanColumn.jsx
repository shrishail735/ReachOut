import React from 'react';
import { Droppable } from '@hello-pangea/dnd';
import KanbanCard from './KanbanCard';

const COLUMN_COLORS = {
  APPLIED: '#4f46e5',
  PHONE_SCREEN: '#f59e0b',
  INTERVIEW: '#3b82f6',
  OFFER: '#10b981',
  REJECTED: '#ef4444'
};

export default function KanbanColumn({ status, applications, onDeleted }) {
  const label = status.replace('_', ' ');
  const color = COLUMN_COLORS[status];

  return (
    <div style={styles.column}>
      <div style={styles.header}>
        <div style={{ ...styles.dot, background: color }} />
        <span style={styles.title}>{label}</span>
        <span style={styles.count}>{applications.length}</span>
      </div>
      <Droppable droppableId={status}>
        {(provided, snapshot) => (
          <div
            ref={provided.innerRef}
            {...provided.droppableProps}
            style={{
              ...styles.cardList,
              background: snapshot.isDraggingOver ? '#f0f4ff' : '#f8f9fa'
            }}
          >
            {applications.map((app, index) => (
              <KanbanCard
                key={app.id}
                app={app}
                index={index}
                onDeleted={onDeleted}
              />
            ))}
            {provided.placeholder}
            {applications.length === 0 && (
              <div style={styles.empty}>Drop cards here</div>
            )}
          </div>
        )}
      </Droppable>
    </div>
  );
}

const styles = {
  column: { minWidth: '240px', maxWidth: '240px', display: 'flex', flexDirection: 'column' },
  header: { display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '12px', padding: '0 4px' },
  dot: { width: '10px', height: '10px', borderRadius: '50%' },
  title: { fontWeight: 600, fontSize: '13px', textTransform: 'uppercase', letterSpacing: '0.05em', flex: 1 },
  count: { background: '#e5e7eb', borderRadius: '20px', padding: '2px 8px', fontSize: '12px', fontWeight: 600 },
  cardList: { flex: 1, borderRadius: '10px', padding: '10px', minHeight: '200px', transition: 'background 0.2s' },
  empty: { textAlign: 'center', color: '#ccc', fontSize: '13px', padding: '2rem 0' }
};