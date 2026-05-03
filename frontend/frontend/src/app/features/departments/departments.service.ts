import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Department, DepartmentPayload } from '../../core/models/department.model';
import { DepartmentService } from '../../core/services/department.service';

@Injectable({ providedIn: 'root' })
export class DepartmentsFacadeService {
  private readonly departmentService = inject(DepartmentService);

  getDepartments(): Observable<Department[]> {
    return this.departmentService.getDepartments();
  }

  saveDepartment(payload: DepartmentPayload, id?: number): Observable<Department> {
    return id
      ? this.departmentService.updateDepartment(id, payload)
      : this.departmentService.createDepartment(payload);
  }

  deleteDepartment(id: number): Observable<string> {
    return this.departmentService.deleteDepartment(id);
  }
}
