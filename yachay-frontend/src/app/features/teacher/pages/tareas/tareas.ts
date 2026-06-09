import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

interface TeacherHomework {
  id: number;
  titulo: string;
  descripcion: string;
  curso: string;
  nivel: string;
  grado: string;
  seccion: string;
  fechaPublicacion: string;
  fechaEntrega: string;
  estado: 'BORRADOR' | 'PUBLICADA' | 'CERRADA';
}

type StatusFilter = TeacherHomework['estado'] | 'TODOS';

@Component({
  selector: 'app-teacher-tareas',
  imports: [],
  templateUrl: './tareas.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TeacherTareas {
  readonly search = signal('');
  readonly statusFilter = signal<StatusFilter>('TODOS');

  readonly homeworks: readonly TeacherHomework[] = [
    { id: 1, titulo: 'Resolución de problemas', descripcion: 'Ejercicios de multiplicación y división.', curso: 'Matemática III', nivel: 'Primaria', grado: '3° Primaria', seccion: 'B', fechaPublicacion: '2026-05-05', fechaEntrega: '2026-05-12', estado: 'PUBLICADA' },
    { id: 2, titulo: 'Lectura guiada', descripcion: 'Comprensión lectora.', curso: 'Comunicación I', nivel: 'Secundaria', grado: '1° Secundaria', seccion: 'C', fechaPublicacion: '2026-05-06', fechaEntrega: '2026-05-14', estado: 'BORRADOR' },
    { id: 3, titulo: 'Portafolio mensual', descripcion: 'Entrega de trabajos del mes.', curso: 'Arte Inicial', nivel: 'Inicial', grado: '5 años', seccion: 'A', fechaPublicacion: '2026-04-20', fechaEntrega: '2026-04-30', estado: 'CERRADA' },
  ];

  readonly filteredHomeworks = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.homeworks.filter((homework) => {
      const searchable = `${homework.titulo} ${homework.descripcion} ${homework.curso} ${homework.nivel} ${homework.grado} ${homework.seccion}`.toLowerCase();
      return (status === 'TODOS' || homework.estado === status) && searchable.includes(query);
    });
  });

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as StatusFilter);
  }

  viewSubmissions(homework: TeacherHomework): void {
    this.showAction(`Entregas de ${homework.titulo}: 24 recibidas, 6 pendientes.`);
  }

  private showAction(message: string): void {
    if (typeof window !== 'undefined') {
      window.alert(message);
    }
  }
}
