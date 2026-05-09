import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiBaseUrl;

  get<T>(path: string): Observable<T> {
    return this.http.get<ApiResponse<T> | T>(`${this.baseUrl}${path}`).pipe(map((response) => this.unwrap<T>(response)));
  }

  post<T, B = unknown>(path: string, body: B): Observable<T> {
    return this.http.post<ApiResponse<T> | T>(`${this.baseUrl}${path}`, body).pipe(map((response) => this.unwrap<T>(response)));
  }

  put<T, B = unknown>(path: string, body: B): Observable<T> {
    return this.http.put<ApiResponse<T> | T>(`${this.baseUrl}${path}`, body).pipe(map((response) => this.unwrap<T>(response)));
  }

  delete<T>(path: string): Observable<T> {
    return this.http.delete<ApiResponse<T> | T>(`${this.baseUrl}${path}`).pipe(map((response) => this.unwrap<T>(response)));
  }

  private unwrap<T>(response: ApiResponse<T> | T): T {
    if (response && typeof response === 'object' && 'data' in response) {
      return (response as ApiResponse<T>).data;
    }

    return response as T;
  }
}
