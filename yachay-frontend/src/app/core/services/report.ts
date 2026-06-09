import { DOCUMENT, isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ReportService {
  private readonly http = inject(HttpClient);
  private readonly document = inject(DOCUMENT);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly isBrowser = isPlatformBrowser(this.platformId);
  private readonly apiUrl = 'http://localhost:8080/api';

  downloadAlumnos(): Observable<Blob> {
    return this.download('/admin/reportes/alumnos.xlsx');
  }

  downloadDocentes(): Observable<Blob> {
    return this.download('/admin/reportes/docentes.xlsx');
  }

  downloadCursos(): Observable<Blob> {
    return this.download('/admin/reportes/cursos.xlsx');
  }

  downloadPostulaciones(): Observable<Blob> {
    return this.download('/admin/reportes/postulaciones.xlsx');
  }

  downloadNotas(): Observable<Blob> {
    return this.download('/admin/reportes/notas.xlsx');
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

  handleDownloadError(filename: string, error: unknown): void {
    const httpError = error instanceof HttpErrorResponse ? error : null;

    console.error(`Error descargando ${filename}`, {
      status: httpError?.status,
      statusText: httpError?.statusText,
      url: httpError?.url,
      message: httpError?.message,
      error: httpError?.error,
    });

    if (!this.isBrowser) return;

    this.document.defaultView?.alert(
      'No se pudo descargar el reporte. Revisa la consola para ver el detalle.',
    );
  }

  private download(endpoint: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}${endpoint}`, {
      responseType: 'blob',
    });
  }
}
