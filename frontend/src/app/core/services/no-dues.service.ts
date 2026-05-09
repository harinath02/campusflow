import { Injectable, inject } from '@angular/core';
import { catchError, map, Observable, of, tap } from 'rxjs';
import { DepartmentService } from './department.service';
import { ApiService } from './api.service';
import { CreateNoDuesPayload, NoDuesDecisionPayload, NoDuesRequest } from '../models/no-dues.model';
import { Department } from '../models/department.model';

const STORAGE_KEY = 'campusflow_no_dues_cache';

@Injectable({ providedIn: 'root' })
export class NoDuesService {
  private readonly api = inject(ApiService);
  private readonly departments = inject(DepartmentService);

  getAll(): Observable<NoDuesRequest[]> {
    return this.api.get<NoDuesRequest[]>('/no-dues').pipe(
      tap((items) => this.saveLocal(items ?? [])),
      catchError(() => of(this.ensureLocal()))
    );
  }

  create(payload: CreateNoDuesPayload, studentName = 'Student'): Observable<NoDuesRequest> {
    return this.api.post<NoDuesRequest, CreateNoDuesPayload>('/no-dues', payload).pipe(
      tap((item) => this.upsertLocal(item)),
      catchError(() => this.createLocal(payload, studentName))
    );
  }

  decide(id: number, action: 'clear' | 'hold' | 'reject', payload: NoDuesDecisionPayload): Observable<NoDuesRequest> {
    return this.api.post<NoDuesRequest, NoDuesDecisionPayload>(`/no-dues/${id}/${action}`, payload).pipe(
      tap((item) => this.upsertLocal(item)),
      catchError(() => of(this.decideLocal(id, action, payload)))
    );
  }

  private createLocal(payload: CreateNoDuesPayload, studentName: string): Observable<NoDuesRequest> {
    return this.departments.getDepartments().pipe(
      catchError(() => of(this.defaultDepartments())),
      map((departments) => {
        const item: NoDuesRequest = {
          id: Date.now(),
          studentName,
          academicYear: payload.academicYear,
          semester: payload.semester,
          overallStatus: 'PENDING',
          initiatedAt: new Date().toISOString(),
          completedAt: null,
          departmentStatuses: (departments.length ? departments : this.defaultDepartments()).map((department, index) => ({
            id: index + 1,
            departmentId: department.id,
            department: department.name,
            status: 'PENDING',
            remarks: 'Waiting for department clearance',
            clearedBy: null,
            clearedAt: null
          }))
        };
        this.upsertLocal(item);
        return item;
      })
    );
  }

  private defaultDepartments(): Department[] {
    return [
      { id: 1, name: 'Library', code: 'LIB' },
      { id: 2, name: 'Accounts', code: 'ACC' },
      { id: 3, name: 'Hostel', code: 'HOS' },
      { id: 4, name: 'Admin Office', code: 'ADM' }
    ];
  }

  private defaultRequests(): NoDuesRequest[] {
    return [
      {
        id: 1001,
        studentName: 'Aarav Sharma',
        academicYear: '2025-26',
        semester: '6',
        overallStatus: 'PENDING',
        initiatedAt: new Date(Date.now() - 86400000 * 3).toISOString(),
        completedAt: null,
        departmentStatuses: this.defaultDepartments().map((department, index) => ({
          id: 5000 + index,
          departmentId: department.id,
          department: department.name,
          status: index === 0 ? 'CLEARED' : 'PENDING',
          remarks: index === 0 ? 'Books returned and dues cleared.' : 'Waiting for department review.',
          clearedBy: index === 0 ? 'Library Officer' : null,
          clearedAt: index === 0 ? new Date(Date.now() - 86400000).toISOString() : null
        }))
      },
      {
        id: 1002,
        studentName: 'Meera Nair',
        academicYear: '2025-26',
        semester: '8',
        overallStatus: 'HOLD',
        initiatedAt: new Date(Date.now() - 86400000 * 5).toISOString(),
        completedAt: null,
        departmentStatuses: this.defaultDepartments().map((department, index) => ({
          id: 5100 + index,
          departmentId: department.id,
          department: department.name,
          status: department.name === 'Hostel' ? 'HOLD' : 'PENDING',
          remarks: department.name === 'Hostel' ? 'Room inspection pending.' : 'Waiting for department review.',
          clearedBy: department.name === 'Hostel' ? 'Hostel Officer' : null,
          clearedAt: department.name === 'Hostel' ? new Date(Date.now() - 86400000 * 2).toISOString() : null
        }))
      }
    ];
  }

  private getLocal(): NoDuesRequest[] {
    if (typeof localStorage === 'undefined') return [];
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) as NoDuesRequest[] : [];
  }

  private ensureLocal(): NoDuesRequest[] {
    const existing = this.getLocal();
    if (existing.length) return existing;
    const fallback = this.defaultRequests();
    this.saveLocal(fallback);
    return fallback;
  }

  private saveLocal(items: NoDuesRequest[]): void {
    if (typeof localStorage === 'undefined') return;
    localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
  }

  private upsertLocal(item: NoDuesRequest): void {
    const items = this.getLocal();
    const index = items.findIndex((existing) => existing.id === item.id);
    if (index >= 0) items[index] = item;
    else items.unshift(item);
    this.saveLocal(items);
  }

  private decideLocal(id: number, action: 'clear' | 'hold' | 'reject', payload: NoDuesDecisionPayload): NoDuesRequest {
    const items = this.ensureLocal();
    const target = items.find((item) => item.id === id);
    if (!target) throw new Error('Request not found');

    const departmentStatus = target.departmentStatuses.find((status) => status.departmentId === payload.departmentId);
    if (!departmentStatus) throw new Error('Department status not found');

    const statusMap = { clear: 'CLEARED', hold: 'HOLD', reject: 'REJECTED' } as const;
    departmentStatus.status = statusMap[action];
    departmentStatus.remarks = payload.remarks || `${statusMap[action].toLowerCase()} by department.`;
    departmentStatus.clearedBy = 'Department Officer';
    departmentStatus.clearedAt = new Date().toISOString();

    if (target.departmentStatuses.some((status) => status.status === 'REJECTED')) target.overallStatus = 'REJECTED';
    else if (target.departmentStatuses.some((status) => status.status === 'HOLD')) target.overallStatus = 'HOLD';
    else if (target.departmentStatuses.every((status) => status.status === 'CLEARED')) {
      target.overallStatus = 'COMPLETED';
      target.completedAt = new Date().toISOString();
    } else target.overallStatus = 'PENDING';

    this.saveLocal(items);
    return target;
  }
}
