export const APP_NAME = 'CampusFlow';

export const STORAGE_KEYS = {
  token: 'campusflow_token',
  user: 'campusflow_user'
} as const;

export const ROUTE_PATHS = {
  login: '/login',
  register: '/register',
  studentDashboard: '/student/dashboard',
  departmentDashboard: '/department/dashboard',
  adminDashboard: '/admin/dashboard',
  dashboard: '/student/dashboard',
  users: '/admin/users',
  departments: '/admin/departments',
  requests: '/student/requests',
  approvals: '/department/queue'
} as const;
