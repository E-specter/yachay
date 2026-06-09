import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AppIcon } from '../../../../shared/components/app-icon/app-icon';

type AnnouncementDetail = {
  id: number;
  titulo: string;
  remitente: string;
  fecha: string;
  destinatario: string;
  leido: boolean;
  contenido: string;
};

const ANNOUNCEMENTS: readonly AnnouncementDetail[] = [
  {
    id: 1,
    titulo: 'Entrega de libretas',
    remitente: 'Administración',
    fecha: '2026-05-10',
    destinatario: 'Alumnos y apoderados',
    leido: false,
    contenido: 'La entrega de libretas del primer bimestre se realizará según el cronograma informado por coordinación académica.',
  },
  {
    id: 2,
    titulo: 'Material de refuerzo',
    remitente: 'Rosa Vargas',
    fecha: '2026-05-09',
    destinatario: '3 Primaria B',
    leido: true,
    contenido: 'Se encuentra disponible material adicional para reforzar operaciones y resolución de problemas.',
  },
];

@Component({
  selector: 'app-comunicado-detalle',
  imports: [RouterLink, AppIcon],
  templateUrl: './comunicado-detalle.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComunicadoDetalle {
  private readonly route = inject(ActivatedRoute);
  private readonly id = Number(this.route.snapshot.paramMap.get('id') ?? 1);
  private readonly baseAnnouncement = ANNOUNCEMENTS.find((item) => item.id === this.id) ?? ANNOUNCEMENTS[0];

  readonly read = signal(this.baseAnnouncement.leido);
  readonly successMessage = signal('');
  readonly announcement = computed(() => ({ ...this.baseAnnouncement, leido: this.read() }));

  markAsRead(): void {
    this.read.set(true);
    this.successMessage.set('Comunicado marcado como leído.');
  }
}
