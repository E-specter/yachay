import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class TeacherPortalService {
  private readonly http = inject(HttpClient);

  getCourses<T>(): Observable<T> {
    return this.http.get<T>(`${API_URL}/docente/cursos`);
  }

  getStudents<T>(): Observable<T> {
    return this.http.get<T>(`${API_URL}/docente/alumnos`);
  }

  getHomeworks<T>(): Observable<T> {
    return this.http.get<T>(`${API_URL}/docente/tareas`);
  }

  createHomework<T>(payload: unknown): Observable<T> {
    return this.http.post<T>(`${API_URL}/docente/tareas`, payload);
  }

  updateHomework<T>(id: number, payload: unknown): Observable<T> {
    return this.http.put<T>(`${API_URL}/docente/tareas/${id}`, payload);
  }

  updateHomeworkStatus<T>(id: number, estado: string): Observable<T> {
    return this.http.patch<T>(`${API_URL}/docente/tareas/${id}/estado`, { estado });
  }

  getHomeworkSubmissions<T>(id: number): Observable<T> {
    return this.http.get<T>(`${API_URL}/docente/tareas/${id}/entregas`);
  }

  getGrades<T>(): Observable<T> {
    return this.http.get<T>(`${API_URL}/docente/notas`);
  }

  createGrade<T>(payload: unknown): Observable<T> {
    return this.http.post<T>(`${API_URL}/docente/notas`, payload);
  }

  updateGrade<T>(id: number, payload: unknown): Observable<T> {
    return this.http.put<T>(`${API_URL}/docente/notas/${id}`, payload);
  }

  updateGradeStatus<T>(id: number, estado: string): Observable<T> {
    return this.http.patch<T>(`${API_URL}/docente/notas/${id}/estado`, { estado });
  }

  getAnnouncements<T>(): Observable<T> {
    return this.http.get<T>(`${API_URL}/docente/comunicados`);
  }

  createAnnouncement<T>(payload: unknown): Observable<T> {
    return this.http.post<T>(`${API_URL}/docente/comunicados`, payload);
  }

  updateAnnouncement<T>(id: number, payload: unknown): Observable<T> {
    return this.http.put<T>(`${API_URL}/docente/comunicados/${id}`, payload);
  }

  updateAnnouncementStatus<T>(id: number, estado: string): Observable<T> {
    return this.http.patch<T>(`${API_URL}/docente/comunicados/${id}/estado`, { estado });
  }

  getProfile<T>(): Observable<T> {
    return this.http.get<T>(`${API_URL}/docente/perfil`);
  }
}
