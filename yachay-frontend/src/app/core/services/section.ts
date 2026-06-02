import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import {
  CreateSchoolSectionRequest,
  SchoolSection,
  UpdateSchoolSectionRequest,
  UpdateSchoolSectionStatusRequest,
} from '../models/section.models';

const API_URL = 'http://localhost:8080/api';

@Injectable({
  providedIn: 'root',
})
export class SectionService {
  private readonly http = inject(HttpClient);

  getSections(): Observable<SchoolSection[]> {
    return this.http.get<SchoolSection[]>(`${API_URL}/admin/secciones`);
  }

  getSection(id: number): Observable<SchoolSection> {
    return this.http.get<SchoolSection>(`${API_URL}/admin/secciones/${id}`);
  }

  createSection(payload: CreateSchoolSectionRequest): Observable<SchoolSection> {
    return this.http.post<SchoolSection>(`${API_URL}/admin/secciones`, payload);
  }

  updateSection(id: number, payload: UpdateSchoolSectionRequest): Observable<SchoolSection> {
    return this.http.put<SchoolSection>(`${API_URL}/admin/secciones/${id}`, payload);
  }

  updateStatus(id: number, payload: UpdateSchoolSectionStatusRequest): Observable<SchoolSection> {
    return this.http.patch<SchoolSection>(`${API_URL}/admin/secciones/${id}/estado`, payload);
  }
}
