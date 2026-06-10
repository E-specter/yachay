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
  seccion?: string;
  area: CourseArea;
  materia?: string;
  materiaId?: number;
  docenteId?: number;
  anioAcademicoId?: number;
  aula?: string;
  maximoEstudiantes?: number;
  activo?: boolean;
}

export interface SubjectOption {
  id: number;
  codigo: string;
  nombre: string;
  area: string;
}

export interface AcademicYearOption {
  id: number;
  anio: number;
  activo: boolean;
}

export interface UpdateCourseRequest extends CreateCourseRequest {
  estado: CourseStatus;
}

export interface UpdateCourseStatusRequest {
  estado: CourseStatus;
}
