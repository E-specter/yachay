export type TeacherStatus = 'ACTIVO' | 'INACTIVO';

export interface Teacher {
  id: number;
  nombres: string;
  apellidos: string;
  documentoTipo: string;
  documentoNumero: string;
  email: string;
  especialidad: string;
  telefono: string;
  estado: TeacherStatus;
  fechaCreacion: string;
}

export interface CreateTeacherRequest {
  nombres: string;
  apellidos: string;
  passwordTemporal?: string;
  codigoEmpleado?: string;
  documentoTipo: string;
  documentoNumero: string;
  email: string;
  especialidad: string;
  telefono: string;
  fechaContratacion?: string;
  activo?: boolean;
}

export interface UpdateTeacherRequest extends CreateTeacherRequest {
  estado: TeacherStatus;
}

export interface UpdateTeacherStatusRequest {
  estado: TeacherStatus;
}
