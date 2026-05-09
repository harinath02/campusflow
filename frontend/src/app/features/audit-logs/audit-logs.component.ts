import { DatePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { AuditLog } from '../../core/models/audit-log.model';
import { AuditLogService } from '../../core/services/audit-log.service';
import { ToastService } from '../../shared/toast/toast.service';

@Component({
  selector: 'app-audit-logs',
  imports: [DatePipe],
  templateUrl: './audit-logs.component.html',
  styleUrl: './audit-logs.component.scss'
})
export class AuditLogsComponent implements OnInit {
  private readonly auditLogService = inject(AuditLogService);
  private readonly toast = inject(ToastService);

  protected logs: AuditLog[] = [];
  protected loading = false;
  protected search = '';

  protected get filtered(): AuditLog[] {
    const q = this.search.toLowerCase().trim();
    return q ? this.logs.filter((log) => `${log.actorName} ${log.action} ${log.entityType} ${log.description}`.toLowerCase().includes(q)) : this.logs;
  }

  ngOnInit(): void { this.load(); }

  load(): void {
    this.loading = true;
    this.auditLogService.getAuditLogs().subscribe({
      next: (logs) => {
        this.logs = logs ?? [];
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.toast.error('Audit logs failed to load', error?.error?.message || 'Please try again.');
      }
    });
  }
}
