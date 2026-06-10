import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { TeacherDashboard } from '../models/teacher-dashboard.models';

import { API_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class TeacherDashboardService {
  private readonly http = inject(HttpClient);

  getDashboard(): Observable<TeacherDashboard> {
    return this.http.get<TeacherDashboard>(`${API_URL}/docente/dashboard`);
  }
}
