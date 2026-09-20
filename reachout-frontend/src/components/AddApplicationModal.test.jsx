import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import AddApplicationModal from './AddApplicationModal';
import * as applicationApi from '../api/applications';
import toast from 'react-hot-toast';

vi.mock('../api/applications', () => ({
  createApplication: vi.fn(),
}));

vi.mock('react-hot-toast', () => ({
  default: {
    success: vi.fn(),
    error: vi.fn(),
  },
}));

describe('AddApplicationModal', () => {
  const onClose = vi.fn();
  const onAdded = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders the form fields and submits a valid application', async () => {
    const user = userEvent.setup();
    applicationApi.createApplication.mockResolvedValue({ data: { id: 42, company: 'Acme', role: 'Frontend Engineer', status: 'APPLIED' } });

    render(<AddApplicationModal onClose={onClose} onAdded={onAdded} />);

    await user.type(screen.getByPlaceholderText('Company *'), 'Acme');
    await user.type(screen.getByPlaceholderText('Role *'), 'Frontend Engineer');
    await user.clear(screen.getByPlaceholderText('Location'));
    await user.type(screen.getByPlaceholderText('Location'), 'Remote');
    await user.type(screen.getByPlaceholderText('Salary Range'), '$120k');
    const dateInput = document.querySelector('input[type="date"]');
    await user.clear(dateInput);
    await user.type(dateInput, '2026-01-15');
    await user.type(screen.getByPlaceholderText('Notes'), 'Great fit');

    await user.click(screen.getByRole('button', { name: 'Add Application' }));

    await waitFor(() => expect(applicationApi.createApplication).toHaveBeenCalledTimes(1));
    expect(applicationApi.createApplication).toHaveBeenCalledWith(expect.objectContaining({
      company: 'Acme',
      role: 'Frontend Engineer',
      location: 'Remote',
      salaryRange: '$120k',
      notes: 'Great fit',
      status: 'APPLIED',
    }));
    expect(toast.success).toHaveBeenCalledWith('Application added!');
    expect(onAdded).toHaveBeenCalledWith(expect.objectContaining({ id: 42 }));
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('shows an error toast when submission fails', async () => {
    const user = userEvent.setup();
    applicationApi.createApplication.mockRejectedValue(new Error('Failed'));

    render(<AddApplicationModal onClose={onClose} onAdded={onAdded} />);

    await user.type(screen.getByPlaceholderText('Company *'), 'Acme');
    await user.type(screen.getByPlaceholderText('Role *'), 'Engineer');
    await user.click(screen.getByRole('button', { name: 'Add Application' }));

    await waitFor(() => expect(toast.error).toHaveBeenCalledWith('Failed to add application'));
    expect(onClose).not.toHaveBeenCalled();
  });

  it('closes the modal without submitting when cancel is clicked', async () => {
    const user = userEvent.setup();

    render(<AddApplicationModal onClose={onClose} onAdded={onAdded} />);
    await user.click(screen.getByRole('button', { name: 'Cancel' }));

    expect(onClose).toHaveBeenCalledTimes(1);
    expect(applicationApi.createApplication).not.toHaveBeenCalled();
  });
});
