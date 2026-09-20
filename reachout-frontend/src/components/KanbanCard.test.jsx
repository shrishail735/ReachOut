import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { DragDropContext, Droppable } from '@hello-pangea/dnd';
import KanbanCard from './KanbanCard';
import * as applicationApi from '../api/applications';
import toast from 'react-hot-toast';

vi.mock('../api/applications', () => ({
  deleteApplication: vi.fn(),
}));

vi.mock('react-hot-toast', () => ({
  default: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

describe('KanbanCard', () => {
  const app = {
    id: 7,
    company: 'Contoso',
    role: 'Product Engineer',
    location: 'Boston, MA',
    salaryRange: '$140k',
    appliedDate: '2026-01-10',
    notes: 'Strong interview loop',
  };

  const renderCard = (onDeleted = vi.fn()) => {
    render(
      <DragDropContext onDragEnd={() => {}}>
        <Droppable droppableId="board-column">
          {(provided) => (
            <div ref={provided.innerRef} {...provided.droppableProps}>
              <KanbanCard app={app} index={0} onDeleted={onDeleted} />
              {provided.placeholder}
            </div>
          )}
        </Droppable>
      </DragDropContext>
    );
  };

  beforeEach(() => {
    vi.clearAllMocks();
    vi.stubGlobal('confirm', vi.fn(() => true));
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it('renders application details', () => {
    renderCard();

    expect(screen.getByText('Contoso')).toBeInTheDocument();
    expect(screen.getByText('Product Engineer')).toBeInTheDocument();
    expect(screen.getByText('📍 Boston, MA')).toBeInTheDocument();
    expect(screen.getByText('💰 $140k')).toBeInTheDocument();
    expect(screen.getByText('📅 2026-01-10')).toBeInTheDocument();
    expect(screen.getByText('Strong interview loop')).toBeInTheDocument();
  });

  it('deletes the application and calls onDeleted', async () => {
    applicationApi.deleteApplication.mockResolvedValue({});
    const onDeleted = vi.fn();

    renderCard(onDeleted);

    const deleteButton = screen.getByText('✕').closest('button');
    fireEvent.click(deleteButton);

    await waitFor(() => expect(applicationApi.deleteApplication).toHaveBeenCalledWith(7));
    expect(toast.success).toHaveBeenCalledWith('Application deleted');
    expect(onDeleted).toHaveBeenCalledWith(7);
  });

  it('shows an error toast if deletion fails', async () => {
    applicationApi.deleteApplication.mockRejectedValue(new Error('Delete failed'));
    renderCard();

    const deleteButton = screen.getByText('✕').closest('button');
    fireEvent.click(deleteButton);

    await waitFor(() => expect(toast.error).toHaveBeenCalledWith('Failed to delete'));
  });
});
