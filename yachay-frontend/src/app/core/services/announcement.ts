import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import {
  Announcement,
  CreateAnnouncementRequest,
  UpdateAnnouncementRequest,
  UpdateAnnouncementStatusRequest,
} from '../models/announcement.models';

const API_URL = 'http://localhost:8080/api';

@Injectable({
  providedIn: 'root',
})
export class AnnouncementService {
  private readonly http = inject(HttpClient);

  getAnnouncements(): Observable<Announcement[]> {
    return this.http.get<Announcement[]>(`${API_URL}/admin/comunicados`);
  }

  getAnnouncement(id: number): Observable<Announcement> {
    return this.http.get<Announcement>(`${API_URL}/admin/comunicados/${id}`);
  }

  createAnnouncement(payload: CreateAnnouncementRequest): Observable<Announcement> {
    return this.http.post<Announcement>(`${API_URL}/admin/comunicados`, payload);
  }

  updateAnnouncement(id: number, payload: UpdateAnnouncementRequest): Observable<Announcement> {
    return this.http.put<Announcement>(`${API_URL}/admin/comunicados/${id}`, payload);
  }

  updateStatus(id: number, payload: UpdateAnnouncementStatusRequest): Observable<Announcement> {
    return this.http.patch<Announcement>(`${API_URL}/admin/comunicados/${id}/estado`, payload);
  }
}
