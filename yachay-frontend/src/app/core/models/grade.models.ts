export type AcademicTerm = 'I' | 'II' | 'III' | 'IV';
export type GradeStatus = 'REGISTRADA' | 'OBSERVADA' | 'ANULADA';

export interface GradeRecord {
  id: number;
  alumno: string;
  curso: string;
  docente: string;
  bimestre: AcademicTerm;
  nota: number;
  fechaRegistro: string;
  estado: GradeStatus;
}

export interface CreateGradeRecordRequest {
  alumnoId: number;
  cursoId: number;
  docenteId: number;
  bimestre: AcademicTerm;
  nota: number;
}

export interface UpdateGradeRecordRequest extends CreateGradeRecordRequest {
  estado: GradeStatus;
}

export interface UpdateGradeRecordStatusRequest {
  estado: GradeStatus;
}
