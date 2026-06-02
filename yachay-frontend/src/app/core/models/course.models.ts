import { AcademicLevel } from './student.models';

export type CourseStatus = 'ACTIVO' | 'INACTIVO';
export type CourseArea =
  | 'Matemática'
  | 'Comunicación'
  | 'Ciencia y Tecnología'
  | 'Ciencias Sociales'
  | 'Inglés'
  | 'Educación Física'
  | 'Arte'
  | 'Religión';

export interface Course {
  id: number;
  nombre: string;
  codigo: string;
  nivel: AcademicLevel;
  grado: string;
  area: CourseArea;
  docenteAsignado: string;
  estado: CourseStatus;
}

export interface CreateCourseRequest {
  nombre: string;
  codigo: string;
  nivel: AcademicLevel;
  grado: string;
  area: CourseArea;
  docenteId?: number;
}

export interface UpdateCourseRequest extends CreateCourseRequest {
  estado: CourseStatus;
}

export interface UpdateCourseStatusRequest {
  estado: CourseStatus;
}
