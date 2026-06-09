import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

import {
  Announcement,
  AnnouncementStatus,
} from '../../../../core/models/announcement.models';

type AnnouncementStatusFilter = AnnouncementStatus | 'TODOS';

@Component({
  selector: 'app-comunicados',
  imports: [],
  templateUrl: './comunicados.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Comunicados {
  readonly search = signal('');
  readonly statusFilter = signal<AnnouncementStatusFilter>('TODOS');

  readonly announcements: readonly Announcement[] = [
    {
      id: 1,
      titulo: 'Entrega de libretas',
      contenido: 'Cronograma de entrega de libretas del primer bimestre.',
      destinatario: 'APODERADOS',
      nivel: 'Primaria',
      grado: '3° Primaria',
      seccion: 'B',
      fechaPublicacion: '2026-05-10',
      estado: 'PUBLICADO',
    },
    {
      id: 2,
      titulo: 'Reunión de docentes',
      contenido: 'Coordinación pedagógica semanal.',
      destinatario: 'DOCENTES',
      fechaPublicacion: '2026-05-07',
      estado: 'BORRADOR',
    },
    {
      id: 3,
      titulo: 'Mantenimiento de plataforma',
      contenido: 'Ventana de mantenimiento del campus virtual.',
      destinatario: 'TODOS',
      fechaPublicacion: '2026-04-28',
      estado: 'ARCHIVADO',
    },
  ];

  readonly filteredAnnouncements = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.announcements.filter((announcement) => {
      const matchesStatus = status === 'TODOS' || announcement.estado === status;
      const searchable = `${announcement.titulo} ${announcement.contenido} ${announcement.destinatario} ${announcement.nivel ?? ''} ${announcement.grado ?? ''} ${announcement.seccion ?? ''}`.toLowerCase();

      return matchesStatus && searchable.includes(query);
    });
  });

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as AnnouncementStatusFilter);
  }

  viewAnnouncement(announcement: Announcement): void {
    this.showAction(`Comunicado: ${announcement.titulo}`);
  }

  statusClass(status: AnnouncementStatus): string {
    if (status === 'PUBLICADO') return 'border-green-200 bg-green-50 text-green-700';
    if (status === 'ARCHIVADO') return 'border-slate-200 bg-slate-50 text-slate-600';
    return 'border-yellow-200 bg-yellow-50 text-yellow-800';
  }

  private showAction(message: string): void {
    if (typeof window !== 'undefined') {
      window.alert(message);
    }
  }
}
