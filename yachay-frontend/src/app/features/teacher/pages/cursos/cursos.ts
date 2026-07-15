import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { TeacherPortalService } from '../../../../core/services/teacher-portal';

interface TeacherCourse { id: number; codigo: string; nombre: string; nivel: string; grado: string; seccion: string; cantidadAlumnos: number; horario: string; estado: 'ACTIVO' | 'INACTIVO'; }
type StatusFilter = TeacherCourse['estado'] | 'TODOS';

@Component({ selector: 'app-teacher-cursos', imports: [], templateUrl: './cursos.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class TeacherCursos implements OnInit {
  private readonly portal = inject(TeacherPortalService);
  readonly search = signal('');
  readonly statusFilter = signal<StatusFilter>('TODOS');
  readonly courses = signal<TeacherCourse[]>([]);
  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly filteredCourses = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();
    return this.courses().filter((course) => (status === 'TODOS' || course.estado === status) && `${course.codigo} ${course.nombre} ${course.nivel} ${course.grado} ${course.seccion}`.toLowerCase().includes(query));
  });
  ngOnInit(): void { this.load(); }
  load(): void {
    this.loading.set(true); this.errorMessage.set('');
    this.portal.getCourses<TeacherCourse[]>().subscribe({ next: (items) => { this.courses.set(items); this.loading.set(false); }, error: () => { this.errorMessage.set('No se pudieron cargar los cursos asignados.'); this.loading.set(false); } });
  }
  updateSearch(event: Event): void { this.search.set((event.target as HTMLInputElement).value); }
  updateStatusFilter(event: Event): void { this.statusFilter.set((event.target as HTMLSelectElement).value as StatusFilter); }
}
