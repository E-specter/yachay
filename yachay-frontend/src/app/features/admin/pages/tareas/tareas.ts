import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { Course } from '../../../../core/models/course.models';
import { Homework, HomeworkStatus } from '../../../../core/models/homework.models';
import { CourseService } from '../../../../core/services/course';
import { HomeworkService } from '../../../../core/services/homework';

type HomeworkStatusFilter = HomeworkStatus | 'TODOS';

@Component({
  selector: 'app-tareas',
  imports: [ReactiveFormsModule],
  templateUrl: './tareas.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Tareas {
  private readonly fb = inject(FormBuilder);
  private readonly homeworkService = inject(HomeworkService);
  private readonly courseService = inject(CourseService);

  readonly search = signal('');
  readonly statusFilter = signal<HomeworkStatusFilter>('TODOS');
  readonly homeworks = signal<Homework[]>([]);
  readonly courses = signal<Course[]>([]);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly modalOpen = signal(false);
  readonly editingId = signal<number | null>(null);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');

  readonly form = this.fb.nonNullable.group({
    cursoId: [0, Validators.required],
    titulo: ['', Validators.required],
    descripcion: [''],
    fechaEntrega: [this.defaultDueAt(), Validators.required],
    puntajeMaximo: [20, [Validators.required, Validators.min(1), Validators.max(20)]],
    tipo: ['TAREA', Validators.required],
    permitirEntregaTardia: [false],
  });

  readonly filteredHomeworks = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.homeworks().filter((homework) => {
      const matchesStatus = status === 'TODOS' || homework.estado === status;
      const searchable = `${homework.titulo} ${homework.curso} ${homework.docente} ${homework.nivel} ${homework.grado} ${homework.seccion}`.toLowerCase();
      return matchesStatus && searchable.includes(query);
    });
  });

  constructor() {
    this.loadHomeworks();
    this.loadCourses();
  }

  openCreateModal(): void {
    this.editingId.set(null);
    this.form.reset({
      cursoId: this.courses()[0]?.id ?? 0,
      titulo: '',
      descripcion: '',
      fechaEntrega: this.defaultDueAt(),
      puntajeMaximo: 20,
      tipo: 'TAREA',
      permitirEntregaTardia: false,
    });
    this.errorMessage.set('');
    this.modalOpen.set(true);
  }

  editHomework(homework: Homework): void {
    this.editingId.set(homework.id);
    this.form.reset({ cursoId: homework.cursoId ?? this.courses().find((item) => item.nombre === homework.curso)?.id ?? 0, titulo: homework.titulo, descripcion: homework.descripcion, fechaEntrega: homework.fechaEntrega.slice(0, 16), puntajeMaximo: homework.puntajeMaximo ?? 20, tipo: homework.tipo ?? 'TAREA', permitirEntregaTardia: homework.permitirEntregaTardia ?? false });
    this.errorMessage.set(''); this.modalOpen.set(true);
  }

  closeCreateModal(): void {
    if (this.saving()) return;
    this.modalOpen.set(false);
  }

  createHomework(): void {
    if (this.form.invalid || this.form.controls.cursoId.value === 0) {
      this.form.markAllAsTouched();
      this.errorMessage.set('Selecciona un curso válido.');
      return;
    }

    const raw = this.form.getRawValue();
    const course = this.courses().find((item) => item.id === Number(raw.cursoId));
    this.saving.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const payload = {
      cursoId: Number(raw.cursoId),
      titulo: raw.titulo,
      descripcion: raw.descripcion,
      nivel: course?.nivel ?? 'Primaria',
      grado: course?.grado ?? '3 Primaria',
      seccion: 'A' as const,
      fechaPublicacion: this.localDateTimeNow(),
      fechaEntrega: raw.fechaEntrega,
      puntajeMaximo: raw.puntajeMaximo,
      tipo: raw.tipo as 'TAREA' | 'PROYECTO' | 'EXAMEN' | 'PARTICIPACION',
      permitirEntregaTardia: raw.permitirEntregaTardia,
      estado: this.editingId() ? this.homeworks().find((item) => item.id === this.editingId())?.estado ?? 'PUBLICADA' as const : 'PUBLICADA' as const,
    };
    const request = this.editingId() ? this.homeworkService.updateHomework(this.editingId()!, payload) : this.homeworkService.createHomework(payload);
    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.modalOpen.set(false);
        this.successMessage.set(this.editingId() ? 'Tarea actualizada correctamente.' : 'Registro creado correctamente.');
        this.loadHomeworks();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  updateStatus(homework: Homework, estado: HomeworkStatus): void {
    this.homeworkService.updateStatus(homework.id, { estado }).subscribe({
      next: () => {
        this.successMessage.set('Estado actualizado correctamente.');
        this.loadHomeworks();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as HomeworkStatusFilter);
  }

  viewHomework(homework: Homework): void {
    this.homeworkService.getHomework(homework.id).subscribe({ next: (item) => this.successMessage.set(`${item.titulo} · ${item.curso} · vence ${item.fechaEntrega}: ${item.descripcion}`), error: (error) => this.handleSaveError(error) });
  }

  statusClass(status: HomeworkStatus): string {
    if (status === 'PUBLICADA') return 'border-green-200 bg-green-50 text-green-700';
    if (status === 'CERRADA') return 'border-slate-200 bg-slate-50 text-slate-600';
    return 'border-yellow-200 bg-yellow-50 text-yellow-800';
  }

  private loadHomeworks(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.homeworkService.list().subscribe({
      next: (homeworks) => {
        this.homeworks.set(homeworks);
        this.loading.set(false);
      },
      error: (error) => {
        this.loading.set(false);
        this.handleLoadError('Error cargando tareas', error);
      },
    });
  }

  private loadCourses(): void {
    this.courseService.list().subscribe({
      next: (courses) => this.courses.set(courses),
      error: (error) => this.logHttpError('Error cargando cursos para tareas', error),
    });
  }

  private defaultDueAt(): string {
    const date = new Date();
    date.setDate(date.getDate() + 7);
    date.setHours(23, 59, 0, 0);
    return date.toISOString().slice(0, 16);
  }

  private localDateTimeNow(): string {
    return new Date().toISOString().slice(0, 16);
  }

  private handleSaveError(error: unknown): void {
    this.saving.set(false);
    this.errorMessage.set('No se pudo guardar. Verifica los datos o la conexión con el servidor.');
    this.logHttpError('Error guardando tarea', error);
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
