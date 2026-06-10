import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import {
  CreateHomeworkRequest,
  Homework,
  UpdateHomeworkRequest,
  UpdateHomeworkStatusRequest,
} from '../models/homework.models';

import { API_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class HomeworkService {
  private readonly http = inject(HttpClient);

  list(): Observable<Homework[]> {
    return this.getHomeworks();
  }

  getHomeworks(): Observable<Homework[]> {
    return this.http.get<Homework[]>(`${API_URL}/admin/tareas`);
  }

  getHomework(id: number): Observable<Homework> {
    return this.http.get<Homework>(`${API_URL}/admin/tareas/${id}`);
  }

  createHomework(payload: CreateHomeworkRequest): Observable<Homework> {
    return this.http.post<Homework>(`${API_URL}/admin/tareas`, payload);
  }

  updateHomework(id: number, payload: UpdateHomeworkRequest): Observable<Homework> {
    return this.http.put<Homework>(`${API_URL}/admin/tareas/${id}`, payload);
  }

  updateStatus(id: number, payload: UpdateHomeworkStatusRequest): Observable<Homework> {
    return this.http.patch<Homework>(`${API_URL}/admin/tareas/${id}/estado`, payload);
  }
}
