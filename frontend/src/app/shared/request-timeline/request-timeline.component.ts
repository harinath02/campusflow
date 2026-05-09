import { DatePipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Approval } from '../../core/models/request.model';
import { StatusBadgeComponent } from '../status-badge/status-badge.component';
import { getStatusBadgeClass } from '../../core/constants/request-status.constants';

@Component({
  selector: 'app-request-timeline',
  standalone: true,
  imports: [DatePipe, StatusBadgeComponent],
  template: `
    <div class="timeline">
      @for (approval of approvals; track approval.id) {
        <article class="timeline-row" [class]="badgeClass(approval.status)">
          <div class="step-dot">{{ marker(approval.status) }}</div>
          <div class="step-card">
            <div>
              <h4>{{ approval.department }}</h4>
              <app-status-badge [status]="approval.status" />
            </div>
            <p>{{ approval.remarks || 'Waiting for department decision.' }}</p>
            <small>{{ approval.actionAt ? (approval.actionAt | date:'medium') : 'No action yet' }}</small>
          </div>
        </article>
      } @empty {
        <div class="empty-state"><strong>No approval history</strong><p>Department decisions will appear here.</p></div>
      }
    </div>
  `
})
export class RequestTimelineComponent {
  @Input() approvals: Approval[] = [];

  badgeClass(status: string): string {
    return getStatusBadgeClass(status);
  }

  marker(status: string): string {
    const normalized = status?.toUpperCase();
    if (normalized === 'APPROVED' || normalized === 'COMPLETED') return 'OK';
    if (normalized === 'REJECTED') return '!';
    if (normalized === 'ON_HOLD') return '..';
    return '-';
  }
}
