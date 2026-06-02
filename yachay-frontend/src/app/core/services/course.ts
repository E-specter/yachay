import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import {
  Course,
  CreateCourseRequest,
  UpdateCourseRequest,
  UpdateCourseStatusRequest,
} from '../models/course.models';

const API_URL = 'http://localhost:8080/api';

@Injectable({
  providedIn: 'root',
})
export class CourseService {
  private readonly http = inject(HttpClient);

  getCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(`${API_URL}/admin/cursos`);
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
