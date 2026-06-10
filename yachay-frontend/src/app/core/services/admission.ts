import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import {
  AdmissionApplication,
  AdmissionDecisionRequest,
  AdmissionRequest,
  AdmissionResponse,
} from '../models/admission.models';
import { Observable } from 'rxjs';

import { API_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class AdmissionService {
  private http = inject(HttpClient);

  createAdmission(payload: AdmissionRequest): Observable<AdmissionResponse> {
    return this.http.post<AdmissionResponse>(`${API_URL}/admisiones`, payload);
  }

  listApplications(): Observable<AdmissionApplication[]> {
    return this.http.get<AdmissionApplication[]>(`${API_URL}/admin/postulaciones`);
  }

  acceptApplication(id: number, payload: AdmissionDecisionRequest): Observable<AdmissionApplication> {
    return this.http.patch<AdmissionApplication>(`${API_URL}/admin/postulaciones/${id}/aceptar`, payload);
  }

  rejectApplication(id: number, payload: AdmissionDecisionRequest): Observable<AdmissionApplication> {
    return this.http.patch<AdmissionApplication>(`${API_URL}/admin/postulaciones/${id}/rechazar`, payload);
  }
}
