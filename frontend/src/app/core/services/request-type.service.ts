import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { RequestType } from '../models/request.model';
import { ApiService } from './api.service';

export type RequestTypePayload = Omit<RequestType, 'id' | 'active'>;

@Injectable({ providedIn: 'root' })
export class RequestTypeService {
  private readonly api = inject(ApiService);

  getRequestTypes(): Observable<RequestType[]> {
    return this.api.get<RequestType[]>('/request-types/get');
  }

  createRequestType(payload: RequestTypePayload): Observable<RequestType> {
    return this.api.post<RequestType, RequestTypePayload>('/request-types/create', payload);
  }

  updateRequestType(id: number, payload: RequestTypePayload): Observable<RequestType> {
    return this.api.put<RequestType, RequestTypePayload>(`/request-types/${id}`, payload);
  }

  deactivateRequestType(id: number): Observable<string> {
    return this.api.delete<string>(`/request-types/${id}`);
  }
}
