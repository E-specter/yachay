export type AdminUserRole = 'ADMINISTRADOR' | 'DOCENTE' | 'ALUMNO';
export type AdminUserStatus = 'ACTIVO' | 'INACTIVO';

export interface AdminUser {
  id: number;
  nombres: string;
  apellidos: string;
  email: string;
  rol: AdminUserRole;
  estado: AdminUserStatus;
  fechaCreacion: string;
}

export interface CreateAdminUserRequest {
  nombres: string;
  apellidos: string;
  email: string;
  rol: AdminUserRole;
}

export interface UpdateAdminUserRequest extends CreateAdminUserRequest {
  estado: AdminUserStatus;
}

export interface UpdateAdminUserStatusRequest {
  estado: AdminUserStatus;
}

export interface ResetAdminUserPasswordResponse {
  temporaryPassword: string;
}
