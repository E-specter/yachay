import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

interface StudentHomework {
  id: number;
  titulo: string;
  descripcion: string;
  curso: string;
  docente: string;
  fechaPublicacion: string;
  fechaEntrega: string;
  estadoEntrega: 'PENDIENTE' | 'ENTREGADA' | 'VENCIDA' | 'CALIFICADA';
}

type StatusFilter = StudentHomework['estadoEntrega'] | 'TODOS';

@Component({
  selector: 'app-student-tareas',
  imports: [RouterLink],
  templateUrl: './tareas.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StudentTareas {
  readonly search = signal('');
  readonly statusFilter = signal<StatusFilter>('TODOS');

  readonly homeworks: readonly StudentHomework[] = [
    { id: 1, titulo: 'Resolución de problemas', descripcion: 'Ejercicios de multiplicación y división.', curso: 'Matemática III', docente: 'Rosa Vargas', fechaPublicacion: '2026-05-05', fechaEntrega: '2026-05-12', estadoEntrega: 'PENDIENTE' },
    { id: 2, titulo: 'Lectura guiada', descripcion: 'Comprensión lectora.', curso: 'Comunicación I', docente: 'Luis Herrera', fechaPublicacion: '2026-05-04', fechaEntrega: '2026-05-11', estadoEntrega: 'ENTREGADA' },
    { id: 3, titulo: 'Vocabulary quiz', descripcion: 'Lista de vocabulario semanal.', curso: 'Inglés III', docente: 'Patricia López', fechaPublicacion: '2026-04-28', fechaEntrega: '2026-05-02', estadoEntrega: 'CALIFICADA' },
  ];

  readonly filteredHomeworks = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.homeworks.filter((homework) => {
      const searchable = `${homework.titulo} ${homework.descripcion} ${homework.curso} ${homework.docente}`.toLowerCase();
      return (status === 'TODOS' || homework.estadoEntrega === status) && searchable.includes(query);
    });
  });

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as StatusFilter);
  }
}
