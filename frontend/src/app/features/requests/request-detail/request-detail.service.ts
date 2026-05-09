import { Injectable, inject } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { RequestService } from '../../../core/services/request.service';
import { RequestDetailViewModel } from './request-detail.model';

@Injectable({ providedIn: 'root' })
export class RequestDetailFacadeService {
  private readonly requestService = inject(RequestService);

  getRequestDetail(id: number): Observable<RequestDetailViewModel> {
    return forkJoin({
      request: this.requestService.getRequest(id),
      approvals: this.requestService.getApprovals(id)
    });
  }
}
