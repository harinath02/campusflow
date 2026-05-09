export const REQUEST_STATUS = {
  draft: 'DRAFT',
  submitted: 'SUBMITTED',
  inReview: 'IN_REVIEW',
  approved: 'APPROVED',
  rejected: 'REJECTED',
  onHold: 'ON_HOLD',
  returned: 'RETURNED',
  completed: 'COMPLETED',
  cancelled: 'CANCELLED'
} as const;

export const PENDING_REQUEST_STATUSES = [REQUEST_STATUS.draft, REQUEST_STATUS.submitted, REQUEST_STATUS.inReview, REQUEST_STATUS.onHold] as const;

export const REQUEST_STATUS_OPTIONS = [
  { label: 'All requests', value: '' },
  { label: 'Pending', value: 'PENDING_GROUP' },
  { label: 'Completed', value: REQUEST_STATUS.completed },
  { label: 'On hold', value: REQUEST_STATUS.onHold },
  { label: 'Rejected', value: REQUEST_STATUS.rejected }
] as const;

export function isPendingStatus(status: string): boolean {
  return PENDING_REQUEST_STATUSES.includes(status?.toUpperCase() as typeof PENDING_REQUEST_STATUSES[number]);
}

export function getStatusBadgeClass(status: string): string {
  const normalizedStatus = status?.toUpperCase();

  if (normalizedStatus === REQUEST_STATUS.approved || normalizedStatus === REQUEST_STATUS.completed) {
    return 'success';
  }

  if (normalizedStatus === REQUEST_STATUS.rejected || normalizedStatus === REQUEST_STATUS.cancelled) {
    return 'danger';
  }

  if (normalizedStatus === REQUEST_STATUS.onHold || normalizedStatus === REQUEST_STATUS.returned) {
    return 'warning';
  }

  if (isPendingStatus(normalizedStatus)) {
    return 'pending';
  }

  return 'neutral';
}
