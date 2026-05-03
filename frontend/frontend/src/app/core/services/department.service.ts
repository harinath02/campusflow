import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Department, DepartmentPayload } from '../models/department.model';
import { ApiService } from './api.service';

@Injectable({ providedIn: 'root' })
export class DepartmentService {
  private readonly api = inject(ApiService);

  getDepartments(): Observable<Department[]> {
    return this.api.get<Department[]>('/departments');
  }

  createDepartment(payload: DepartmentPayload): Observable<Department> {
    return this.api.post<Department, DepartmentPayload>('/departments', payload);
  }

  updateDepartment(id: number, payload: DepartmentPayload): Observable<Department> {
    return this.api.put<Department, DepartmentPayload>(`/departments/${id}`, payload);
  }

  deleteDepartment(id: number): Observable<string> {
    return this.api.delete<string>(`/departments/${id}`);
  }
}
