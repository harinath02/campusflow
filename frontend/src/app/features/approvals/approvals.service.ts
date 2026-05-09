import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApprovalPayload, CampusRequest } from '../../core/models/request.model';
import { RequestService } from '../../core/services/request.service';
import { ApprovalDecision } from './approvals.model';

@Injectable({ providedIn: 'root' })
export class ApprovalsFacadeService {
  private readonly requestService = inject(RequestService);

  getPendingRequests(): Observable<CampusRequest[]> {
    return this.requestService.getPendingRequests();
  }

  decide(action: ApprovalDecision, payload: ApprovalPayload): Observable<string> {
    return action === 'approve'
      ? this.requestService.approveRequest(payload)
      : this.requestService.rejectRequest(payload);
  }
}
