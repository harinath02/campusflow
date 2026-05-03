import { Injectable, inject } from '@angular/core';
import { forkJoin, map, Observable } from 'rxjs';
import { REQUEST_STATUS, isPendingStatus } from '../../core/constants/request-status.constants';
import { CampusRequest } from '../../core/models/request.model';
import { DepartmentService } from '../../core/services/department.service';
import { RequestService } from '../../core/services/request.service';
import { UserService } from '../../core/services/user.service';
import { DashboardSummary } from './dashboard.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly userService = inject(UserService);
  private readonly departmentService = inject(DepartmentService);
  private readonly requestService = inject(RequestService);

  getSummary(): Observable<DashboardSummary> {
    return forkJoin({
      users: this.userService.getUsers(),
      departments: this.departmentService.getDepartments(),
      requests: this.requestService.getRequests()
    }).pipe(
      map(({ users, departments, requests }) => ({
        totalUsers: users.length,
        totalDepartments: departments.length,
        pendingRequests: requests.filter((request) => isPendingStatus(request.status)).length,
        approvedRequests: requests.filter((request) => request.status === REQUEST_STATUS.approved).length,
        rejectedRequests: requests.filter((request) => request.status === REQUEST_STATUS.rejected).length
      }))
    );
  }

  getRecentRequests(limit = 6): Observable<CampusRequest[]> {
    return this.requestService.getRequests().pipe(
      map((requests) => requests
        .slice()
        .sort((a, b) => new Date(b.createdAt ?? '').getTime() - new Date(a.createdAt ?? '').getTime())
        .slice(0, limit)
      )
    );
  }
}
