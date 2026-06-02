import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

import { Student, StudentStatus } from '../../../../core/models/student.models';

type StudentStatusFilter = StudentStatus | 'TODOS';

@Component({
  selector: 'app-alumnos',
  imports: [],
  templateUrl: './alumnos.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Alumnos {
  readonly search = signal('');
  readonly statusFilter = signal<StudentStatusFilter>('TODOS');

  readonly students: readonly Student[] = [
    {
      id: 1,
      codigo: 'ALU-2026-0001',
      nombres: 'Luis Alberto',
      apellidos: 'Torres Quispe',
      documentoTipo: 'DNI',
      documentoNumero: '82345678',
      correoInstitucional: 'ltorres@mgp.edu.pe',
      nivel: 'Inicial',
      grado: '5 años',
      seccion: 'A',
      estado: 'ACTIVO',
      apoderado: 'Carlos Torres Medina',
      correoApoderado: 'carlos.torres@example.com',
    },
    {
      id: 2,
      codigo: 'ALU-2026-0002',
      nombres: 'María Fernanda',
      apellidos: 'Salazar Rojas',
      documentoTipo: 'DNI',
      documentoNumero: '81234567',
      correoInstitucional: 'msalazar@mgp.edu.pe',
      nivel: 'Primaria',
      grado: '3° Primaria',
      seccion: 'B',
      estado: 'ACTIVO',
      apoderado: 'Rosa Rojas Pérez',
      correoApoderado: 'rosa.rojas@example.com',
    },
    {
      id: 3,
      codigo: 'ALU-2025-0198',
      nombres: 'Joaquín André',
      apellidos: 'Paredes León',
      documentoTipo: 'DNI',
      documentoNumero: '80123456',
      correoInstitucional: 'jparedes@mgp.edu.pe',
      nivel: 'Secundaria',
      grado: '2° Secundaria',
      seccion: 'C',
      estado: 'RETIRADO',
      apoderado: 'Mónica León Rivas',
      correoApoderado: 'monica.leon@example.com',
    },
  ];

  readonly filteredStudents = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.students.filter((student) => {
      const matchesStatus = status === 'TODOS' || student.estado === status;
      const searchable = `${student.codigo} ${student.nombres} ${student.apellidos} ${student.correoInstitucional} ${student.apoderado}`.toLowerCase();

      return matchesStatus && searchable.includes(query);
    });
  });

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as StudentStatusFilter);
  }

  statusClass(status: StudentStatus): string {
    if (status === 'ACTIVO') return 'border-green-200 bg-green-50 text-green-700';
    if (status === 'RETIRADO') return 'border-red-200 bg-red-50 text-red-700';
    return 'border-slate-200 bg-slate-50 text-slate-600';
  }
}
