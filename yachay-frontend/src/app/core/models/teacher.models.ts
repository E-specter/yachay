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
  documentoTipo: string;
  documentoNumero: string;
  email: string;
  especialidad: string;
  telefono: string;
}

export interface UpdateTeacherRequest extends CreateTeacherRequest {
  estado: TeacherStatus;
}

export interface UpdateTeacherStatusRequest {
  estado: TeacherStatus;
}
