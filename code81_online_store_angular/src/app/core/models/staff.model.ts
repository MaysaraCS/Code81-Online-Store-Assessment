export type Role = 'ADMIN' | 'STORE_MANAGER' | 'SUPPORT_AGENT';

export interface StaffCreateRequest {
  username: string;
  email: string;
  password: string;
  role: Role;
}

export interface StaffUpdateRequest {
  role: Role;
  active: boolean;
}

export interface StaffResponse {
  id: number;
  username: string;
  email: string;
  role: Role;
  active: boolean;
  createdAt: string;
}
