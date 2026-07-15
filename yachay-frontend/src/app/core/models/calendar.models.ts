import { UserRole } from './auth.models';

export type CalendarAudience =
  | 'TODOS'
  | 'ADMINISTRADOR'
  | 'DOCENTE'
  | 'ALUMNO'
  | 'APODERADO';

export type CalendarEventType =
  | 'CLASE'
  | 'CURSO'
  | 'EXAMEN'
  | 'REUNION'
  | 'REUNIÓN'
  | 'FERIADO'
  | 'COMUNICADO'
  | 'TAREA'
  | 'EVALUACION'
  | 'EVALUACIÓN'
  | 'OTRO';

export interface AcademicCalendarEvent {
  id: number;
  title: string;
  description?: string | null;
  startDateTime: string;
  endDateTime: string;
  eventType: CalendarEventType | string;
  courseName?: string | null;
  sectionName?: string | null;
  audience?: CalendarAudience | string | null;
  courseId?: number | null;
  status?: 'ACTIVO' | 'ARCHIVADO';
}

export interface CreateCalendarEventRequest {
  titulo: string;
  descripcion?: string | null;
  fechaInicio: string;
  fechaFin: string;
  tipo: CalendarEventType | string;
  cursoId?: number | null;
  seccion?: string | null;
  publicoObjetivo?: CalendarAudience | string | null;
}

export type CalendarRole = Extract<UserRole, 'ADMINISTRADOR' | 'DOCENTE' | 'ALUMNO'>;
