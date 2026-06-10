import { AcademicLevel, SectionCode } from './student.models';

export type HomeworkStatus = 'BORRADOR' | 'PUBLICADA' | 'CERRADA';

export interface Homework {
  id: number;
  titulo: string;
  descripcion: string;
  curso: string;
  docente: string;
  nivel: AcademicLevel;
  grado: string;
  seccion: SectionCode;
  fechaPublicacion: string;
  fechaEntrega: string;
  estado: HomeworkStatus;
}

export interface CreateHomeworkRequest {
  titulo: string;
  descripcion: string;
  cursoId: number;
  docenteId?: number;
  nivel: AcademicLevel;
  grado: string;
  seccion: SectionCode;
  fechaPublicacion: string;
  fechaEntrega: string;
  puntajeMaximo?: number;
  tipo?: 'TAREA' | 'PROYECTO' | 'EXAMEN' | 'PARTICIPACION';
  permitirEntregaTardia?: boolean;
}

export interface UpdateHomeworkRequest extends CreateHomeworkRequest {
  estado: HomeworkStatus;
}

export interface UpdateHomeworkStatusRequest {
  estado: HomeworkStatus;
}
