import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { Teacher, TeacherStatus } from '../../../../core/models/teacher.models';
import { ReportService } from '../../../../core/services/report';
import { TeacherService } from '../../../../core/services/teacher';

type TeacherStatusFilter = TeacherStatus | 'TODOS';

@Component({
  selector: 'app-docentes',
  imports: [ReactiveFormsModule],
  templateUrl: './docentes.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Docentes {
  private readonly fb = inject(FormBuilder);
  private readonly teacherService = inject(TeacherService);
  private readonly reportService = inject(ReportService);

  readonly search = signal('');
  readonly statusFilter = signal<TeacherStatusFilter>('TODOS');
  readonly teachers = signal<Teacher[]>([]);
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
    passwordTemporal: ['Docente123456', Validators.minLength(8)],
    documentoTipo: ['DNI'],
    documentoNumero: [''],
    codigoEmpleado: ['', Validators.required],
    especialidad: ['', Validators.required],
    telefono: [''],
    fechaContratacion: [new Date().toISOString().slice(0, 10), Validators.required],
    activo: [true],
  });

  readonly filteredTeachers = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.teachers().filter((teacher) => {
      const matchesStatus = status === 'TODOS' || teacher.estado === status;
      const searchable = `${teacher.nombres} ${teacher.apellidos} ${teacher.email} ${teacher.especialidad}`.toLowerCase();
      return matchesStatus && searchable.includes(query);
    });
  });

  constructor() {
    this.loadTeachers();
  }

  openCreateModal(): void {
    this.editingId.set(null);
    this.form.reset({
      nombres: '',
      apellidos: '',
      email: '',
      passwordTemporal: 'Docente123456',
      codigoEmpleado: `DOC-2026-${String(this.teachers().length + 1).padStart(3, '0')}`,
      especialidad: '',
      telefono: '',
      fechaContratacion: new Date().toISOString().slice(0, 10),
      activo: true,
      documentoTipo: 'DNI', documentoNumero: '',
    });
    this.errorMessage.set('');
    this.modalOpen.set(true);
  }

  closeCreateModal(): void {
    if (this.saving()) return;
    this.modalOpen.set(false);
  }

  createTeacher(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    this.saving.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const payload = {
      nombres: raw.nombres,
      apellidos: raw.apellidos,
      email: raw.email,
      passwordTemporal: raw.passwordTemporal,
      codigoEmpleado: raw.codigoEmpleado,
      documentoTipo: raw.documentoTipo,
      documentoNumero: raw.documentoNumero,
      especialidad: raw.especialidad,
      telefono: raw.telefono,
      fechaContratacion: raw.fechaContratacion,
      activo: raw.activo,
      estado: raw.activo ? 'ACTIVO' as const : 'INACTIVO' as const,
    };
    const request = this.editingId() ? this.teacherService.updateTeacher(this.editingId()!, payload) : this.teacherService.createTeacher(payload);
    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.modalOpen.set(false);
        this.successMessage.set(this.editingId() ? 'Docente actualizado correctamente.' : 'Registro creado correctamente.');
        this.loadTeachers();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as TeacherStatusFilter);
  }

  downloadExcel(): void {
    const filename = 'docentes.xlsx';
    this.reportService.downloadDocentes().subscribe({
      next: (blob) => this.reportService.downloadFile(blob, filename),
      error: (error) => this.errorMessage.set(this.reportService.handleDownloadError(filename, error)),
    });
  }

  viewTeacher(teacher: Teacher): void {
    this.teacherService.getTeacher(teacher.id).subscribe({ next: (item) => this.successMessage.set(`${item.codigoEmpleado ?? ''} · ${item.nombres} ${item.apellidos} · ${item.especialidad}`), error: (error) => this.handleSaveError(error) });
  }

  editTeacher(teacher: Teacher): void {
    this.editingId.set(teacher.id);
    this.form.reset({ nombres: teacher.nombres, apellidos: teacher.apellidos, email: teacher.email, passwordTemporal: '', codigoEmpleado: teacher.codigoEmpleado ?? '', especialidad: teacher.especialidad, telefono: teacher.telefono, fechaContratacion: teacher.fechaContratacion ?? teacher.fechaCreacion, activo: teacher.estado === 'ACTIVO', documentoTipo: teacher.documentoTipo, documentoNumero: teacher.documentoNumero });
    this.errorMessage.set('');
    this.modalOpen.set(true);
  }

  toggleStatus(teacher: Teacher): void {
    const estado: TeacherStatus = teacher.estado === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';
    this.teacherService.updateStatus(teacher.id, { estado }).subscribe({
      next: () => {
        this.successMessage.set('Estado actualizado correctamente.');
        this.loadTeachers();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  statusClass(status: TeacherStatus): string {
    return status === 'ACTIVO'
      ? 'border-green-200 bg-green-50 text-green-700'
      : 'border-slate-200 bg-slate-50 text-slate-600';
  }

  private loadTeachers(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.teacherService.list().subscribe({
      next: (teachers) => {
        this.teachers.set(teachers);
        this.loading.set(false);
      },
      error: (error) => {
        this.loading.set(false);
        this.errorMessage.set('No se pudo conectar con el servidor. Verifica que el backend esté activo en http://localhost:8080/api y que MySQL esté iniciado.');
        this.logHttpError('Error cargando docentes', error);
      },
    });
  }

  private handleSaveError(error: unknown): void {
    this.saving.set(false);
    this.errorMessage.set('No se pudo guardar. Verifica los datos o la conexión con el servidor.');
    this.logHttpError('Error guardando docente', error);
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
