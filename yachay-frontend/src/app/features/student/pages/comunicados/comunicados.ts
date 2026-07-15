import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { StudentPortalService } from '../../../../core/services/student-portal';

interface StudentAnnouncement { id: number; titulo: string; contenido: string; remitente: string; fechaPublicacion: string; leido: boolean; }
type ReadFilter = 'TODOS' | 'LEIDOS' | 'NO_LEIDOS';

@Component({ selector: 'app-student-comunicados', imports: [RouterLink], templateUrl: './comunicados.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class StudentComunicados implements OnInit {
  private readonly portal = inject(StudentPortalService);
  readonly search = signal(''); readonly readFilter = signal<ReadFilter>('TODOS'); readonly announcements = signal<StudentAnnouncement[]>([]); readonly loading = signal(false); readonly errorMessage = signal('');
  readonly filteredAnnouncements = computed(() => { const query = this.search().trim().toLowerCase(); const filter = this.readFilter(); return this.announcements().filter((item) => (filter === 'TODOS' || (filter === 'LEIDOS' ? item.leido : !item.leido)) && `${item.titulo} ${item.contenido} ${item.remitente}`.toLowerCase().includes(query)); });
  ngOnInit(): void { this.load(); }
  load(): void { this.loading.set(true); this.errorMessage.set(''); this.portal.getAnnouncements<StudentAnnouncement[]>().subscribe({ next: (items) => { this.announcements.set(items); this.loading.set(false); }, error: () => { this.errorMessage.set('No se pudieron cargar los comunicados.'); this.loading.set(false); } }); }
  updateSearch(event: Event): void { this.search.set((event.target as HTMLInputElement).value); }
  updateReadFilter(event: Event): void { this.readFilter.set((event.target as HTMLSelectElement).value as ReadFilter); }
}
