import { AcademicLevel, SectionCode } from './student.models';

export type AnnouncementRecipient = 'TODOS' | 'ALUMNOS' | 'APODERADOS' | 'DOCENTES';
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
  destinatario: AnnouncementRecipient;
  nivel?: AcademicLevel;
  grado?: string;
  seccion?: SectionCode;
  fechaPublicacion: string;
}

export interface UpdateAnnouncementRequest extends CreateAnnouncementRequest {
  estado: AnnouncementStatus;
}

export interface UpdateAnnouncementStatusRequest {
  estado: AnnouncementStatus;
}
