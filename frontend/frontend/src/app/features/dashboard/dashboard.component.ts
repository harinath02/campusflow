import { DatePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import { isPendingStatus } from '../../core/constants/request-status.constants';
import { AuditLog } from '../../core/models/audit-log.model';
import { Department } from '../../core/models/department.model';
import { CampusRequest } from '../../core/models/request.model';
import { User } from '../../core/models/user.model';
import { AuditLogService } from '../../core/services/audit-log.service';
import { AuthService } from '../../core/services/auth.service';
import { DepartmentService } from '../../core/services/department.service';
import { RequestService } from '../../core/services/request.service';
import { UserService } from '../../core/services/user.service';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';
import { ToastService } from '../../shared/toast/toast.service';

@Component({
  selector: 'app-dashboard',
  imports: [DatePipe, RouterLink, StatusBadgeComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly requestService = inject(RequestService);
  private readonly departmentService = inject(DepartmentService);
  private readonly userService = inject(UserService);
  private readonly auditLogService = inject(AuditLogService);
  private readonly toast = inject(ToastService);

  protected readonly currentUser: User | null = this.auth.getCurrentUser();
  protected loading = true;
  protected requests: CampusRequest[] = [];
  protected departments: Department[] = [];
  protected users: User[] = [];
  protected auditLogs: AuditLog[] = [];

  protected get isStudent(): boolean { return this.currentUser?.role === 'STUDENT'; }
  protected get isOfficer(): boolean { return this.currentUser?.role === 'OFFICER'; }
  protected get isAdmin(): boolean { return this.currentUser?.role === 'ADMIN'; }
  protected get totalRequests(): number { return this.requests.length; }
  protected get pendingCount(): number { return this.requests.filter((item) => isPendingStatus(item.status)).length; }
  protected get completedCount(): number { return this.requests.filter((item) => item.status === 'COMPLETED').length; }
  protected get rejectedCount(): number { return this.requests.filter((item) => item.status === 'REJECTED' || item.status === 'ON_HOLD').length; }
  protected get delayedCount(): number { return this.requests.filter((item) => item.delayed).length; }
  protected get recentRequests(): CampusRequest[] { return this.requests.slice(0, 6); }

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    const requestSource = this.isStudent && this.currentUser
      ? this.requestService.getRequestsByUser(this.currentUser.id)
      : this.isOfficer && this.currentUser?.departmentId
        ? this.requestService.getRequestsByDepartment(this.currentUser.departmentId)
        : this.requestService.getRequests();

    forkJoin({
      requests: requestSource.pipe(catchError(() => of([]))),
      departments: this.isAdmin ? this.departmentService.getDepartments().pipe(catchError(() => of([]))) : of([]),
      users: this.isAdmin ? this.userService.getUsers().pipe(catchError(() => of([]))) : of([]),
      auditLogs: this.isAdmin ? this.auditLogService.getAuditLogs().pipe(catchError(() => of([]))) : of([])
    }).subscribe({
      next: ({ requests, departments, users, auditLogs }) => {
        this.requests = this.sortRequests(requests);
        this.departments = departments;
        this.users = users;
        this.auditLogs = auditLogs;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.toast.error('Dashboard failed to load', 'Please refresh the page.');
      }
    });
  }

  protected detailRoute(request: CampusRequest): string {
    if (this.isOfficer) return `/department/requests/${request.id}`;
    return `/student/requests/${request.id}`;
  }

  private sortRequests(requests: CampusRequest[]): CampusRequest[] {
    return (requests ?? []).slice().sort((a, b) => new Date(b.createdAt ?? 0).getTime() - new Date(a.createdAt ?? 0).getTime());
  }
}
