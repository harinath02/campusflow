import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateUserPayload, User } from '../models/user.model';
import { ApiService } from './api.service';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly api = inject(ApiService);

  getUsers(): Observable<User[]> {
    return this.api.get<User[]>('/users/get');
  }

  getUser(id: number): Observable<User> {
    return this.api.get<User>(`/users/${id}`);
  }

  getUsersByRole(roleId: number): Observable<User[]> {
    return this.api.get<User[]>(`/users/by-role/${roleId}`);
  }

  getUsersByDepartment(departmentId: number): Observable<User[]> {
    return this.api.get<User[]>(`/users/by-department/${departmentId}`);
  }

  createUser(payload: CreateUserPayload): Observable<User> {
    return this.api.post<User, CreateUserPayload>('/users/create', payload);
  }
}
