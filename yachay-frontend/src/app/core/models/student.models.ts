export type StudentStatus = 'ACTIVO' | 'INACTIVO' | 'RETIRADO';
export type AcademicLevel = 'Inicial' | 'Primaria' | 'Secundaria';
export type SectionCode = 'A' | 'B' | 'C' | 'D' | 'E' | 'F';

export interface Student {
  id: number;
  codigo: string;
  nombres: string;
  apellidos: string;
  documentoTipo: string;
  documentoNumero: string;
  correoInstitucional: string;
  nivel: AcademicLevel;
  grado: string;
  seccion: SectionCode;
  estado: StudentStatus;
  apoderado: string;
  correoApoderado: string;
}

export interface CreateStudentRequest {
  codigo: string;
  nombres: string;
  apellidos: string;
  documentoTipo: string;
  documentoNumero: string;
  correoInstitucional: string;
  nivel: AcademicLevel;
  grado: string;
  seccion: SectionCode;
  apoderadoId?: number;
}

export interface UpdateStudentRequest extends CreateStudentRequest {
  estado: StudentStatus;
}

export interface UpdateStudentStatusRequest {
  estado: StudentStatus;
}
