import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { AdmissionApplication } from '../../../../core/models/admission.models';
import { AdmissionService } from '../../../../core/services/admission';
import { DocumentService } from '../../../../core/services/document';
import { ReportService } from '../../../../core/services/report';

type DecisionMode = 'ACCEPT' | 'REJECT' | 'ASSIGN';

@Component({
  selector: 'app-postulaciones',
  imports: [ReactiveFormsModule],
  templateUrl: './postulaciones.html',
  styleUrl: './postulaciones.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Postulaciones {
  private readonly formBuilder = inject(FormBuilder);
  private readonly admissionService = inject(AdmissionService);
  private readonly reportService = inject(ReportService);
  private readonly documentService = inject(DocumentService);

  readonly postulaciones = signal<AdmissionApplication[]>([]);
  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');
  readonly decisionMode = signal<DecisionMode | null>(null);
  readonly selectedApplication = signal<AdmissionApplication | null>(null);
  readonly savingDecision = signal(false);
  readonly decisionForm = this.formBuilder.nonNullable.group({
    reason: ['', [Validators.maxLength(500)]],
    section: ['A', [Validators.required, Validators.maxLength(10)]],
  });

  constructor() {
    this.loadPostulaciones();
  }

  downloadExcel(): void {
    const filename = 'postulaciones.xlsx';
    this.reportService.downloadPostulaciones().subscribe({
      next: (blob) => this.reportService.downloadFile(blob, filename),
      error: (error) => this.errorMessage.set(this.reportService.handleDownloadError(filename, error)),
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
        this.errorMessage.set('No se pudo generar el PDF. Intenta nuevamente.');
        this.documentService.logDownloadError('Error generando PDF de postulación', error);
      },
    });
  }

  viewPostulacion(postulacion: AdmissionApplication): void {
    this.admissionService.getApplication(postulacion.id).subscribe({
      next: (item) => this.successMessage.set(
        `${item.postulante} · ${item.apoderado} · ${item.correo ?? ''} · ${item.nivel} ${item.grado}`,
      ),
      error: (error) => this.handleSaveError('Error cargando postulación', error),
    });
  }

  acceptPostulacion(postulacion: AdmissionApplication): void {
    this.openDecision('ACCEPT', postulacion);
  }

  rejectPostulacion(postulacion: AdmissionApplication): void {
    this.openDecision('REJECT', postulacion);
  }

  assignSection(postulacion: AdmissionApplication): void {
    this.openDecision('ASSIGN', postulacion);
  }

  closeDecision(): void {
    if (this.savingDecision()) return;
    this.decisionMode.set(null);
    this.selectedApplication.set(null);
    this.decisionForm.reset({ reason: '', section: 'A' });
  }

  submitDecision(): void {
    const mode = this.decisionMode();
    const application = this.selectedApplication();
    if (!mode || !application) return;

    const reasonControl = this.decisionForm.controls.reason;
    reasonControl.setValidators(mode === 'REJECT'
      ? [Validators.required, Validators.minLength(5), Validators.maxLength(500)]
      : [Validators.maxLength(500)]);
    reasonControl.updateValueAndValidity();
    if (this.decisionForm.invalid) {
      this.decisionForm.markAllAsTouched();
      return;
    }

    const { reason, section } = this.decisionForm.getRawValue();
    this.savingDecision.set(true);
    this.errorMessage.set('');
    const request = mode === 'REJECT'
      ? this.admissionService.rejectApplication(application.id, {
          motivo: reason.trim(),
          enviarCorreo: true,
        })
      : this.admissionService.acceptApplication(application.id, {
          nivel: application.nivel,
          grado: application.grado,
          seccion: section.trim(),
          generarCredenciales: true,
          enviarCorreo: mode === 'ACCEPT',
          observaciones: reason.trim() || (mode === 'ACCEPT'
            ? 'Postulación aceptada desde administración.'
            : `Sección ${section.trim()} asignada desde administración.`),
        });

    request.pipe(finalize(() => this.savingDecision.set(false))).subscribe({
      next: () => {
        this.successMessage.set(mode === 'REJECT'
          ? 'Postulación rechazada correctamente.'
          : mode === 'ACCEPT'
            ? 'Postulación aceptada correctamente.'
            : `Sección ${section.trim()} asignada a ${application.postulante}.`);
        this.decisionMode.set(null);
        this.selectedApplication.set(null);
        this.decisionForm.reset({ reason: '', section: 'A' });
        this.loadPostulaciones();
      },
      error: (error) => this.handleSaveError(`Error procesando postulación (${mode})`, error),
    });
  }

  statusClass(status: AdmissionApplication['status']): string {
    if (status === 'ACEPTADA') return 'border-green-200 bg-green-50 text-green-700';
    if (status === 'RECHAZADA') return 'border-red-200 bg-red-50 text-red-700';
    return 'border-yellow-200 bg-yellow-50 text-yellow-800';
  }

  private openDecision(mode: DecisionMode, application: AdmissionApplication): void {
    this.successMessage.set('');
    this.errorMessage.set('');
    this.selectedApplication.set(application);
    this.decisionMode.set(mode);
    this.decisionForm.reset({ reason: '', section: 'A' });
    const reasonControl = this.decisionForm.controls.reason;
    reasonControl.setValidators(mode === 'REJECT'
      ? [Validators.required, Validators.minLength(5), Validators.maxLength(500)]
      : [Validators.maxLength(500)]);
    reasonControl.updateValueAndValidity();
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
        this.errorMessage.set('No se pudo cargar la información. Intenta nuevamente.');
        this.logHttpError('Error cargando postulaciones', error);
      },
    });
  }

  private handleSaveError(label: string, error: unknown): void {
    const messages: Record<number, string> = {
      400: 'Revisa los datos ingresados.',
      401: 'Tu sesión venció. Inicia sesión nuevamente.',
      403: 'No tienes permisos para realizar esta acción.',
      404: 'La postulación ya no existe.',
      409: 'La postulación entra en conflicto con un registro existente.',
      500: 'Ocurrió un error interno. Intenta nuevamente.',
    };
    this.errorMessage.set(error instanceof HttpErrorResponse
      ? messages[error.status] ?? 'No se pudo guardar la decisión.'
      : 'No se pudo guardar la decisión.');
    this.logHttpError(label, error);
  }

  private logHttpError(label: string, error: unknown): void {
    if (error instanceof HttpErrorResponse) {
      console.error(label, {
        status: error.status,
        statusText: error.statusText,
        url: error.url,
        message: error.message,
      });
    }
  }
}
