import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { StudentPortalService } from '../../../../core/services/student-portal';

interface StudentHomework { id: number; titulo: string; descripcion: string; curso: string; docente: string; fechaPublicacion: string; fechaEntrega: string; estadoEntrega: 'PENDIENTE' | 'ENTREGADA' | 'VENCIDA' | 'CALIFICADA'; }
type StatusFilter = StudentHomework['estadoEntrega'] | 'TODOS';

@Component({ selector: 'app-student-tareas', imports: [RouterLink], templateUrl: './tareas.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class StudentTareas implements OnInit {
  private readonly portal = inject(StudentPortalService);
  readonly search = signal(''); readonly statusFilter = signal<StatusFilter>('TODOS');
  readonly homeworks = signal<StudentHomework[]>([]); readonly loading = signal(false); readonly errorMessage = signal('');
  readonly filteredHomeworks = computed(() => { const query = this.search().trim().toLowerCase(); const status = this.statusFilter(); return this.homeworks().filter((item) => (status === 'TODOS' || item.estadoEntrega === status) && `${item.titulo} ${item.descripcion} ${item.curso} ${item.docente}`.toLowerCase().includes(query)); });
  ngOnInit(): void { this.load(); }
  load(): void { this.loading.set(true); this.errorMessage.set(''); this.portal.getHomeworks<StudentHomework[]>().subscribe({ next: (items) => { this.homeworks.set(items); this.loading.set(false); }, error: () => { this.errorMessage.set('No se pudieron cargar tus tareas.'); this.loading.set(false); } }); }
  updateSearch(event: Event): void { this.search.set((event.target as HTMLInputElement).value); }
  updateStatusFilter(event: Event): void { this.statusFilter.set((event.target as HTMLSelectElement).value as StatusFilter); }
}
