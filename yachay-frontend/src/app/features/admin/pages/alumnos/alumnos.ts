import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { SectionCode, Student, StudentStatus } from '../../../../core/models/student.models';
import { DocumentService } from '../../../../core/services/document';
import { ReportService } from '../../../../core/services/report';
import { StudentService } from '../../../../core/services/student';

type StudentStatusFilter = StudentStatus | 'TODOS';

@Component({
  selector: 'app-alumnos',
  imports: [ReactiveFormsModule],
  templateUrl: './alumnos.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Alumnos {
  private readonly fb = inject(FormBuilder);
  private readonly studentService = inject(StudentService);
  private readonly reportService = inject(ReportService);
  private readonly documentService = inject(DocumentService);

  readonly search = signal('');
  readonly statusFilter = signal<StudentStatusFilter>('TODOS');
  readonly students = signal<Student[]>([]);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly modalOpen = signal(false);
  readonly editingId = signal<number | null>(null);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');

  readonly form = this.fb.nonNullable.group({
    nombres: ['', Validators.required],
    apellidos: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    passwordTemporal: ['Alumno123456', Validators.minLength(8)],
    documentoTipo: ['DNI'],
    documentoNumero: [''],
    nivel: ['Primaria'],
    apoderado: [''],
    correoApoderado: ['', Validators.email],
    codigo: ['', Validators.required],
    grado: ['3 Primaria', Validators.required],
    seccion: ['A', Validators.required],
    fechaMatricula: [new Date().toISOString().slice(0, 10), Validators.required],
    activo: [true],
  });

  readonly filteredStudents = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.students().filter((student) => {
      const matchesStatus = status === 'TODOS' || student.estado === status;
      const searchable = `${student.codigo} ${student.nombres} ${student.apellidos} ${student.correoInstitucional} ${student.apoderado}`.toLowerCase();
      return matchesStatus && searchable.includes(query);
    });
  });

  constructor() {
    this.loadStudents();
  }

  openCreateModal(): void {
    this.editingId.set(null);
    this.form.reset({
      nombres: '',
      apellidos: '',
      email: '',
      passwordTemporal: 'Alumno123456',
      codigo: `ALU-2026-${String(this.students().length + 1).padStart(3, '0')}`,
      grado: '3 Primaria',
      seccion: 'A',
      fechaMatricula: new Date().toISOString().slice(0, 10),
      activo: true,
      documentoTipo: 'DNI', documentoNumero: '', nivel: 'Primaria', apoderado: '', correoApoderado: '',
    });
    this.errorMessage.set('');
    this.modalOpen.set(true);
  }

  closeCreateModal(): void {
    if (this.saving()) return;
    this.modalOpen.set(false);
  }

  createStudent(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    this.saving.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const payload = {
      codigo: raw.codigo,
      nombres: raw.nombres,
      apellidos: raw.apellidos,
      email: raw.email,
      passwordTemporal: raw.passwordTemporal,
      documentoTipo: raw.documentoTipo,
      documentoNumero: raw.documentoNumero,
      correoInstitucional: raw.email,
      nivel: raw.nivel as 'Inicial' | 'Primaria' | 'Secundaria',
      grado: raw.grado,
      seccion: raw.seccion as SectionCode,
      fechaMatricula: raw.fechaMatricula,
      activo: raw.activo,
      apoderado: raw.apoderado,
      correoApoderado: raw.correoApoderado,
      estado: raw.activo ? 'ACTIVO' as const : 'INACTIVO' as const,
    };
    const request = this.editingId() ? this.studentService.updateStudent(this.editingId()!, payload) : this.studentService.createStudent(payload);
    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.modalOpen.set(false);
        this.successMessage.set(this.editingId() ? 'Alumno actualizado correctamente.' : 'Registro creado correctamente.');
        this.loadStudents();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as StudentStatusFilter);
  }

  downloadExcel(): void {
    const filename = 'alumnos.xlsx';
    this.reportService.downloadAlumnos().subscribe({
      next: (blob) => this.reportService.downloadFile(blob, filename),
      error: (error) => this.errorMessage.set(this.reportService.handleDownloadError(filename, error)),
    });
  }

  generateStudentPdf(student: Student): void {
    const filename = `ficha-alumno-${student.id}.pdf`;
    this.documentService.downloadStudentPdf(student.id).subscribe({
      next: (blob) => {
        this.documentService.downloadFile(blob, filename);
        this.successMessage.set('PDF generado correctamente.');
      },
      error: (error) => {
        this.errorMessage.set('No se pudo generar el PDF. Revisa el backend.');
        this.documentService.logDownloadError('Error generando ficha PDF de alumno', error);
      },
    });
  }

  viewStudent(student: Student): void {
    this.studentService.getStudent(student.id).subscribe({ next: (item) => this.successMessage.set(`${item.codigo} · ${item.nombres} ${item.apellidos} · ${item.nivel} ${item.grado} ${item.seccion}`), error: (error) => this.handleSaveError(error) });
  }

  editStudent(student: Student): void {
    this.editingId.set(student.id);
    this.form.reset({ nombres: student.nombres, apellidos: student.apellidos, email: student.correoInstitucional, passwordTemporal: '', codigo: student.codigo, grado: student.grado, seccion: student.seccion, fechaMatricula: new Date().toISOString().slice(0, 10), activo: student.estado === 'ACTIVO', documentoTipo: student.documentoTipo, documentoNumero: student.documentoNumero, nivel: student.nivel, apoderado: student.apoderado, correoApoderado: student.correoApoderado });
    this.errorMessage.set('');
    this.modalOpen.set(true);
  }

  changeStudentStatus(student: Student): void {
    const estado: StudentStatus = student.estado === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';
    this.studentService.updateStatus(student.id, { estado }).subscribe({
      next: () => {
        this.successMessage.set('Estado actualizado correctamente.');
        this.loadStudents();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  statusClass(status: StudentStatus): string {
    if (status === 'ACTIVO') return 'border-green-200 bg-green-50 text-green-700';
    if (status === 'RETIRADO') return 'border-red-200 bg-red-50 text-red-700';
    return 'border-slate-200 bg-slate-50 text-slate-600';
  }

  private loadStudents(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.studentService.list().subscribe({
      next: (students) => {
        this.students.set(students);
        this.loading.set(false);
      },
      error: (error) => {
        this.loading.set(false);
        this.handleLoadError(error);
      },
    });
  }

  private handleSaveError(error: unknown): void {
    this.saving.set(false);
    this.errorMessage.set('No se pudo guardar. Verifica los datos o la conexión con el servidor.');
    this.logHttpError('Error guardando alumno', error);
  }

  private handleLoadError(error: unknown): void {
    this.errorMessage.set('No se pudo conectar con el servidor. Verifica que el backend esté activo en http://localhost:8080/api y que MySQL esté iniciado.');
    this.logHttpError('Error cargando alumnos', error);
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
