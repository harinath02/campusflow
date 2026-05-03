import { Injectable } from '@angular/core';
import { STORAGE_KEYS } from '../constants/app.constants';

@Injectable({ providedIn: 'root' })
export class TokenService {
  getToken(): string | null {
    if (typeof localStorage === 'undefined') return null;
    return localStorage.getItem(STORAGE_KEYS.token);
  }

  setToken(token: string): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem(STORAGE_KEYS.token, token);
    }
  }

  clear(): void {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem(STORAGE_KEYS.token);
    }
  }
}
