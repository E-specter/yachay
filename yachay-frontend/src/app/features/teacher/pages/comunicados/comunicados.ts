import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

interface TeacherAnnouncement {
  id: number;
  titulo: string;
  contenido: string;
  curso: string;
  nivel: string;
  grado: string;
  seccion: string;
  fechaPublicacion: string;
  estado: 'BORRADOR' | 'PUBLICADO' | 'ARCHIVADO';
}

type StatusFilter = TeacherAnnouncement['estado'] | 'TODOS';

@Component({
  selector: 'app-teacher-comunicados',
  imports: [],
  templateUrl: './comunicados.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TeacherComunicados {
  readonly search = signal('');
  readonly statusFilter = signal<StatusFilter>('TODOS');

  readonly announcements: readonly TeacherAnnouncement[] = [
    { id: 1, titulo: 'Material de refuerzo', contenido: 'Se adjunta material para la evaluación semanal.', curso: 'Matemática III', nivel: 'Primaria', grado: '3° Primaria', seccion: 'B', fechaPublicacion: '2026-05-10', estado: 'PUBLICADO' },
    { id: 2, titulo: 'Lectura complementaria', contenido: 'Texto adicional para la próxima clase.', curso: 'Comunicación I', nivel: 'Secundaria', grado: '1° Secundaria', seccion: 'C', fechaPublicacion: '2026-05-08', estado: 'BORRADOR' },
  ];

  readonly filteredAnnouncements = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.announcements.filter((announcement) => {
      const searchable = `${announcement.titulo} ${announcement.contenido} ${announcement.curso} ${announcement.nivel} ${announcement.grado} ${announcement.seccion}`.toLowerCase();
      return (status === 'TODOS' || announcement.estado === status) && searchable.includes(query);
    });
  });

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as StatusFilter);
  }

  viewAnnouncement(announcement: TeacherAnnouncement): void {
    this.showAction(`Comunicado: ${announcement.titulo}`);
  }

  private showAction(message: string): void {
    if (typeof window !== 'undefined') {
      window.alert(message);
    }
  }
}
