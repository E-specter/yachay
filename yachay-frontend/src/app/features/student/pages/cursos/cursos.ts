import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { StudentPortalService } from '../../../../core/services/student-portal';

interface StudentCourse { id: number; codigo: string; nombre: string; docente: string; nivel: string; grado: string; seccion: string; promedio: number; estado: 'ACTIVO' | 'INACTIVO'; }
type StatusFilter = StudentCourse['estado'] | 'TODOS';

@Component({ selector: 'app-student-cursos', imports: [RouterLink], templateUrl: './cursos.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class StudentCursos implements OnInit {
  private readonly portal = inject(StudentPortalService);
  readonly search = signal(''); readonly statusFilter = signal<StatusFilter>('TODOS');
  readonly courses = signal<StudentCourse[]>([]); readonly loading = signal(false); readonly errorMessage = signal('');
  readonly filteredCourses = computed(() => { const query = this.search().trim().toLowerCase(); const status = this.statusFilter(); return this.courses().filter((course) => (status === 'TODOS' || course.estado === status) && `${course.codigo} ${course.nombre} ${course.docente}`.toLowerCase().includes(query)); });
  ngOnInit(): void { this.load(); }
  load(): void { this.loading.set(true); this.errorMessage.set(''); this.portal.getCourses<StudentCourse[]>().subscribe({ next: (items) => { this.courses.set(items); this.loading.set(false); }, error: () => { this.errorMessage.set('No se pudieron cargar tus cursos.'); this.loading.set(false); } }); }
  updateSearch(event: Event): void { this.search.set((event.target as HTMLInputElement).value); }
  updateStatusFilter(event: Event): void { this.statusFilter.set((event.target as HTMLSelectElement).value as StatusFilter); }
}
