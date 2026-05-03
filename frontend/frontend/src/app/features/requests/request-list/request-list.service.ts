import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CampusRequest, CreateCampusRequestPayload, RequestType } from '../../../core/models/request.model';
import { RequestService } from '../../../core/services/request.service';

@Injectable({ providedIn: 'root' })
export class RequestListFacadeService {
  private readonly requestService = inject(RequestService);

  getRequests(): Observable<CampusRequest[]> {
    return this.requestService.getRequests();
  }

  getRequestTypes(): Observable<RequestType[]> {
    return this.requestService.getRequestTypes();
  }

  createRequest(payload: CreateCampusRequestPayload): Observable<CampusRequest> {
    return this.requestService.createRequest(payload);
  }
}
