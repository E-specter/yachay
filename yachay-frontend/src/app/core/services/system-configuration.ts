import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../config/api.config';

export interface SystemConfiguration {
  nombreInstitucion: string; correoInstitucional: string | null; telefono: string | null;
  direccion: string | null; logoUrl: string | null; versionVisible: string;
  correoConfigurado: boolean; whatsappConfigurado: boolean;
}

@Injectable({ providedIn: 'root' })
export class SystemConfigurationService {
  private readonly http = inject(HttpClient);
  get(): Observable<SystemConfiguration> { return this.http.get<SystemConfiguration>(`${API_URL}/admin/configuracion`); }
  update(payload: Omit<SystemConfiguration, 'correoConfigurado' | 'whatsappConfigurado'>): Observable<SystemConfiguration> { return this.http.put<SystemConfiguration>(`${API_URL}/admin/configuracion`, payload); }
  restore(): Observable<SystemConfiguration> { return this.http.post<SystemConfiguration>(`${API_URL}/admin/configuracion/restaurar`, {}); }
}
