import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import {
  CreateStudentRequest,
  Student,
  UpdateStudentRequest,
  UpdateStudentStatusRequest,
} from '../models/student.models';

const API_URL = 'http://localhost:8080/api';

@Injectable({
  providedIn: 'root',
})
export class StudentService {
  private readonly http = inject(HttpClient);

  getStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(`${API_URL}/admin/alumnos`);
  }

  getStudent(id: number): Observable<Student> {
    return this.http.get<Student>(`${API_URL}/admin/alumnos/${id}`);
  }

  createStudent(payload: CreateStudentRequest): Observable<Student> {
    return this.http.post<Student>(`${API_URL}/admin/alumnos`, payload);
  }

  updateStudent(id: number, payload: UpdateStudentRequest): Observable<Student> {
    return this.http.put<Student>(`${API_URL}/admin/alumnos/${id}`, payload);
  }

  updateStatus(id: number, payload: UpdateStudentStatusRequest): Observable<Student> {
    return this.http.patch<Student>(`${API_URL}/admin/alumnos/${id}/estado`, payload);
  }
}
