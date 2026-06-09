import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AppIcon } from '../../../../shared/components/app-icon/app-icon';

type HomeworkStatus = 'PENDIENTE' | 'ENTREGADA' | 'CALIFICADA';

type HomeworkDetail = {
  id: number;
  titulo: string;
  curso: string;
  docente: string;
  fechaPublicacion: string;
  fechaEntrega: string;
  estado: HomeworkStatus;
  descripcion: string;
  instrucciones: string;
  recurso: string;
};

const HOMEWORKS: readonly HomeworkDetail[] = [
  {
    id: 1,
    titulo: 'Resolución de problemas',
    curso: 'Matemática III',
    docente: 'Rosa Vargas',
    fechaPublicacion: '2026-05-05',
    fechaEntrega: '2026-05-12',
    estado: 'PENDIENTE',
    descripcion: 'Ejercicios de multiplicación, división y situaciones problemáticas.',
    instrucciones: 'Resolver los ejercicios del 1 al 12 en el cuaderno. Revisar procedimiento y respuesta final.',
    recurso: 'Guía de práctica - Matemática III.pdf',
  },
  {
    id: 2,
    titulo: 'Lectura guiada',
    curso: 'Comunicación I',
    docente: 'Luis Herrera',
    fechaPublicacion: '2026-05-04',
    fechaEntrega: '2026-05-11',
    estado: 'ENTREGADA',
    descripcion: 'Comprensión lectora sobre cuento breve.',
    instrucciones: 'Leer el texto asignado y responder las preguntas de inferencia.',
    recurso: 'Lectura complementaria.docx',
  },
];

@Component({
  selector: 'app-tarea-detalle',
  imports: [RouterLink, AppIcon],
  templateUrl: './tarea-detalle.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TareaDetalle {
  private readonly route = inject(ActivatedRoute);
  private readonly id = Number(this.route.snapshot.paramMap.get('id') ?? 1);
  private readonly baseHomework = HOMEWORKS.find((item) => item.id === this.id) ?? HOMEWORKS[0];

  readonly status = signal<HomeworkStatus>(this.baseHomework.estado);
  readonly successMessage = signal('');
  readonly homework = computed(() => ({ ...this.baseHomework, estado: this.status() }));

  simulateDelivery(): void {
    this.status.set('ENTREGADA');
    this.successMessage.set('Entrega simulada correctamente. El backend se conectará en una siguiente fase.');
  }
}
