export interface DashboardSummaryCard {
  label: string;
  value: number;
  tone: 'blue' | 'green' | 'amber' | 'red' | 'slate';
}

export interface DashboardSummary {
  totalUsers: number;
  totalDepartments: number;
  pendingRequests: number;
  approvedRequests: number;
  rejectedRequests: number;
}
