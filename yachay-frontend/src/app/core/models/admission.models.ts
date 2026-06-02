export type NivelAcademico = 'Inicial' | 'Primaria' | 'Secundaria';

export interface PostulanteRequest {
  apellidoPaterno: string;
  apellidoMaterno: string;
  nombres: string;
  genero: string;
  documentoTipo: string;
  documentoNumero: string;
  fechaNacimiento: string;
  viveCon: string;
  colegioProcedencia: string;
  lugarColegioProcedencia: string;
  referenciaZonaColegio?: string;
  nivel: NivelAcademico;
  grado: string;
}

export interface ApoderadoRequest {
  apellidoPaterno: string;
  apellidoMaterno: string;
  nombres: string;
  genero: string;
  documentoTipo: string;
  documentoNumero: string;
  parentesco: string;
  telefono?: string;
  celular: string;
  correo: string;
  profesion?: string;
  centroTrabajo?: string;
}

export interface AdmissionRequest {
  postulantes: PostulanteRequest[];
  apoderado: ApoderadoRequest;
}

export type EstadoPostulacion = 'PENDIENTE' | 'ACEPTADA' | 'RECHAZADA';

export interface AdmissionResponse {
  id: number;
  estado: EstadoPostulacion;
  fechaRegistro: string;
}

export interface Alumno {
  id: number;
  nombres: string;
  apellidos: string;
  documento: string;
  correoInstitucional: string;
  nivel: NivelAcademico;
  grado: string;
  seccion: string;
  estado: 'ACTIVO' | 'INACTIVO';
}

export interface Apoderado {
  id: number;
  nombres: string;
  apellidos: string;
  documento: string;
  parentesco: string;
  telefono?: string;
  celular: string;
  correoNotificacion: string;
  esContactoPrincipal: boolean;
}

export interface AlumnoApoderado {
  id: number;
  alumnoId: number;
  apoderadoId: number;
  relacion: string;
  recibeNotificaciones: boolean;
}
