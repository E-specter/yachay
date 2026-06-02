import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api';

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

  getGrades<T>(): Observable<T> {
    return this.http.get<T>(`${API_URL}/docente/notas`);
  }

  getAnnouncements<T>(): Observable<T> {
    return this.http.get<T>(`${API_URL}/docente/comunicados`);
  }

  getProfile<T>(): Observable<T> {
    return this.http.get<T>(`${API_URL}/docente/perfil`);
  }
}
