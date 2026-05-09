import { DashboardSummaryCard } from './dashboard.model';

export const EMPTY_DASHBOARD_CARDS: DashboardSummaryCard[] = [
  { label: 'Total Users', value: 0, tone: 'blue' },
  { label: 'Departments', value: 0, tone: 'slate' },
  { label: 'Pending', value: 0, tone: 'amber' },
  { label: 'Approved', value: 0, tone: 'green' },
  { label: 'Rejected', value: 0, tone: 'red' }
];
