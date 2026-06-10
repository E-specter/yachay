import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';

import { AdmissionApplication } from '../../../../core/models/admission.models';
import { AdmissionService } from '../../../../core/services/admission';
import { DocumentService } from '../../../../core/services/document';
import { ReportService } from '../../../../core/services/report';

@Component({
  selector: 'app-postulaciones',
  imports: [],
  templateUrl: './postulaciones.html',
  styleUrl: './postulaciones.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Postulaciones {
  private readonly admissionService = inject(AdmissionService);
  private readonly reportService = inject(ReportService);
  private readonly documentService = inject(DocumentService);

  readonly postulaciones = signal<AdmissionApplication[]>([]);
  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');

  constructor() {
    this.loadPostulaciones();
  }

  downloadExcel(): void {
    const filename = 'postulaciones.xlsx';

    this.reportService.downloadPostulaciones().subscribe({
      next: (blob) => this.reportService.downloadFile(blob, filename),
      error: (error) => this.reportService.handleDownloadError(filename, error),
    });
  }

  generatePdf(postulacion: AdmissionApplication): void {
    const filename = `postulacion-${postulacion.id}.pdf`;
    this.documentService.downloadAdmissionPdf(postulacion.id).subscribe({
      next: (blob) => {
        this.documentService.downloadFile(blob, filename);
        this.successMessage.set('PDF generado correctamente.');
      },
      error: (error) => {
        this.errorMessage.set('No se pudo generar el PDF. Revisa el backend.');
        this.documentService.logDownloadError('Error generando PDF de postulación', error);
      },
    });
  }

  viewPostulacion(postulacion: AdmissionApplication): void {
    this.successMessage.set(`Postulación seleccionada: ${postulacion.postulante}`);
  }

  acceptPostulacion(postulacion: AdmissionApplication): void {
    this.admissionService.acceptApplication(postulacion.id, {
      nivel: postulacion.nivel,
      grado: postulacion.grado,
      seccion: 'A',
      generarCredenciales: true,
      enviarCorreo: true,
      observaciones: 'Postulación aceptada desde administración.',
    }).subscribe({
      next: () => {
        this.successMessage.set('Postulación aceptada correctamente.');
        this.loadPostulaciones();
      },
      error: (error) => this.handleSaveError('Error aceptando postulación', error),
    });
  }

  rejectPostulacion(postulacion: AdmissionApplication): void {
    this.admissionService.rejectApplication(postulacion.id, {
      motivo: 'Solicitud rechazada desde administración.',
      enviarCorreo: true,
    }).subscribe({
      next: () => {
        this.successMessage.set('Postulación rechazada correctamente.');
        this.loadPostulaciones();
      },
      error: (error) => this.handleSaveError('Error rechazando postulación', error),
    });
  }

  assignSection(postulacion: AdmissionApplication): void {
    this.successMessage.set(`Asignación preparada para ${postulacion.postulante}: sección A.`);
  }

  statusClass(status: AdmissionApplication['status']): string {
    if (status === 'ACEPTADA') return 'border-green-200 bg-green-50 text-green-700';
    if (status === 'RECHAZADA') return 'border-red-200 bg-red-50 text-red-700';
    return 'border-yellow-200 bg-yellow-50 text-yellow-800';
  }

  private loadPostulaciones(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.admissionService.listApplications().subscribe({
      next: (postulaciones) => {
        this.postulaciones.set(postulaciones);
        this.loading.set(false);
      },
      error: (error) => {
        this.loading.set(false);
        this.errorMessage.set('No se pudo conectar con el servidor. Verifica que el backend esté activo en http://localhost:8080/api y que MySQL esté iniciado.');
        this.logHttpError('Error cargando postulaciones', error);
      },
    });
  }

  private handleSaveError(label: string, error: unknown): void {
    this.errorMessage.set('No se pudo guardar. Verifica los datos o la conexión con el servidor.');
    this.logHttpError(label, error);
  }

  private logHttpError(label: string, error: unknown): void {
    if (error instanceof HttpErrorResponse) {
      console.error(label, {
        status: error.status,
        statusText: error.statusText,
        url: error.url,
        message: error.message,
        error: error.error,
      });
    }
  }
}
