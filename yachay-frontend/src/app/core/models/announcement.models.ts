import { AcademicLevel, SectionCode } from './student.models';

export type AnnouncementRecipient = 'TODOS' | 'ADMINISTRADOR' | 'ALUMNOS' | 'APODERADOS' | 'DOCENTES';
export type AnnouncementStatus = 'BORRADOR' | 'PUBLICADO' | 'ARCHIVADO';

export interface Announcement {
  id: number;
  titulo: string;
  contenido: string;
  destinatario: AnnouncementRecipient;
  nivel?: AcademicLevel;
  grado?: string;
  seccion?: SectionCode;
  fechaPublicacion: string;
  estado: AnnouncementStatus;
}

export interface CreateAnnouncementRequest {
  titulo: string;
  contenido: string;
  cuerpo?: string;
  destinatario: AnnouncementRecipient;
  publicoObjetivo?: AnnouncementRecipient;
  nivel?: AcademicLevel;
  grado?: string;
  seccion?: SectionCode;
  fechaPublicacion: string;
  fechaExpiracion?: string;
  fijado?: boolean;
  autorId?: number;
  cursoId?: number;
}

export interface UpdateAnnouncementRequest extends CreateAnnouncementRequest {
  estado: AnnouncementStatus;
}

export interface UpdateAnnouncementStatusRequest {
  estado: AnnouncementStatus;
}
