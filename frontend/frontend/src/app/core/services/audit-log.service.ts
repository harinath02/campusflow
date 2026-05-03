import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { AuditLog } from '../models/audit-log.model';
import { ApiService } from './api.service';

@Injectable({ providedIn: 'root' })
export class AuditLogService {
  private readonly api = inject(ApiService);

  getAuditLogs(): Observable<AuditLog[]> {
    return this.api.get<AuditLog[]>('/audit-logs');
  }
}
