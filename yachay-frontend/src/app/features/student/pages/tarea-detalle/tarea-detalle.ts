import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { StudentPortalService } from '../../../../core/services/student-portal';
import { AppIcon } from '../../../../shared/components/app-icon/app-icon';

type HomeworkDetail = { id: number; titulo: string; curso: string; docente: string; fechaPublicacion: string; fechaEntrega: string; estado: 'PENDIENTE' | 'ENTREGADA' | 'VENCIDA' | 'CALIFICADA'; descripcion: string; instrucciones: string; recurso: string; contenidoEntrega: string; fechaEntregaReal: string; };
const EMPTY: HomeworkDetail = { id: 0, titulo: '', curso: '', docente: '', fechaPublicacion: '', fechaEntrega: '', estado: 'PENDIENTE', descripcion: '', instrucciones: '', recurso: '', contenidoEntrega: '', fechaEntregaReal: '' };

@Component({ selector: 'app-tarea-detalle', imports: [RouterLink, AppIcon], templateUrl: './tarea-detalle.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class TareaDetalle implements OnInit {
  private readonly route = inject(ActivatedRoute); private readonly portal = inject(StudentPortalService); private readonly id = Number(this.route.snapshot.paramMap.get('id'));
  readonly homework = signal<HomeworkDetail>(EMPTY); readonly content = signal(''); readonly successMessage = signal(''); readonly errorMessage = signal(''); readonly submitting = signal(false);
  ngOnInit(): void { this.load(); }
  load(): void { this.errorMessage.set(''); this.portal.getHomework<HomeworkDetail>(this.id).subscribe({ next: (item) => { this.homework.set(item); this.content.set(item.contenidoEntrega ?? ''); }, error: () => this.errorMessage.set('No se pudo cargar la tarea.') }); }
  updateContent(event: Event): void { this.content.set((event.target as HTMLTextAreaElement).value); }
  submitDelivery(): void {
    if (!this.content().trim()) { this.errorMessage.set('Escribe el contenido de la entrega.'); return; }
    this.submitting.set(true); this.errorMessage.set(''); this.successMessage.set('');
    this.portal.submitHomework(this.id, { contenido: this.content().trim() }).subscribe({ next: () => { this.successMessage.set('Tarea entregada correctamente.'); this.submitting.set(false); this.load(); }, error: () => { this.errorMessage.set('No se pudo registrar la entrega. Revisa la fecha límite.'); this.submitting.set(false); } });
  }
}
