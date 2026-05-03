import { DatePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin, map, of, switchMap } from 'rxjs';
import { Approval, CampusRequest } from '../../core/models/request.model';
import { User } from '../../core/models/user.model';
import { AuthService } from '../../core/services/auth.service';
import { RequestService } from '../../core/services/request.service';
import { RemarkDialogComponent } from '../../shared/remark-dialog/remark-dialog.component';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';
import { ToastService } from '../../shared/toast/toast.service';

type Decision = 'approve' | 'hold' | 'reject';
interface QueueItem { request: CampusRequest; approval: Approval | null; }

@Component({
  selector: 'app-approvals',
  imports: [DatePipe, RouterLink, RemarkDialogComponent, StatusBadgeComponent],
  templateUrl: './approvals.component.html',
  styleUrl: './approvals.component.scss'
})
export class ApprovalsComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly requestService = inject(RequestService);
  private readonly toast = inject(ToastService);

  protected readonly currentUser: User | null = this.auth.getCurrentUser();
  protected queue: QueueItem[] = [];
  protected loading = false;
  protected search = '';
  protected statusFilter = 'IN_REVIEW';
  protected activeItem: QueueItem | null = null;
  protected decision: Decision | null = null;
  protected remarks = '';
  protected saving = false;

  protected get visible(): QueueItem[] {
    const q = this.search.toLowerCase().trim();
    return this.queue.filter((item) => {
      const status = item.approval?.status || item.request.status;
      const matchesStatus = this.statusFilter === 'ALL' || status === this.statusFilter;
      const matchesSearch = !q || `${item.request.title} ${item.request.requesterName} ${item.request.requestNumber}`.toLowerCase().includes(q);
      return matchesStatus && matchesSearch;
    });
  }

  protected get pendingCount(): number {
    return this.queue.filter((item) => ['IN_REVIEW', 'ON_HOLD'].includes(item.approval?.status || '')).length;
  }

  protected get delayedCount(): number {
    return this.queue.filter((item) => item.request.delayed).length;
  }

  ngOnInit(): void { this.load(); }

  load(): void {
    if (!this.currentUser?.departmentId) {
      this.toast.warning('Department missing', 'This officer account is not linked to a department.');
      return;
    }

    this.loading = true;
    this.requestService.getRequestsByDepartment(this.currentUser.departmentId).pipe(
      switchMap((requests) => {
        const sorted = (requests ?? []).slice().sort((a, b) => new Date(b.createdAt ?? 0).getTime() - new Date(a.createdAt ?? 0).getTime());
        if (!sorted.length) return of([]);
        return forkJoin(sorted.map((request) => this.requestService.getApprovals(request.id).pipe(
          map((approvals) => ({
            request,
            approval: approvals.find((approval) => approval.departmentId === this.currentUser?.departmentId) ?? null
          }))
        )));
      })
    ).subscribe({
      next: (items) => {
        this.queue = items;
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.toast.error('Queue failed to load', error?.error?.message || 'Please try again.');
      }
    });
  }

  canAct(item: QueueItem): boolean {
    const status = item.approval?.status;
    return Boolean(status && ['IN_REVIEW', 'ON_HOLD'].includes(status) && !['COMPLETED', 'REJECTED', 'CANCELLED'].includes(item.request.status));
  }

  openDecision(item: QueueItem, decision: Decision): void {
    this.activeItem = item;
    this.decision = decision;
    this.remarks = this.defaultRemark(decision);
  }

  closeDecision(): void {
    this.activeItem = null;
    this.decision = null;
    this.saving = false;
  }

  saveDecision(remarks: string): void {
    if (!this.activeItem?.approval || !this.decision || !this.currentUser?.departmentId) return;
    this.saving = true;
    const payload = {
      requestId: this.activeItem.request.id,
      departmentId: this.currentUser.departmentId,
      actorUserId: this.currentUser.id,
      remarks
    };
    const call = this.decision === 'approve'
      ? this.requestService.approveRequest(payload)
      : this.decision === 'hold'
        ? this.requestService.holdRequest(payload)
        : this.requestService.rejectRequest(payload);

    call.subscribe({
      next: () => {
        this.toast.success('Decision saved', `${this.activeItem?.request.requestNumber} updated.`);
        this.closeDecision();
        this.load();
      },
      error: (error) => {
        this.saving = false;
        this.toast.error('Decision failed', error?.error?.message || 'Could not update request.');
      }
    });
  }

  private defaultRemark(decision: Decision): string {
    if (decision === 'approve') return 'Approved by department.';
    if (decision === 'hold') return 'Kept on hold pending clarification.';
    return 'Rejected by department.';
  }
}
