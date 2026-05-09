export type NoDuesStatus = 'PENDING' | 'CLEARED' | 'REJECTED' | 'HOLD' | 'COMPLETED' | string;

export interface NoDuesDepartmentStatus {
  id?: number;
  departmentId?: number;
  department: string;
  status: NoDuesStatus;
  remarks?: string | null;
  clearedBy?: string | null;
  clearedAt?: string | null;
}

export interface NoDuesRequest {
  id: number;
  studentName: string;
  academicYear: string;
  semester?: string | null;
  overallStatus: NoDuesStatus;
  initiatedAt: string;
  completedAt?: string | null;
  departmentStatuses: NoDuesDepartmentStatus[];
}

export interface CreateNoDuesPayload {
  studentId: number;
  academicYear: string;
  semester?: string | null;
}

export interface NoDuesDecisionPayload {
  departmentId: number;
  officerId: number;
  remarks?: string | null;
}
