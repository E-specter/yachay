import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

import { Homework, HomeworkStatus } from '../../../../core/models/homework.models';

type HomeworkStatusFilter = HomeworkStatus | 'TODOS';

@Component({
  selector: 'app-tareas',
  imports: [],
  templateUrl: './tareas.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Tareas {
  readonly search = signal('');
  readonly statusFilter = signal<HomeworkStatusFilter>('TODOS');

  readonly homeworks: readonly Homework[] = [
    {
      id: 1,
      titulo: 'Resolución de problemas',
      descripcion: 'Ejercicios de multiplicación y división.',
      curso: 'Matemática III',
      docente: 'Miguel Campos Flores',
      nivel: 'Primaria',
      grado: '3° Primaria',
      seccion: 'B',
      fechaPublicacion: '2026-05-05',
      fechaEntrega: '2026-05-12',
      estado: 'PUBLICADA',
    },
    {
      id: 2,
      titulo: 'Lectura guiada',
      descripcion: 'Comprensión lectora del cuento asignado.',
      curso: 'Comunicación I',
      docente: 'Rosa Vargas Medina',
      nivel: 'Secundaria',
      grado: '1° Secundaria',
      seccion: 'C',
      fechaPublicacion: '2026-05-06',
      fechaEntrega: '2026-05-14',
      estado: 'BORRADOR',
    },
    {
      id: 3,
      titulo: 'Portafolio de arte',
      descripcion: 'Entrega final del portafolio mensual.',
      curso: 'Arte Inicial',
      docente: 'Patricia López Rivas',
      nivel: 'Inicial',
      grado: '5 años',
      seccion: 'A',
      fechaPublicacion: '2026-04-20',
      fechaEntrega: '2026-04-30',
      estado: 'CERRADA',
    },
  ];

  readonly filteredHomeworks = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.homeworks.filter((homework) => {
      const matchesStatus = status === 'TODOS' || homework.estado === status;
      const searchable = `${homework.titulo} ${homework.curso} ${homework.docente} ${homework.nivel} ${homework.grado} ${homework.seccion}`.toLowerCase();

      return matchesStatus && searchable.includes(query);
    });
  });

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as HomeworkStatusFilter);
  }

  viewHomework(homework: Homework): void {
    this.showAction(`Tarea: ${homework.titulo}`);
  }

  statusClass(status: HomeworkStatus): string {
    if (status === 'PUBLICADA') return 'border-green-200 bg-green-50 text-green-700';
    if (status === 'CERRADA') return 'border-slate-200 bg-slate-50 text-slate-600';
    return 'border-yellow-200 bg-yellow-50 text-yellow-800';
  }

  private showAction(message: string): void {
    if (typeof window !== 'undefined') {
      window.alert(message);
    }
  }
}
