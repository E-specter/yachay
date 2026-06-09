import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';

import { GradeRecord, GradeStatus } from '../../../../core/models/grade.models';
import { ReportService } from '../../../../core/services/report';

type GradeStatusFilter = GradeStatus | 'TODOS';

@Component({
  selector: 'app-notas',
  imports: [],
  templateUrl: './notas.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Notas {
  private readonly reportService = inject(ReportService);

  readonly search = signal('');
  readonly statusFilter = signal<GradeStatusFilter>('TODOS');

  readonly grades: readonly GradeRecord[] = [
    {
      id: 1,
      alumno: 'María Fernanda Salazar Rojas',
      curso: 'Matemática III',
      docente: 'Miguel Campos Flores',
      bimestre: 'I',
      nota: 17,
      fechaRegistro: '2026-05-04',
      estado: 'REGISTRADA',
    },
    {
      id: 2,
      alumno: 'Luis Alberto Torres Quispe',
      curso: 'Arte Inicial',
      docente: 'Patricia López Rivas',
      bimestre: 'I',
      nota: 15,
      fechaRegistro: '2026-05-03',
      estado: 'OBSERVADA',
    },
    {
      id: 3,
      alumno: 'Joaquín André Paredes León',
      curso: 'Comunicación I',
      docente: 'Rosa Vargas Medina',
      bimestre: 'IV',
      nota: 11,
      fechaRegistro: '2025-12-18',
      estado: 'ANULADA',
    },
  ];

  readonly filteredGrades = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.grades.filter((grade) => {
      const matchesStatus = status === 'TODOS' || grade.estado === status;
      const searchable = `${grade.alumno} ${grade.curso} ${grade.docente} ${grade.bimestre}`.toLowerCase();

      return matchesStatus && searchable.includes(query);
    });
  });

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as GradeStatusFilter);
  }

  downloadExcel(): void {
    const filename = 'notas.xlsx';

    this.reportService.downloadNotas().subscribe({
      next: (blob) => this.reportService.downloadFile(blob, filename),
      error: (error) => this.reportService.handleDownloadError(filename, error),
    });
  }

  viewGrade(grade: GradeRecord): void {
    this.showAction(`Nota: ${grade.alumno} - ${grade.curso}`);
  }

  statusClass(status: GradeStatus): string {
    if (status === 'REGISTRADA') return 'border-green-200 bg-green-50 text-green-700';
    if (status === 'OBSERVADA') return 'border-yellow-200 bg-yellow-50 text-yellow-800';
    return 'border-red-200 bg-red-50 text-red-700';
  }

  private showAction(message: string): void {
    if (typeof window !== 'undefined') {
      window.alert(message);
    }
  }
}
