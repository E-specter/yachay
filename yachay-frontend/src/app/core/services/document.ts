import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api';

export interface DocumentResponse {
  success: boolean;
  message: string;
  documentType: string;
  entityId: number;
  providerConfigured: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class DocumentService {
  private readonly http = inject(HttpClient);

  generateAdmissionPdf(id: number): Observable<DocumentResponse> {
    return this.http.post<DocumentResponse>(
      `${API_URL}/admin/documentos/postulacion/${id}/pdf`,
      {},
    );
  }

  generateStudentPdf(id: number): Observable<DocumentResponse> {
    return this.http.post<DocumentResponse>(
      `${API_URL}/admin/documentos/alumno/${id}/pdf`,
      {},
    );
  }
}
