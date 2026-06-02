import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import {
  AdmissionRequest,
  AdmissionResponse,
} from '../models/admission.models';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api';

@Injectable({
  providedIn: 'root',
})
export class AdmissionService {
  private http = inject(HttpClient);

  createAdmission(payload: AdmissionRequest): Observable<AdmissionResponse> {
    return this.http.post<AdmissionResponse>(`${API_URL}/admisiones`, payload);
  }
}
