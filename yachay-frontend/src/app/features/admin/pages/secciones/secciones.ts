import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { AcademicYearOption } from '../../../../core/models/course.models';
import { SchoolSection, SchoolSectionStatus } from '../../../../core/models/section.models';
import { AcademicLevel, SectionCode } from '../../../../core/models/student.models';
import { Teacher } from '../../../../core/models/teacher.models';
import { CourseService } from '../../../../core/services/course';
import { SectionService } from '../../../../core/services/section';
import { TeacherService } from '../../../../core/services/teacher';

type SectionStatusFilter = SchoolSectionStatus | 'TODOS';

@Component({
  selector: 'app-secciones',
  imports: [ReactiveFormsModule],
  templateUrl: './secciones.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Secciones {
  private readonly fb = inject(FormBuilder);
  private readonly sectionService = inject(SectionService);
  private readonly teacherService = inject(TeacherService);
  private readonly courseService = inject(CourseService);

  readonly search = signal('');
  readonly statusFilter = signal<SectionStatusFilter>('TODOS');
  readonly sections = signal<SchoolSection[]>([]);
  readonly teachers = signal<Teacher[]>([]);
  readonly academicYears = signal<AcademicYearOption[]>([]);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly modalOpen = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');

  readonly form = this.fb.nonNullable.group({
    nivel: ['Primaria', Validators.required],
    grado: ['3 Primaria', Validators.required],
    seccion: ['A', Validators.required],
    tutorId: [0],
    aula: ['Aula 301'],
    anioAcademicoId: [0, Validators.required],
    capacidad: [30, [Validators.required, Validators.min(1)]],
    activo: [true],
  });

  readonly filteredSections = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.sections().filter((section) => {
      const matchesStatus = status === 'TODOS' || section.estado === status;
      const searchable = `${section.nivel} ${section.grado} ${section.seccion} ${section.tutor}`.toLowerCase();
      return matchesStatus && searchable.includes(query);
    });
  });

  constructor() {
    this.loadSections();
    this.loadOptions();
  }

  openCreateModal(): void {
    const firstTeacher = this.teachers()[0];
    const activeYear = this.academicYears().find((year) => year.activo) ?? this.academicYears()[0];
    this.form.reset({
      nivel: 'Primaria',
      grado: '3 Primaria',
      seccion: 'A',
      tutorId: firstTeacher?.id ?? 0,
      aula: 'Aula 301',
      anioAcademicoId: activeYear?.id ?? 0,
      capacidad: 30,
      activo: true,
    });
    this.errorMessage.set('');
    this.modalOpen.set(true);
  }

  closeCreateModal(): void {
    if (this.saving()) return;
    this.modalOpen.set(false);
  }

  createSection(): void {
    if (this.form.invalid || Number(this.form.controls.anioAcademicoId.value) === 0) {
      this.form.markAllAsTouched();
      this.errorMessage.set('Selecciona un año académico válido.');
      return;
    }

    const raw = this.form.getRawValue();
    this.saving.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.sectionService.createSection({
      nivel: raw.nivel as AcademicLevel,
      grado: raw.grado,
      seccion: raw.seccion as SectionCode,
      nombre: raw.seccion as SectionCode,
      tutorId: Number(raw.tutorId) || undefined,
      aula: raw.aula,
      anioAcademicoId: Number(raw.anioAcademicoId),
      capacidad: raw.capacidad,
      activo: raw.activo,
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.modalOpen.set(false);
        this.successMessage.set('Registro creado correctamente.');
        this.loadSections();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  toggleStatus(section: SchoolSection): void {
    const estado: SchoolSectionStatus = section.estado === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';
    this.sectionService.updateStatus(section.id, { estado }).subscribe({
      next: () => {
        this.successMessage.set('Estado actualizado correctamente.');
        this.loadSections();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as SectionStatusFilter);
  }

  viewSection(section: SchoolSection): void {
    this.successMessage.set(`Sección seleccionada: ${section.nivel} ${section.grado} ${section.seccion}`);
  }

  statusClass(status: SchoolSectionStatus): string {
    return status === 'ACTIVO'
      ? 'border-green-200 bg-green-50 text-green-700'
      : 'border-slate-200 bg-slate-50 text-slate-600';
  }

  private loadSections(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.sectionService.list().subscribe({
      next: (sections) => {
        this.sections.set(sections);
        this.loading.set(false);
      },
      error: (error) => {
        this.loading.set(false);
        this.handleLoadError('Error cargando secciones', error);
      },
    });
  }

  private loadOptions(): void {
    this.teacherService.list().subscribe({
      next: (teachers) => this.teachers.set(teachers),
      error: (error) => this.logHttpError('Error cargando docentes para secciones', error),
    });
    this.courseService.getAcademicYears().subscribe({
      next: (years) => this.academicYears.set(years),
      error: (error) => this.logHttpError('Error cargando años académicos', error),
    });
  }

  private handleSaveError(error: unknown): void {
    this.saving.set(false);
    this.errorMessage.set('No se pudo guardar. Verifica los datos o la conexión con el servidor.');
    this.logHttpError('Error guardando sección', error);
  }

  private handleLoadError(label: string, error: unknown): void {
    this.errorMessage.set('No se pudo conectar con el servidor. Verifica que el backend esté activo en http://localhost:8080/api y que MySQL esté iniciado.');
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
