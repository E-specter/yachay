import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

interface StudentAnnouncement {
  id: number;
  titulo: string;
  contenido: string;
  remitente: string;
  fechaPublicacion: string;
  leido: boolean;
}

type ReadFilter = 'TODOS' | 'LEIDOS' | 'NO_LEIDOS';

@Component({
  selector: 'app-student-comunicados',
  imports: [RouterLink],
  templateUrl: './comunicados.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StudentComunicados {
  readonly search = signal('');
  readonly readFilter = signal<ReadFilter>('TODOS');

  readonly announcements: readonly StudentAnnouncement[] = [
    { id: 1, titulo: 'Entrega de libretas', contenido: 'Cronograma de entrega del primer bimestre.', remitente: 'Administración', fechaPublicacion: '2026-05-10', leido: false },
    { id: 2, titulo: 'Material de refuerzo', contenido: 'Material complementario de matemática.', remitente: 'Rosa Vargas', fechaPublicacion: '2026-05-09', leido: true },
    { id: 3, titulo: 'Actividad deportiva', contenido: 'Programación de educación física.', remitente: 'Coordinación', fechaPublicacion: '2026-05-07', leido: true },
  ];

  readonly filteredAnnouncements = computed(() => {
    const query = this.search().trim().toLowerCase();
    const filter = this.readFilter();

    return this.announcements.filter((announcement) => {
      const matchesRead = filter === 'TODOS' || (filter === 'LEIDOS' ? announcement.leido : !announcement.leido);
      const searchable = `${announcement.titulo} ${announcement.contenido} ${announcement.remitente}`.toLowerCase();
      return matchesRead && searchable.includes(query);
    });
  });

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateReadFilter(event: Event): void {
    this.readFilter.set((event.target as HTMLSelectElement).value as ReadFilter);
  }
}
