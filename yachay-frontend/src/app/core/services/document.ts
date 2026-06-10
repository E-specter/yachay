import { DOCUMENT, isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class DocumentService {
  private readonly http = inject(HttpClient);
  private readonly document = inject(DOCUMENT);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly isBrowser = isPlatformBrowser(this.platformId);

  downloadAdmissionPdf(id: number): Observable<Blob> {
    return this.http.get(`${API_URL}/admin/documentos/postulacion/${id}/pdf`, {
      responseType: 'blob',
    });
  }

  downloadStudentPdf(id: number): Observable<Blob> {
    return this.http.get(`${API_URL}/admin/documentos/alumno/${id}/pdf`, {
      responseType: 'blob',
    });
  }

  downloadFile(blob: Blob, filename: string): void {
    if (!this.isBrowser) return;

    const view = this.document.defaultView;
    if (!view) return;

    const url = view.URL.createObjectURL(blob);
    const anchor = this.document.createElement('a');
    anchor.href = url;
    anchor.download = filename;
    anchor.style.display = 'none';
    this.document.body.appendChild(anchor);
    anchor.click();
    anchor.remove();
    view.URL.revokeObjectURL(url);
  }

  logDownloadError(label: string, error: unknown): void {
    const httpError = error instanceof HttpErrorResponse ? error : null;

    console.error(label, {
      status: httpError?.status,
      statusText: httpError?.statusText,
      url: httpError?.url,
      message: httpError?.message,
      error: httpError?.error,
    });
  }
}
