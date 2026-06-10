import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import {
  AcademicYearOption,
  Course,
  CreateCourseRequest,
  SubjectOption,
  UpdateCourseRequest,
  UpdateCourseStatusRequest,
} from '../models/course.models';

import { API_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class CourseService {
  private readonly http = inject(HttpClient);

  list(): Observable<Course[]> {
    return this.getCourses();
  }

  getCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(`${API_URL}/admin/cursos`);
  }

  getSubjects(): Observable<SubjectOption[]> {
    return this.http.get<SubjectOption[]>(`${API_URL}/admin/materias`);
  }

  getAcademicYears(): Observable<AcademicYearOption[]> {
    return this.http.get<AcademicYearOption[]>(`${API_URL}/admin/anios-academicos`);
  }

  getCourse(id: number): Observable<Course> {
    return this.http.get<Course>(`${API_URL}/admin/cursos/${id}`);
  }

  createCourse(payload: CreateCourseRequest): Observable<Course> {
    return this.http.post<Course>(`${API_URL}/admin/cursos`, payload);
  }

  updateCourse(id: number, payload: UpdateCourseRequest): Observable<Course> {
    return this.http.put<Course>(`${API_URL}/admin/cursos/${id}`, payload);
  }

  updateStatus(id: number, payload: UpdateCourseStatusRequest): Observable<Course> {
    return this.http.patch<Course>(`${API_URL}/admin/cursos/${id}/estado`, payload);
  }
}
