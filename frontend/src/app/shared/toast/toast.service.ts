import { Injectable, signal } from '@angular/core';

export type ToastType = 'success' | 'error' | 'info' | 'warning';
export interface Toast { id: number; type: ToastType; title: string; message?: string; }

@Injectable({ providedIn: 'root' })
export class ToastService {
  readonly toasts = signal<Toast[]>([]);
  private id = 0;

  show(type: ToastType, title: string, message = ''): void {
    const toast = { id: ++this.id, type, title, message };
    this.toasts.update((items) => [toast, ...items].slice(0, 4));
    setTimeout(() => this.dismiss(toast.id), 4000);
  }
  success(title: string, message = ''): void { this.show('success', title, message); }
  error(title: string, message = ''): void { this.show('error', title, message); }
  info(title: string, message = ''): void { this.show('info', title, message); }
  warning(title: string, message = ''): void { this.show('warning', title, message); }
  dismiss(id: number): void { this.toasts.update((items) => items.filter((item) => item.id !== id)); }
}
