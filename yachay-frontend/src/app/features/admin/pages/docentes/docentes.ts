import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

import { Teacher, TeacherStatus } from '../../../../core/models/teacher.models';

type TeacherStatusFilter = TeacherStatus | 'TODOS';

@Component({
  selector: 'app-docentes',
  imports: [],
  templateUrl: './docentes.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Docentes {
  readonly search = signal('');
  readonly statusFilter = signal<TeacherStatusFilter>('TODOS');

  readonly teachers: readonly Teacher[] = [
    {
      id: 1,
      nombres: 'Rosa Elena',
      apellidos: 'Vargas Medina',
      documentoTipo: 'DNI',
      documentoNumero: '45678123',
      email: 'rvargas@mgp.edu.pe',
      especialidad: 'Comunicación',
      telefono: '987654320',
      estado: 'ACTIVO',
      fechaCreacion: '2026-05-01',
    },
    {
      id: 2,
      nombres: 'Miguel Ángel',
      apellidos: 'Campos Flores',
      documentoTipo: 'DNI',
      documentoNumero: '46781234',
      email: 'mcampos@mgp.edu.pe',
      especialidad: 'Matemática',
      telefono: '976543211',
      estado: 'ACTIVO',
      fechaCreacion: '2026-05-02',
    },
    {
      id: 3,
      nombres: 'Patricia',
      apellidos: 'López Rivas',
      documentoTipo: 'CE',
      documentoNumero: 'CE102938',
      email: 'plopez@mgp.edu.pe',
      especialidad: 'Inglés',
      telefono: '965432112',
      estado: 'INACTIVO',
      fechaCreacion: '2026-05-03',
    },
  ];

  readonly filteredTeachers = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.teachers.filter((teacher) => {
      const matchesStatus = status === 'TODOS' || teacher.estado === status;
      const searchable = `${teacher.nombres} ${teacher.apellidos} ${teacher.email} ${teacher.especialidad}`.toLowerCase();

      return matchesStatus && searchable.includes(query);
    });
  });

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as TeacherStatusFilter);
  }

  statusClass(status: TeacherStatus): string {
    return status === 'ACTIVO'
      ? 'border-green-200 bg-green-50 text-green-700'
      : 'border-slate-200 bg-slate-50 text-slate-600';
  }
}
