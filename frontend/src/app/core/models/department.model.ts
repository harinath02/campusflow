export interface Department {
  id: number;
  name: string;
  code: string;
  description?: string;
}

export interface DepartmentPayload {
  name: string;
  code: string;
  description?: string;
}
