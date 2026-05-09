export type UserRole = 'STUDENT' | 'OFFICER' | 'ADMIN' | string;

export interface User {
  id: number;
  name: string;
  email: string;
  role: UserRole;
  departmentId?: number | null;
  department?: string | null;
  branch?: string | null;
  admissionYear?: number | null;
  rollNumber?: string | null;
}

export interface LoginResponse {
  token: string;
  userId: number;
  id?: number;
  name: string;
  email: string;
  role: UserRole;
  departmentId?: number | null;
  department?: string | null;
  branch?: string | null;
  admissionYear?: number | null;
  rollNumber?: string | null;
}

export interface CreateUserPayload {
  name: string;
  email: string;
  password: string;
  roleId: number;
  departmentId?: number | null;
  branch?: string | null;
  admissionYear?: number | null;
  rollNumber?: string | null;
}

export interface LoginPayload {
  email: string;
  password: string;
}
