import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../config/api.config';
import { UserRole } from '../models/auth.models';

export interface NotificationResponse {
  success: boolean;
  message: string;
}

export interface EmailTestPayload {
  to: string;
  subject: string;
  message: string;
}

export interface WhatsappTestPayload {
  to: string;
  message: string;
}

export interface UserNotification {
  id: number;
  title: string;
  body: string;
  type: string;
  linkUrl?: string | null;
  read: boolean;
  createdAt: string;
  readAt?: string | null;
}

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private readonly http = inject(HttpClient);

  list(role: UserRole): Observable<UserNotification[]> {
    return this.http.get<UserNotification[]>(
      `${API_URL}/${this.rolePath(role)}/notificaciones`,
    );
  }

  markAsRead(role: UserRole, id: number): Observable<UserNotification> {
    return this.http.patch<UserNotification>(
      `${API_URL}/${this.rolePath(role)}/notificaciones/${id}/leido`,
      {},
    );
  }

  markAllAsRead(role: UserRole): Observable<UserNotification[]> {
    return this.http.patch<UserNotification[]>(
      `${API_URL}/${this.rolePath(role)}/notificaciones/leidas`,
      {},
    );
  }

  sendTestEmail(payload: EmailTestPayload): Observable<NotificationResponse> {
    return this.http.post<NotificationResponse>(
      `${API_URL}/admin/notificaciones/email/test`,
      payload,
    );
  }

  sendTestWhatsapp(payload: WhatsappTestPayload): Observable<NotificationResponse> {
    return this.http.post<NotificationResponse>(
      `${API_URL}/admin/notificaciones/whatsapp/test`,
      payload,
    );
  }

  private rolePath(role: UserRole): string {
    if (role === 'ADMINISTRADOR') return 'admin';
    if (role === 'DOCENTE') return 'docente';
    return 'alumno';
  }
}
