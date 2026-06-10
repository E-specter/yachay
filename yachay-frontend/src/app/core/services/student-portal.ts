import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class StudentPortalService {
  private readonly http = inject(HttpClient);

  getCourses<T>(): Observable<T> {
    return this.http.get<T>(`${API_URL}/alumno/cursos`);
  }

  getHomeworks<T>(): Observable<T> {
    return this.http.get<T>(`${API_URL}/alumno/tareas`);
  }

  submitHomework<T>(id: number, payload: unknown): Observable<T> {
    return this.http.post<T>(`${API_URL}/alumno/tareas/${id}/entrega`, payload);
  }

  getGrades<T>(): Observable<T> {
    return this.http.get<T>(`${API_URL}/alumno/notas`);
  }

  getAnnouncements<T>(): Observable<T> {
    return this.http.get<T>(`${API_URL}/alumno/comunicados`);
  }

  markAnnouncementAsRead<T>(id: number): Observable<T> {
    return this.http.patch<T>(`${API_URL}/alumno/comunicados/${id}/leido`, {});
  }

  getProfile<T>(): Observable<T> {
    return this.http.get<T>(`${API_URL}/alumno/perfil`);
  }
}
