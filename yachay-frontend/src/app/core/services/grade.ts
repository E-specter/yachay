import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import {
  CreateGradeRecordRequest,
  GradeRecord,
  UpdateGradeRecordRequest,
  UpdateGradeRecordStatusRequest,
} from '../models/grade.models';

import { API_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class GradeService {
  private readonly http = inject(HttpClient);

  list(): Observable<GradeRecord[]> {
    return this.getGrades();
  }

  getGrades(): Observable<GradeRecord[]> {
    return this.http.get<GradeRecord[]>(`${API_URL}/admin/notas`);
  }

  getGrade(id: number): Observable<GradeRecord> {
    return this.http.get<GradeRecord>(`${API_URL}/admin/notas/${id}`);
  }

  createGrade(payload: CreateGradeRecordRequest): Observable<GradeRecord> {
    return this.http.post<GradeRecord>(`${API_URL}/admin/notas`, payload);
  }

  updateGrade(id: number, payload: UpdateGradeRecordRequest): Observable<GradeRecord> {
    return this.http.put<GradeRecord>(`${API_URL}/admin/notas/${id}`, payload);
  }

  updateStatus(id: number, payload: UpdateGradeRecordStatusRequest): Observable<GradeRecord> {
    return this.http.patch<GradeRecord>(`${API_URL}/admin/notas/${id}/estado`, payload);
  }
}
