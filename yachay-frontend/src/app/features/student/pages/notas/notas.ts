import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

interface StudentGrade {
  curso: string;
  docente: string;
  bimestre: 'I' | 'II' | 'III' | 'IV';
  nota: number;
  observacion: string;
  fechaRegistro: string;
}

@Component({
  selector: 'app-student-notas',
  imports: [],
  templateUrl: './notas.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StudentNotas {
  readonly search = signal('');

  readonly grades: readonly StudentGrade[] = [
    { curso: 'Matemática III', docente: 'Miguel Campos Flores', bimestre: 'I', nota: 17, observacion: 'Rendimiento sostenido.', fechaRegistro: '2026-05-04' },
    { curso: 'Comunicación III', docente: 'Rosa Vargas Medina', bimestre: 'I', nota: 16, observacion: 'Buena comprensión lectora.', fechaRegistro: '2026-05-03' },
    { curso: 'Inglés III', docente: 'Patricia López Rivas', bimestre: 'I', nota: 15, observacion: 'Reforzar pronunciación.', fechaRegistro: '2026-05-02' },
  ];

  readonly filteredGrades = computed(() => {
    const query = this.search().trim().toLowerCase();
    return this.grades.filter((grade) => `${grade.curso} ${grade.docente} ${grade.bimestre}`.toLowerCase().includes(query));
  });

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }
}
