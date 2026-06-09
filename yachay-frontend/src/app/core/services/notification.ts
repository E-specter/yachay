import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api';

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

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private readonly http = inject(HttpClient);

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
}
