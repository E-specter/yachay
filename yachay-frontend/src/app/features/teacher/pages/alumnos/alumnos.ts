import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { TeacherPortalService } from '../../../../core/services/teacher-portal';

interface TeacherStudent { id: number; codigo: string; nombres: string; apellidos: string; documento: string; nivel: string; grado: string; seccion: string; curso: string; promedio: number; estado: 'ACTIVO' | 'INACTIVO'; }
type StatusFilter = TeacherStudent['estado'] | 'TODOS';

@Component({ selector: 'app-teacher-alumnos', imports: [], templateUrl: './alumnos.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class TeacherAlumnos implements OnInit {
  private readonly portal = inject(TeacherPortalService);
  readonly search = signal('');
  readonly statusFilter = signal<StatusFilter>('TODOS');
  readonly students = signal<TeacherStudent[]>([]);
  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly filteredStudents = computed(() => {
    const query = this.search().trim().toLowerCase(); const status = this.statusFilter();
    return this.students().filter((student) => (status === 'TODOS' || student.estado === status) && `${student.codigo} ${student.nombres} ${student.apellidos} ${student.documento} ${student.curso}`.toLowerCase().includes(query));
  });
  ngOnInit(): void { this.load(); }
  load(): void { this.loading.set(true); this.errorMessage.set(''); this.portal.getStudents<TeacherStudent[]>().subscribe({ next: (items) => { this.students.set(items); this.loading.set(false); }, error: () => { this.errorMessage.set('No se pudieron cargar los alumnos asignados.'); this.loading.set(false); } }); }
  updateSearch(event: Event): void { this.search.set((event.target as HTMLInputElement).value); }
  updateStatusFilter(event: Event): void { this.statusFilter.set((event.target as HTMLSelectElement).value as StatusFilter); }
}
