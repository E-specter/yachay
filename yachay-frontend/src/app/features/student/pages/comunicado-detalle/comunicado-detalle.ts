import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { StudentPortalService } from '../../../../core/services/student-portal';
import { AppIcon } from '../../../../shared/components/app-icon/app-icon';

type AnnouncementDetail = { id: number; titulo: string; remitente: string; fechaPublicacion: string; leido: boolean; contenido: string; };
const EMPTY: AnnouncementDetail = { id: 0, titulo: '', remitente: '', fechaPublicacion: '', leido: false, contenido: '' };

@Component({ selector: 'app-comunicado-detalle', imports: [RouterLink, AppIcon], templateUrl: './comunicado-detalle.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class ComunicadoDetalle implements OnInit {
  private readonly route = inject(ActivatedRoute); private readonly portal = inject(StudentPortalService); private readonly id = Number(this.route.snapshot.paramMap.get('id'));
  readonly announcement = signal<AnnouncementDetail>(EMPTY); readonly successMessage = signal(''); readonly errorMessage = signal('');
  ngOnInit(): void { this.portal.getAnnouncement<AnnouncementDetail>(this.id).subscribe({ next: (item) => this.announcement.set(item), error: () => this.errorMessage.set('No se pudo cargar el comunicado.') }); }
  markAsRead(): void { if (this.announcement().leido) return; this.portal.markAnnouncementAsRead<AnnouncementDetail>(this.id).subscribe({ next: (item) => { this.announcement.set(item); this.successMessage.set('Comunicado marcado como leído.'); }, error: () => this.errorMessage.set('No se pudo marcar el comunicado como leído.') }); }
}
