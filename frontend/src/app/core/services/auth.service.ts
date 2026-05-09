import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, map, tap } from 'rxjs';
import { ROUTE_PATHS, STORAGE_KEYS } from '../constants/app.constants';
import { CreateUserPayload, LoginPayload, LoginResponse, User, UserRole } from '../models/user.model';
import { ApiService } from './api.service';
import { TokenService } from './token.service';
import { UserService } from './user.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = inject(ApiService);
  private readonly userService = inject(UserService);
  private readonly tokenService = inject(TokenService);
  private readonly router = inject(Router);

  login(payload: LoginPayload): Observable<User> {
    return this.api.post<LoginResponse, LoginPayload>('/auth/login', payload).pipe(
      map((response) => this.toUser(response)),
      tap((user) => {
        const token = (user as User & { token?: string }).token;
        if (token) this.tokenService.setToken(token);
        localStorage.setItem(STORAGE_KEYS.user, JSON.stringify(user));
      })
    );
  }

  register(payload: CreateUserPayload): Observable<User> {
    return this.userService.createUser(payload);
  }

  clearSession(): void {
    this.tokenService.clear();
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem(STORAGE_KEYS.user);
    }
  }

  logout(): void {
    this.clearSession();
    this.router.navigate(['/welcome']);
  }

  getToken(): string | null {
    return this.tokenService.getToken();
  }

  getCurrentUser(): User | null {
    if (typeof localStorage === 'undefined') return null;
    const stored = localStorage.getItem(STORAGE_KEYS.user);
    return stored ? JSON.parse(stored) as User : null;
  }

  isAuthenticated(): boolean {
    return Boolean(this.getToken() && this.getCurrentUser());
  }

  getHomeRoute(role: UserRole | null | undefined = this.getCurrentUser()?.role): string {
    if (role === 'ADMIN') return ROUTE_PATHS.adminDashboard;
    if (role === 'OFFICER') return ROUTE_PATHS.departmentDashboard;
    return ROUTE_PATHS.studentDashboard;
  }

  hasAnyRole(roles: string[]): boolean {
    const user = this.getCurrentUser();
    return Boolean(user && roles.includes(user.role));
  }

  private toUser(response: LoginResponse): User & { token?: string } {
    return {
      token: response.token,
      id: response.userId ?? response.id ?? 0,
      name: response.name,
      email: response.email,
      role: response.role,
      departmentId: response.departmentId ?? null,
      department: response.department ?? null,
      branch: response.branch ?? null,
      admissionYear: response.admissionYear ?? null,
      rollNumber: response.rollNumber ?? null
    };
  }
}
