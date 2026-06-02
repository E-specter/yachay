import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

@Component({
  selector: 'app-status-badge',
  template: `
    <span
      [class]="'inline-flex min-h-8 items-center rounded-full px-3 text-xs font-black uppercase tracking-wide ' + statusClass()"
    >
      {{ label() }}
    </span>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StatusBadge {
  readonly label = input.required<string>();

  readonly statusClass = computed(() => {
    const status = this.label().toUpperCase();

    if (['ACTIVO', 'ACEPTADA', 'PUBLICADA', 'PUBLICADO', 'REGISTRADA', 'ENTREGADA', 'CALIFICADA'].includes(status)) {
      return 'bg-emerald-50 text-emerald-700';
    }

    if (['PENDIENTE', 'BORRADOR', 'OBSERVADA'].includes(status)) {
      return 'bg-yellow/30 text-amber-800';
    }

    if (['RECHAZADA', 'INACTIVO', 'RETIRADO', 'VENCIDA', 'ANULADA', 'CERRADA'].includes(status)) {
      return 'bg-red/10 text-red';
    }

    if (['ARCHIVADO'].includes(status)) {
      return 'bg-slate-100 text-slate-700';
    }

    return 'bg-blue/10 text-blue';
  });
}
