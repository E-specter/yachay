import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../config/api.config';
import {
  AcademicCalendarEvent,
  CalendarRole,
  CreateCalendarEventRequest,
} from '../models/calendar.models';

@Injectable({
  providedIn: 'root',
})
export class CalendarService {
  private readonly http = inject(HttpClient);

  list(role: CalendarRole): Observable<AcademicCalendarEvent[]> {
    return this.http.get<AcademicCalendarEvent[]>(
      `${API_URL}/${this.rolePath(role)}/calendario`,
    );
  }

  week(role: CalendarRole): Observable<AcademicCalendarEvent[]> {
    return this.http.get<AcademicCalendarEvent[]>(
      `${API_URL}/${this.rolePath(role)}/calendario/semana`,
    );
  }

  month(role: CalendarRole): Observable<AcademicCalendarEvent[]> {
    return this.http.get<AcademicCalendarEvent[]>(
      `${API_URL}/${this.rolePath(role)}/calendario/mes`,
    );
  }

  createAdminEvent(
    payload: CreateCalendarEventRequest,
  ): Observable<AcademicCalendarEvent> {
    return this.http.post<AcademicCalendarEvent>(
      `${API_URL}/admin/calendario`,
      payload,
    );
  }

  private rolePath(role: CalendarRole): string {
    if (role === 'ADMINISTRADOR') return 'admin';
    if (role === 'DOCENTE') return 'docente';
    return 'alumno';
  }
}
