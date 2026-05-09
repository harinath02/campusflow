export interface AuditLog {
  id: number;
  userId?: number | null;
  actorName: string;
  action: string;
  entityType: string;
  entityId?: number | null;
  description?: string | null;
  timestamp: string;
}
