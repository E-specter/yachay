import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

interface TeacherGrade {
  id: number;
  alumno: string;
  curso: string;
  bimestre: 'I' | 'II' | 'III' | 'IV';
  nota: number;
  observacion: string;
  fechaRegistro: string;
  estado: 'REGISTRADA' | 'OBSERVADA' | 'ANULADA';
}

type StatusFilter = TeacherGrade['estado'] | 'TODOS';

@Component({
  selector: 'app-teacher-notas',
  imports: [],
  templateUrl: './notas.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TeacherNotas {
  readonly search = signal('');
  readonly statusFilter = signal<StatusFilter>('TODOS');

  readonly grades: readonly TeacherGrade[] = [
    { id: 1, alumno: 'María Fernanda Salazar Rojas', curso: 'Matemática III', bimestre: 'I', nota: 17, observacion: 'Rendimiento sostenido.', fechaRegistro: '2026-05-04', estado: 'REGISTRADA' },
    { id: 2, alumno: 'Luis Alberto Torres Quispe', curso: 'Arte Inicial', bimestre: 'I', nota: 15, observacion: 'Requiere completar portafolio.', fechaRegistro: '2026-05-03', estado: 'OBSERVADA' },
    { id: 3, alumno: 'Ana Paula Huamán Soto', curso: 'Comunicación I', bimestre: 'II', nota: 12, observacion: 'Registro anulado por duplicidad.', fechaRegistro: '2026-05-02', estado: 'ANULADA' },
  ];

  readonly filteredGrades = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.grades.filter((grade) => {
      const searchable = `${grade.alumno} ${grade.curso} ${grade.bimestre} ${grade.observacion}`.toLowerCase();
      return (status === 'TODOS' || grade.estado === status) && searchable.includes(query);
    });
  });

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as StatusFilter);
  }
}
