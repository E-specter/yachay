import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { StudentDashboard } from '../models/student-dashboard.models';

import { API_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class StudentDashboardService {
  private readonly http = inject(HttpClient);

  getDashboard(): Observable<StudentDashboard> {
    return this.http.get<StudentDashboard>(`${API_URL}/alumno/dashboard`);
  }
}
