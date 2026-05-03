import { Component, HostListener, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { Notification } from '../core/models/notification.model';
import { User } from '../core/models/user.model';
import { AuthService } from '../core/services/auth.service';
import { NotificationService } from '../core/services/notification.service';

interface NavItem { label: string; path: string; icon: string; }

@Component({
  selector: 'app-layout',
  imports: [DatePipe, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app-layout.component.html',
  styleUrl: './app-layout.component.scss'
})
export class AppLayoutComponent implements OnInit {
  protected readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);

  protected readonly currentUser: User | null = this.authService.getCurrentUser();
  protected showNotifications = false;
  protected showProfile = false;
  protected notifications: Notification[] = [];
  protected notificationsLoading = true;

  protected get navItems(): NavItem[] {
    if (this.currentUser?.role === 'ADMIN') {
      return [
        { label: 'Dashboard', path: '/admin/dashboard', icon: 'D' },
        { label: 'Request Queue', path: '/requests', icon: 'Q' },
        { label: 'Departments', path: '/admin/departments', icon: 'B' },
        { label: 'Users', path: '/admin/users', icon: 'U' },
        { label: 'Request Types', path: '/admin/request-types', icon: 'T' },
        { label: 'Audit Logs', path: '/admin/audit-logs', icon: 'A' }
      ];
    }

    if (this.currentUser?.role === 'OFFICER') {
      return [
        { label: 'Dashboard', path: '/department/dashboard', icon: 'D' },
        { label: 'Queue', path: '/department/queue', icon: 'Q' }
      ];
    }

    return [
      { label: 'Dashboard', path: '/student/dashboard', icon: 'D' },
      { label: 'My Requests', path: '/student/requests', icon: 'R' },
      { label: 'Create Request', path: '/student/requests/new', icon: '+' }
    ];
  }

  protected get unreadCount(): number {
    return this.notifications.filter((item) => !item.read).length;
  }

  protected get initials(): string {
    const name = this.currentUser?.name || 'User';
    return name.split(' ').map((part) => part[0]).join('').slice(0, 2).toUpperCase();
  }

  protected get roleLabel(): string {
    if (this.currentUser?.role === 'OFFICER') return 'Department Officer';
    return this.currentUser?.role || 'Student';
  }

  protected get workspaceTitle(): string {
    if (this.currentUser?.role === 'ADMIN') return 'System Management';
    if (this.currentUser?.role === 'OFFICER') return `${this.currentUser?.department || 'Department'} Workflow`;
    return 'Student Self Service';
  }

  ngOnInit(): void {
    this.loadNotifications();
  }

  toggleNotifications(event: MouseEvent): void {
    event.stopPropagation();
    this.showNotifications = !this.showNotifications;
    this.showProfile = false;
    if (this.showNotifications) this.loadNotifications();
  }

  toggleProfile(event: MouseEvent): void {
    event.stopPropagation();
    this.showProfile = !this.showProfile;
    this.showNotifications = false;
  }

  createQuickRequest(): void {
    this.router.navigate(['/student/requests/new']);
  }

  markAllRead(): void {
    if (!this.currentUser) return;
    this.notificationService.markAllAsRead(this.currentUser.id).subscribe({
      next: () => this.loadNotifications(),
      error: () => this.notifications = this.notifications.map((item) => ({ ...item, read: true }))
    });
  }

  markRead(notification: Notification): void {
    if (notification.read) return;
    this.notificationService.markAsRead(notification.id).subscribe({
      next: () => notification.read = true,
      error: () => notification.read = true
    });
  }

  logout(): void {
    this.authService.logout();
  }

  notificationTone(notification: Notification): string {
    const text = `${notification.title} ${notification.message}`.toUpperCase();
    if (text.includes('REJECT')) return 'red';
    if (text.includes('HOLD')) return 'amber';
    if (text.includes('APPROV') || text.includes('COMPLET')) return 'green';
    return 'blue';
  }

  private loadNotifications(): void {
    if (!this.currentUser) {
      this.notificationsLoading = false;
      return;
    }
    this.notificationsLoading = true;
    this.notificationService.getNotifications(this.currentUser.id).subscribe({
      next: (items) => {
        this.notifications = items ?? [];
        this.notificationsLoading = false;
      },
      error: () => {
        this.notifications = [];
        this.notificationsLoading = false;
      }
    });
  }

  @HostListener('document:click')
  closeMenus(): void {
    this.showNotifications = false;
    this.showProfile = false;
  }
}
