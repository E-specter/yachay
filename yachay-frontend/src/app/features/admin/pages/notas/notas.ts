import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { Course } from '../../../../core/models/course.models';
import { AcademicTerm, GradeRecord, GradeStatus } from '../../../../core/models/grade.models';
import { Student } from '../../../../core/models/student.models';
import { CourseService } from '../../../../core/services/course';
import { GradeService } from '../../../../core/services/grade';
import { ReportService } from '../../../../core/services/report';
import { StudentService } from '../../../../core/services/student';

type GradeStatusFilter = GradeStatus | 'TODOS';

@Component({
  selector: 'app-notas',
  imports: [ReactiveFormsModule],
  templateUrl: './notas.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Notas {
  private readonly fb = inject(FormBuilder);
  private readonly gradeService = inject(GradeService);
  private readonly studentService = inject(StudentService);
  private readonly courseService = inject(CourseService);
  private readonly reportService = inject(ReportService);

  readonly search = signal('');
  readonly statusFilter = signal<GradeStatusFilter>('TODOS');
  readonly grades = signal<GradeRecord[]>([]);
  readonly students = signal<Student[]>([]);
  readonly courses = signal<Course[]>([]);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly modalOpen = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');

  readonly form = this.fb.nonNullable.group({
    alumnoId: [0, Validators.required],
    cursoId: [0, Validators.required],
    bimestre: ['I', Validators.required],
    nota: [16, [Validators.required, Validators.min(0), Validators.max(20)]],
    tipoEvaluacion: ['Práctica calificada', Validators.required],
    comentario: [''],
  });

  readonly filteredGrades = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.grades().filter((grade) => {
      const matchesStatus = status === 'TODOS' || grade.estado === status;
      const searchable = `${grade.alumno} ${grade.curso} ${grade.docente} ${grade.bimestre}`.toLowerCase();
      return matchesStatus && searchable.includes(query);
    });
  });

  constructor() {
    this.loadGrades();
    this.loadOptions();
  }

  openCreateModal(): void {
    this.form.reset({
      alumnoId: this.students()[0]?.id ?? 0,
      cursoId: this.courses()[0]?.id ?? 0,
      bimestre: 'I',
      nota: 16,
      tipoEvaluacion: 'Práctica calificada',
      comentario: '',
    });
    this.errorMessage.set('');
    this.modalOpen.set(true);
  }

  closeCreateModal(): void {
    if (this.saving()) return;
    this.modalOpen.set(false);
  }

  createGrade(): void {
    if (this.form.invalid || this.form.controls.alumnoId.value === 0 || this.form.controls.cursoId.value === 0) {
      this.form.markAllAsTouched();
      this.errorMessage.set('Selecciona alumno y curso válidos.');
      return;
    }

    const raw = this.form.getRawValue();
    this.saving.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.gradeService.createGrade({
      alumnoId: Number(raw.alumnoId),
      cursoId: Number(raw.cursoId),
      bimestre: raw.bimestre as AcademicTerm,
      nota: Number(raw.nota),
      tipoEvaluacion: raw.tipoEvaluacion,
      comentario: raw.comentario,
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.modalOpen.set(false);
        this.successMessage.set('Registro creado correctamente.');
        this.loadGrades();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  updateStatus(grade: GradeRecord, estado: GradeStatus): void {
    this.gradeService.updateStatus(grade.id, { estado }).subscribe({
      next: () => {
        this.successMessage.set('Estado actualizado correctamente.');
        this.loadGrades();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as GradeStatusFilter);
  }

  downloadExcel(): void {
    const filename = 'notas.xlsx';

    this.reportService.downloadNotas().subscribe({
      next: (blob) => this.reportService.downloadFile(blob, filename),
      error: (error) => this.reportService.handleDownloadError(filename, error),
    });
  }

  viewGrade(grade: GradeRecord): void {
    this.successMessage.set(`Nota seleccionada: ${grade.alumno} - ${grade.curso}`);
  }

  statusClass(status: GradeStatus): string {
    if (status === 'REGISTRADA') return 'border-green-200 bg-green-50 text-green-700';
    if (status === 'OBSERVADA') return 'border-yellow-200 bg-yellow-50 text-yellow-800';
    return 'border-red-200 bg-red-50 text-red-700';
  }

  private loadGrades(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.gradeService.list().subscribe({
      next: (grades) => {
        this.grades.set(grades);
        this.loading.set(false);
      },
      error: (error) => {
        this.loading.set(false);
        this.handleLoadError('Error cargando notas', error);
      },
    });
  }

  private loadOptions(): void {
    this.studentService.list().subscribe({
      next: (students) => this.students.set(students),
      error: (error) => this.logHttpError('Error cargando alumnos para notas', error),
    });
    this.courseService.list().subscribe({
      next: (courses) => this.courses.set(courses),
      error: (error) => this.logHttpError('Error cargando cursos para notas', error),
    });
  }

  private handleSaveError(error: unknown): void {
    this.saving.set(false);
    this.errorMessage.set('No se pudo guardar. Verifica los datos o la conexión con el servidor.');
    this.logHttpError('Error guardando nota', error);
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
