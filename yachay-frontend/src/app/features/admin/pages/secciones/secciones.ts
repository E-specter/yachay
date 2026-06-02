import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

import {
  SchoolSection,
  SchoolSectionStatus,
} from '../../../../core/models/section.models';

type SectionStatusFilter = SchoolSectionStatus | 'TODOS';

@Component({
  selector: 'app-secciones',
  imports: [],
  templateUrl: './secciones.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Secciones {
  readonly search = signal('');
  readonly statusFilter = signal<SectionStatusFilter>('TODOS');

  readonly sections: readonly SchoolSection[] = [
    {
      id: 1,
      nivel: 'Inicial',
      grado: '5 años',
      seccion: 'A',
      tutor: 'Patricia López Rivas',
      capacidad: 24,
      matriculados: 21,
      estado: 'ACTIVO',
    },
    {
      id: 2,
      nivel: 'Primaria',
      grado: '3° Primaria',
      seccion: 'B',
      tutor: 'Miguel Campos Flores',
      capacidad: 30,
      matriculados: 28,
      estado: 'ACTIVO',
    },
    {
      id: 3,
      nivel: 'Secundaria',
      grado: '1° Secundaria',
      seccion: 'C',
      tutor: 'Rosa Vargas Medina',
      capacidad: 32,
      matriculados: 0,
      estado: 'INACTIVO',
    },
  ];

  readonly filteredSections = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.sections.filter((section) => {
      const matchesStatus = status === 'TODOS' || section.estado === status;
      const searchable = `${section.nivel} ${section.grado} ${section.seccion} ${section.tutor}`.toLowerCase();

      return matchesStatus && searchable.includes(query);
    });
  });

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as SectionStatusFilter);
  }

  statusClass(status: SchoolSectionStatus): string {
    return status === 'ACTIVO'
      ? 'border-green-200 bg-green-50 text-green-700'
      : 'border-slate-200 bg-slate-50 text-slate-600';
  }
}
