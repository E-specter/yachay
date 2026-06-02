export type UserRole = 'ADMINISTRADOR' | 'DOCENTE' | 'ALUMNO';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthUser {
  id: number;
  nombres: string;
  apellidos: string;
  email: string;
  role: UserRole;
}

export interface LoginResponse {
  token: string;
  user: AuthUser;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  password: string;
  confirmPassword: string;
}
