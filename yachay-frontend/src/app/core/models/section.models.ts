import { AcademicLevel, SectionCode } from './student.models';

export type SchoolSectionStatus = 'ACTIVO' | 'INACTIVO';

export interface SchoolSection {
  id: number;
  nivel: AcademicLevel;
  grado: string;
  seccion: SectionCode;
  tutor: string;
  capacidad: number;
  matriculados: number;
  estado: SchoolSectionStatus;
}

export interface CreateSchoolSectionRequest {
  nivel: AcademicLevel;
  grado: string;
  seccion: SectionCode;
  nombre?: SectionCode;
  tutorId?: number;
  aula?: string;
  anioAcademicoId?: number;
  capacidad: number;
  activo?: boolean;
}

export interface UpdateSchoolSectionRequest extends CreateSchoolSectionRequest {
  estado: SchoolSectionStatus;
}

export interface UpdateSchoolSectionStatusRequest {
  estado: SchoolSectionStatus;
}
