import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { DocumentService } from '../../../../core/services/document';
import { ReportService } from '../../../../core/services/report';

interface PostulacionMock {
  id: number;
  postulante: string;
  apoderado: string;
  nivel: string;
  grado: string;
  estado: 'Pendiente' | 'Aceptada' | 'Rechazada';
}

@Component({
  selector: 'app-postulaciones',
  imports: [],
  templateUrl: './postulaciones.html',
  styleUrl: './postulaciones.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Postulaciones {
  private readonly reportService = inject(ReportService);
  private readonly documentService = inject(DocumentService);

  readonly postulaciones: readonly PostulacionMock[] = [
    {
      id: 1,
      postulante: 'Maria Fernanda Salazar Rojas',
      apoderado: 'Rosa Rojas Perez',
      nivel: 'Primaria',
      grado: '3 Primaria',
      estado: 'Pendiente',
    },
    {
      id: 2,
      postulante: 'Luis Alberto Torres Quispe',
      apoderado: 'Carlos Torres Medina',
      nivel: 'Inicial',
      grado: '5 anos',
      estado: 'Aceptada',
    },
    {
      id: 3,
      postulante: 'Ana Paula Huaman Soto',
      apoderado: 'Elena Soto Vargas',
      nivel: 'Secundaria',
      grado: '1 Secundaria',
      estado: 'Rechazada',
    },
  ];

  downloadExcel(): void {
    const filename = 'postulaciones.xlsx';

    this.reportService.downloadPostulaciones().subscribe({
      next: (blob) => this.reportService.downloadFile(blob, filename),
      error: (error) => this.reportService.handleDownloadError(filename, error),
    });
  }

  generatePdf(postulacion: PostulacionMock): void {
    this.documentService.generateAdmissionPdf(postulacion.id).subscribe({
      next: (response) => this.showAction(response.message),
      error: () => this.showAction('No se pudo generar el PDF de postulacion. Verifica que el backend este activo.'),
    });
  }

  viewPostulacion(postulacion: PostulacionMock): void {
    this.showAction(`Postulacion: ${postulacion.postulante}`);
  }

  acceptPostulacion(postulacion: PostulacionMock): void {
    this.showAction(`Aceptar postulacion: ${postulacion.postulante}`);
  }

  rejectPostulacion(postulacion: PostulacionMock): void {
    this.showAction(`Rechazar postulacion: ${postulacion.postulante}`);
  }

  assignSection(postulacion: PostulacionMock): void {
    this.showAction(`Asignar seccion a: ${postulacion.postulante}`);
  }

  private showAction(message: string): void {
    if (typeof window !== 'undefined') {
      window.alert(message);
    }
  }
}
