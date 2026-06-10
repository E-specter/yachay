import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import {
  CreateTeacherRequest,
  Teacher,
  UpdateTeacherRequest,
  UpdateTeacherStatusRequest,
} from '../models/teacher.models';

import { API_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class TeacherService {
  private readonly http = inject(HttpClient);

  list(): Observable<Teacher[]> {
    return this.getTeachers();
  }

  getTeachers(): Observable<Teacher[]> {
    return this.http.get<Teacher[]>(`${API_URL}/admin/docentes`);
  }

  getTeacher(id: number): Observable<Teacher> {
    return this.http.get<Teacher>(`${API_URL}/admin/docentes/${id}`);
  }

  createTeacher(payload: CreateTeacherRequest): Observable<Teacher> {
    return this.http.post<Teacher>(`${API_URL}/admin/docentes`, payload);
  }

  updateTeacher(id: number, payload: UpdateTeacherRequest): Observable<Teacher> {
    return this.http.put<Teacher>(`${API_URL}/admin/docentes/${id}`, payload);
  }

  updateStatus(id: number, payload: UpdateTeacherStatusRequest): Observable<Teacher> {
    return this.http.patch<Teacher>(`${API_URL}/admin/docentes/${id}/estado`, payload);
  }
}
