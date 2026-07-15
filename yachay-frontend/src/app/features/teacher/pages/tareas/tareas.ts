import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { TeacherPortalService } from '../../../../core/services/teacher-portal';

interface TeacherCourse { id: number; nombre: string; }
interface TeacherHomework { id: number; cursoId: number; titulo: string; descripcion: string; curso: string; nivel: string; grado: string; seccion: string; fechaPublicacion: string; fechaEntrega: string; estado: 'BORRADOR' | 'PUBLICADA' | 'CERRADA'; entregas: number; pendientes: number; }
interface Submission { id: number; alumno: string; contenido: string; estado: string; nota: number | null; fechaEntrega: string; }
type StatusFilter = TeacherHomework['estado'] | 'TODOS';

@Component({ selector: 'app-teacher-tareas', imports: [ReactiveFormsModule], templateUrl: './tareas.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class TeacherTareas implements OnInit {
  private readonly portal = inject(TeacherPortalService); private readonly fb = inject(FormBuilder);
  readonly search = signal(''); readonly statusFilter = signal<StatusFilter>('TODOS'); readonly homeworks = signal<TeacherHomework[]>([]); readonly courses = signal<TeacherCourse[]>([]);
  readonly modalOpen = signal(false); readonly editingId = signal<number | null>(null); readonly submissions = signal<Submission[]>([]); readonly submissionTitle = signal(''); readonly errorMessage = signal(''); readonly successMessage = signal(''); readonly saving = signal(false);
  readonly form = this.fb.nonNullable.group({ cursoId: [0, Validators.min(1)], titulo: ['', [Validators.required, Validators.maxLength(180)]], descripcion: ['', Validators.maxLength(1000)], fechaEntrega: ['', Validators.required], estado: ['BORRADOR'] });
  readonly filteredHomeworks = computed(() => { const query = this.search().trim().toLowerCase(); const status = this.statusFilter(); return this.homeworks().filter((item) => (status === 'TODOS' || item.estado === status) && `${item.titulo} ${item.descripcion} ${item.curso} ${item.nivel} ${item.grado} ${item.seccion}`.toLowerCase().includes(query)); });
  ngOnInit(): void { this.load(); }
  load(): void { this.errorMessage.set(''); forkJoin({ tasks: this.portal.getHomeworks<TeacherHomework[]>(), courses: this.portal.getCourses<TeacherCourse[]>() }).subscribe({ next: ({ tasks, courses }) => { this.homeworks.set(tasks); this.courses.set(courses); }, error: () => this.errorMessage.set('No se pudieron cargar las tareas.') }); }
  openNew(): void { this.editingId.set(null); this.form.reset({ cursoId: this.courses()[0]?.id ?? 0, titulo: '', descripcion: '', fechaEntrega: '', estado: 'BORRADOR' }); this.modalOpen.set(true); }
  openEdit(item: TeacherHomework): void { this.editingId.set(item.id); this.form.reset({ cursoId: item.cursoId, titulo: item.titulo, descripcion: item.descripcion, fechaEntrega: item.fechaEntrega.slice(0, 16), estado: item.estado }); this.modalOpen.set(true); }
  closeModal(): void { this.modalOpen.set(false); }
  save(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving.set(true); this.errorMessage.set(''); const value = this.form.getRawValue(); const payload = { ...value, fechaEntrega: new Date(value.fechaEntrega).toISOString(), fechaPublicacion: new Date().toISOString(), puntajeMaximo: 20, tipo: 'TAREA', permitirEntregaTardia: true };
    const request = this.editingId() ? this.portal.updateHomework<TeacherHomework>(this.editingId()!, payload) : this.portal.createHomework<TeacherHomework>(payload);
    request.subscribe({ next: () => { this.saving.set(false); this.modalOpen.set(false); this.successMessage.set(this.editingId() ? 'Tarea actualizada.' : 'Tarea creada.'); this.load(); }, error: () => { this.saving.set(false); this.errorMessage.set('No se pudo guardar la tarea.'); } });
  }
  changeStatus(item: TeacherHomework, estado: TeacherHomework['estado']): void { if (item.estado === estado) return; this.portal.updateHomeworkStatus<TeacherHomework>(item.id, estado).subscribe({ next: () => { this.successMessage.set(`Tarea marcada como ${estado.toLowerCase()}.`); this.load(); }, error: () => this.errorMessage.set('No se pudo actualizar el estado.') }); }
  viewSubmissions(item: TeacherHomework): void { this.portal.getHomeworkSubmissions<Submission[]>(item.id).subscribe({ next: (rows) => { this.submissions.set(rows); this.submissionTitle.set(item.titulo); }, error: () => this.errorMessage.set('No se pudieron cargar las entregas.') }); }
  updateSearch(event: Event): void { this.search.set((event.target as HTMLInputElement).value); }
  updateStatusFilter(event: Event): void { this.statusFilter.set((event.target as HTMLSelectElement).value as StatusFilter); }
}
