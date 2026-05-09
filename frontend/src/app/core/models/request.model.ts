export type RequestStatus = 'DRAFT' | 'SUBMITTED' | 'IN_REVIEW' | 'APPROVED' | 'REJECTED' | 'ON_HOLD' | 'RETURNED' | 'COMPLETED' | 'CANCELLED' | string;

export interface RequestType {
  id: number;
  name: string;
  code: string;
  description?: string;
  active: boolean;
}

export interface CampusRequest {
  id: number;
  requestNumber: string;
  requestTypeId?: number;
  requestType: string;
  requesterId?: number;
  requesterName: string;
  title: string;
  description?: string;
  status: RequestStatus;
  priority?: string;
  createdAt?: string;
  submittedAt?: string;
  expectedCompletionTime?: string;
  actualCompletionTime?: string;
  delayed?: boolean;
}

export interface CreateCampusRequestPayload {
  requestTypeId: number;
  requesterId: number;
  title: string;
  description?: string;
  priority?: string;
}

export interface ApprovalPayload {
  requestId: number;
  departmentId: number;
  actorUserId?: number;
  remarks?: string;
}

export interface Approval {
  id: number;
  requestId: number;
  departmentId?: number;
  department: string;
  status: string;
  remarks?: string;
  actionAt?: string;
}
