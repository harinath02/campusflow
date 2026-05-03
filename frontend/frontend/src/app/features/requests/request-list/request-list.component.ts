import { DatePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { REQUEST_STATUS_OPTIONS, isPendingStatus } from '../../../core/constants/request-status.constants';
import { CampusRequest, RequestType } from '../../../core/models/request.model';
import { User } from '../../../core/models/user.model';
import { AuthService } from '../../../core/services/auth.service';
import { RequestService } from '../../../core/services/request.service';
import { RequestTypeService } from '../../../core/services/request-type.service';
import { StatusBadgeComponent } from '../../../shared/status-badge/status-badge.component';
import { ToastService } from '../../../shared/toast/toast.service';

@Component({
  selector: 'app-request-list',
  imports: [RouterLink, DatePipe, StatusBadgeComponent],
  templateUrl: './request-list.component.html',
  styleUrl: './request-list.component.scss'
})
export class RequestListComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly requestService = inject(RequestService);
  private readonly requestTypeService = inject(RequestTypeService);
  private readonly toast = inject(ToastService);

  protected readonly currentUser: User | null = this.auth.getCurrentUser();
  protected readonly statuses = REQUEST_STATUS_OPTIONS;
  protected loading = true;
  protected requests: CampusRequest[] = [];
  protected requestTypes: RequestType[] = [];
  protected search = '';
  protected statusFilter = '';
  protected typeFilter = '';

  protected get isAdmin(): boolean { return this.currentUser?.role === 'ADMIN'; }
  protected get isOfficer(): boolean { return this.currentUser?.role === 'OFFICER'; }
  protected get isStudent(): boolean { return this.currentUser?.role === 'STUDENT'; }
  protected get pageEyebrow(): string { return this.isAdmin ? 'Admin request queue' : this.isOfficer ? 'Department request queue' : 'Student requests'; }
  protected get pageTitle(): string { return this.isAdmin ? 'Campus request queue' : this.isOfficer ? `${this.currentUser?.department || 'Department'} request queue` : 'My campus requests'; }
  protected get pageDescription(): string {
    if (this.isAdmin) return 'Read-only view of every request moving through CampusFlow.';
    if (this.isOfficer) return 'Requests routed to your department for review.';
    return 'Search, filter, and open every request raised from your student account.';
  }

  protected get filtered(): CampusRequest[] {
    const q = this.search.toLowerCase().trim();
    return this.requests.filter((request) => {
      const matchesStatus = !this.statusFilter
        || (this.statusFilter === 'PENDING_GROUP' ? isPendingStatus(request.status) : request.status === this.statusFilter);
      const matchesType = !this.typeFilter || String(request.requestTypeId) === this.typeFilter || request.requestType === this.typeFilter;
      const matchesSearch = !q || `${request.title} ${request.requestNumber} ${request.requestType} ${request.status}`.toLowerCase().includes(q);
      return matchesStatus && matchesType && matchesSearch;
    });
  }

  ngOnInit(): void { this.load(); }

  load(): void {
    if (!this.currentUser) return;
    this.loading = true;
    const source = this.isAdmin
      ? this.requestService.getRequests()
      : this.isOfficer && this.currentUser.departmentId
        ? this.requestService.getRequestsByDepartment(this.currentUser.departmentId)
        : this.requestService.getRequestsByUser(this.currentUser.id);

    source.subscribe({
      next: (items) => {
        this.requests = this.sortRequests(items ?? []);
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.toast.error('Unable to load requests', error?.error?.message || 'Please try again.');
      }
    });
    this.requestTypeService.getRequestTypes().subscribe({
      next: (items) => this.requestTypes = (items ?? []).filter((item) => item.active),
      error: () => this.requestTypes = []
    });
  }

  private sortRequests(items: CampusRequest[]): CampusRequest[] {
    return items.slice().sort((a, b) => new Date(b.createdAt ?? 0).getTime() - new Date(a.createdAt ?? 0).getTime());
  }

  protected detailLink(request: CampusRequest): string[] {
    if (this.isOfficer) return ['/department/requests', request.id.toString()];
    if (this.isAdmin) return ['/requests', request.id.toString()];
    return ['/student/requests', request.id.toString()];
  }
}
