import { Routes } from '@angular/router';
import { authGuard, guestGuard, roleGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'welcome', canActivate: [guestGuard], loadComponent: () => import('./features/auth/portal/portal.component').then((m) => m.PortalComponent) },
  { path: 'login', canActivate: [guestGuard], loadComponent: () => import('./features/auth/login/login.component').then((m) => m.LoginComponent) },
  { path: 'register', canActivate: [guestGuard], loadComponent: () => import('./features/auth/register/register.component').then((m) => m.RegisterComponent) },
  {
    path: 'student',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['STUDENT'] },
    loadComponent: () => import('./layout/app-layout.component').then((m) => m.AppLayoutComponent),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.component').then((m) => m.DashboardComponent) },
      { path: 'requests', loadComponent: () => import('./features/requests/request-list/request-list.component').then((m) => m.RequestListComponent) },
      { path: 'requests/new', loadComponent: () => import('./features/requests/request-create/request-create.component').then((m) => m.RequestCreateComponent) },
      { path: 'requests/:id', loadComponent: () => import('./features/requests/request-detail/request-detail.component').then((m) => m.RequestDetailComponent) }
    ]
  },
  {
    path: 'department',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['OFFICER'] },
    loadComponent: () => import('./layout/app-layout.component').then((m) => m.AppLayoutComponent),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.component').then((m) => m.DashboardComponent) },
      { path: 'queue', loadComponent: () => import('./features/approvals/approvals.component').then((m) => m.ApprovalsComponent) },
      { path: 'requests/:id', loadComponent: () => import('./features/requests/request-detail/request-detail.component').then((m) => m.RequestDetailComponent) }
    ]
  },
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN'] },
    loadComponent: () => import('./layout/app-layout.component').then((m) => m.AppLayoutComponent),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.component').then((m) => m.DashboardComponent) },
      { path: 'departments', loadComponent: () => import('./features/departments/departments.component').then((m) => m.DepartmentsComponent) },
      { path: 'users', loadComponent: () => import('./features/users/users.component').then((m) => m.UsersComponent) },
      { path: 'request-types', loadComponent: () => import('./features/request-types/request-types.component').then((m) => m.RequestTypesComponent) },
      { path: 'audit-logs', loadComponent: () => import('./features/audit-logs/audit-logs.component').then((m) => m.AuditLogsComponent) }
    ]
  },
  { path: 'dashboard', redirectTo: 'student/dashboard' },
  { path: 'requests/create', redirectTo: 'student/requests/new' },
  { path: 'requests', canActivate: [authGuard], loadComponent: () => import('./features/requests/request-list/request-list.component').then((m) => m.RequestListComponent) },
  { path: 'requests/:id', canActivate: [authGuard], loadComponent: () => import('./features/requests/request-detail/request-detail.component').then((m) => m.RequestDetailComponent) },
  { path: 'approvals', redirectTo: 'department/queue' },
  { path: 'departments', redirectTo: 'admin/departments' },
  { path: 'users', redirectTo: 'admin/users' },
  { path: 'request-types', redirectTo: 'admin/request-types' },
  { path: '', pathMatch: 'full', redirectTo: 'welcome' },
  { path: '**', redirectTo: 'welcome' }
];
