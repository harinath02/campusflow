import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Approval, ApprovalPayload, CampusRequest, CreateCampusRequestPayload, RequestType } from '../models/request.model';
import { isPendingStatus } from '../constants/request-status.constants';
import { ApiService } from './api.service';

@Injectable({ providedIn: 'root' })
export class RequestService {
  private readonly api = inject(ApiService);
  getRequests(): Observable<CampusRequest[]> { return this.api.get<CampusRequest[]>('/requests/get'); }
  getPendingRequests(): Observable<CampusRequest[]> { return this.getRequests().pipe(map((requests) => requests.filter((request) => isPendingStatus(request.status)))); }
  getRequest(id: number): Observable<CampusRequest> { return this.api.get<CampusRequest>(`/requests/${id}`); }
  createRequest(payload: CreateCampusRequestPayload): Observable<CampusRequest> { return this.api.post<CampusRequest, CreateCampusRequestPayload>('/requests/create', payload); }
  submitRequest(id: number): Observable<CampusRequest> { return this.api.post<CampusRequest, Record<string, never>>(`/requests/${id}/submit`, {}); }
  approveRequest(payload: ApprovalPayload): Observable<string> { return this.api.post<string, ApprovalPayload>('/requests/approve', payload); }
  rejectRequest(payload: ApprovalPayload): Observable<string> { return this.api.post<string, ApprovalPayload>('/requests/reject', payload); }
  holdRequest(payload: ApprovalPayload): Observable<string> { return this.api.post<string, ApprovalPayload>('/requests/hold', payload); }
  getApprovals(id: number): Observable<Approval[]> { return this.api.get<Approval[]>(`/requests/${id}/approvals`); }
  getRequestsByUser(userId: number): Observable<CampusRequest[]> { return this.api.get<CampusRequest[]>(`/requests/by-user/${userId}`); }
  getRequestsByStatus(status: string): Observable<CampusRequest[]> { return this.api.get<CampusRequest[]>(`/requests/by-status/${status}`); }
  getRequestsByDepartment(departmentId: number): Observable<CampusRequest[]> { return this.api.get<CampusRequest[]>(`/requests/by-department/${departmentId}`); }
  getPendingRequestsByDepartment(departmentId: number): Observable<CampusRequest[]> { return this.api.get<CampusRequest[]>(`/requests/by-department/${departmentId}/pending`); }
  getRequestsByType(requestTypeId: number): Observable<CampusRequest[]> { return this.api.get<CampusRequest[]>(`/requests/by-type/${requestTypeId}`); }
  getRequestTypes(): Observable<RequestType[]> { return this.api.get<RequestType[]>('/request-types/get'); }
  createRequestType(payload: Omit<RequestType, 'id' | 'active'> & { active?: boolean }): Observable<RequestType> { return this.api.post<RequestType, typeof payload>('/request-types/create', payload); }
  updateRequestType(id: number, payload: Omit<RequestType, 'id' | 'active'> & { active?: boolean }): Observable<RequestType> { return this.api.put<RequestType, typeof payload>(`/request-types/${id}`, payload); }
  deleteRequestType(id: number): Observable<string> { return this.api.delete<string>(`/request-types/${id}`); }
}
