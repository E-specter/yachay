import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { AcademicYearOption, Course, CourseArea, CourseStatus, SubjectOption } from '../../../../core/models/course.models';
import { Teacher } from '../../../../core/models/teacher.models';
import { CourseService } from '../../../../core/services/course';
import { ReportService } from '../../../../core/services/report';
import { TeacherService } from '../../../../core/services/teacher';

type CourseStatusFilter = CourseStatus | 'TODOS';

@Component({
  selector: 'app-cursos',
  imports: [ReactiveFormsModule],
  templateUrl: './cursos.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Cursos {
  private readonly fb = inject(FormBuilder);
  private readonly courseService = inject(CourseService);
  private readonly teacherService = inject(TeacherService);
  private readonly reportService = inject(ReportService);

  readonly search = signal('');
  readonly statusFilter = signal<CourseStatusFilter>('TODOS');
  readonly courses = signal<Course[]>([]);
  readonly teachers = signal<Teacher[]>([]);
  readonly subjects = signal<SubjectOption[]>([]);
  readonly academicYears = signal<AcademicYearOption[]>([]);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly modalOpen = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');

  readonly form = this.fb.nonNullable.group({
    nombre: ['', Validators.required],
    codigo: ['', Validators.required],
    nivel: ['Primaria', Validators.required],
    grado: ['3 Primaria', Validators.required],
    seccion: ['A', Validators.required],
    area: ['Matemática', Validators.required],
    materiaId: [0, Validators.required],
    docenteId: [0, Validators.required],
    anioAcademicoId: [0, Validators.required],
    aula: ['Aula 301'],
    maximoEstudiantes: [30, [Validators.required, Validators.min(1)]],
    activo: [true],
  });

  readonly filteredCourses = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.courses().filter((course) => {
      const matchesStatus = status === 'TODOS' || course.estado === status;
      const searchable = `${course.nombre} ${course.codigo} ${course.area} ${course.docenteAsignado} ${course.nivel} ${course.grado}`.toLowerCase();
      return matchesStatus && searchable.includes(query);
    });
  });

  constructor() {
    this.loadCourses();
    this.loadOptions();
  }

  openCreateModal(): void {
    const firstSubject = this.subjects()[0];
    const firstTeacher = this.teachers()[0];
    const activeYear = this.academicYears().find((year) => year.activo) ?? this.academicYears()[0];

    this.form.reset({
      nombre: firstSubject?.nombre ?? '',
      codigo: `CUR-2026-${String(this.courses().length + 1).padStart(3, '0')}`,
      nivel: 'Primaria',
      grado: '3 Primaria',
      seccion: 'A',
      area: (firstSubject?.area ?? 'Matemática') as CourseArea,
      materiaId: firstSubject?.id ?? 0,
      docenteId: firstTeacher?.id ?? 0,
      anioAcademicoId: activeYear?.id ?? 0,
      aula: 'Aula 301',
      maximoEstudiantes: 30,
      activo: true,
    });
    this.errorMessage.set('');
    this.modalOpen.set(true);
  }

  closeCreateModal(): void {
    if (this.saving()) return;
    this.modalOpen.set(false);
  }

  createCourse(): void {
    const selectedSubjectId = Number(this.form.controls.materiaId.value);
    const selectedTeacherId = Number(this.form.controls.docenteId.value);
    if (this.form.invalid || selectedSubjectId === 0 || selectedTeacherId === 0) {
      this.form.markAllAsTouched();
      this.errorMessage.set('Selecciona materia, docente y año académico.');
      return;
    }

    const raw = this.form.getRawValue();
    this.saving.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.courseService.createCourse({
      nombre: raw.nombre,
      codigo: raw.codigo,
      nivel: raw.nivel as 'Inicial' | 'Primaria' | 'Secundaria',
      grado: raw.grado,
      seccion: raw.seccion,
      area: raw.area as CourseArea,
      materiaId: Number(raw.materiaId),
      docenteId: Number(raw.docenteId),
      anioAcademicoId: Number(raw.anioAcademicoId) || undefined,
      aula: raw.aula,
      maximoEstudiantes: raw.maximoEstudiantes,
      activo: raw.activo,
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.modalOpen.set(false);
        this.successMessage.set('Registro creado correctamente.');
        this.loadCourses();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  toggleStatus(course: Course): void {
    const estado: CourseStatus = course.estado === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';
    this.courseService.updateStatus(course.id, { estado }).subscribe({
      next: () => {
        this.successMessage.set('Estado actualizado correctamente.');
        this.loadCourses();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as CourseStatusFilter);
  }

  downloadExcel(): void {
    const filename = 'cursos.xlsx';

    this.reportService.downloadCursos().subscribe({
      next: (blob) => this.reportService.downloadFile(blob, filename),
      error: (error) => this.reportService.handleDownloadError(filename, error),
    });
  }

  viewCourse(course: Course): void {
    this.successMessage.set(`Curso seleccionado: ${course.nombre}`);
  }

  statusClass(status: CourseStatus): string {
    return status === 'ACTIVO'
      ? 'border-green-200 bg-green-50 text-green-700'
      : 'border-slate-200 bg-slate-50 text-slate-600';
  }

  private loadCourses(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.courseService.list().subscribe({
      next: (courses) => {
        this.courses.set(courses);
        this.loading.set(false);
      },
      error: (error) => {
        this.loading.set(false);
        this.handleLoadError('Error cargando cursos', error);
      },
    });
  }

  private loadOptions(): void {
    this.courseService.getSubjects().subscribe({
      next: (subjects) => this.subjects.set(subjects),
      error: (error) => this.logHttpError('Error cargando materias', error),
    });
    this.courseService.getAcademicYears().subscribe({
      next: (years) => this.academicYears.set(years),
      error: (error) => this.logHttpError('Error cargando años académicos', error),
    });
    this.teacherService.list().subscribe({
      next: (teachers) => this.teachers.set(teachers),
      error: (error) => this.logHttpError('Error cargando docentes para curso', error),
    });
  }

  private handleSaveError(error: unknown): void {
    this.saving.set(false);
    this.errorMessage.set('No se pudo guardar. Verifica los datos o la conexión con el servidor.');
    this.logHttpError('Error guardando curso', error);
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
