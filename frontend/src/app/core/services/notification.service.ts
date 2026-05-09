import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Notification } from '../models/notification.model';
import { ApiService } from './api.service';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly api = inject(ApiService);

  getNotifications(userId: number): Observable<Notification[]> {
    return this.api.get<Notification[]>(`/notifications/user/${userId}`);
  }

  markAsRead(id: number): Observable<Notification> {
    return this.api.put<Notification, Record<string, never>>(`/notifications/${id}/read`, {});
  }

  markAllAsRead(userId: number): Observable<string> {
    return this.api.put<string, Record<string, never>>(`/notifications/user/${userId}/read-all`, {});
  }
}
