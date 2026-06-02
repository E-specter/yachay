import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

interface TeacherStudent {
  id: number;
  codigo: string;
  nombres: string;
  apellidos: string;
  documento: string;
  nivel: string;
  grado: string;
  seccion: string;
  curso: string;
  promedio: number;
  estado: 'ACTIVO' | 'INACTIVO';
}

type StatusFilter = TeacherStudent['estado'] | 'TODOS';

@Component({
  selector: 'app-teacher-alumnos',
  imports: [],
  templateUrl: './alumnos.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TeacherAlumnos {
  readonly search = signal('');
  readonly statusFilter = signal<StatusFilter>('TODOS');

  readonly students: readonly TeacherStudent[] = [
    { id: 1, codigo: 'ALU-2026-0001', nombres: 'María Fernanda', apellidos: 'Salazar Rojas', documento: 'DNI 81234567', nivel: 'Primaria', grado: '3° Primaria', seccion: 'B', curso: 'Matemática III', promedio: 17.4, estado: 'ACTIVO' },
    { id: 2, codigo: 'ALU-2026-0002', nombres: 'Luis Alberto', apellidos: 'Torres Quispe', documento: 'DNI 82345678', nivel: 'Inicial', grado: '5 años', seccion: 'A', curso: 'Arte Inicial', promedio: 15.8, estado: 'ACTIVO' },
    { id: 3, codigo: 'ALU-2026-0003', nombres: 'Ana Paula', apellidos: 'Huamán Soto', documento: 'DNI 83456789', nivel: 'Secundaria', grado: '1° Secundaria', seccion: 'C', curso: 'Comunicación I', promedio: 14.2, estado: 'INACTIVO' },
  ];

  readonly filteredStudents = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.students.filter((student) => {
      const searchable = `${student.codigo} ${student.nombres} ${student.apellidos} ${student.documento} ${student.curso}`.toLowerCase();
      return (status === 'TODOS' || student.estado === status) && searchable.includes(query);
    });
  });

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as StatusFilter);
  }
}
