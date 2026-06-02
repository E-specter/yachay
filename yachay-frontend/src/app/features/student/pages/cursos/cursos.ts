import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

interface StudentCourse {
  id: number;
  codigo: string;
  nombre: string;
  docente: string;
  nivel: string;
  grado: string;
  seccion: string;
  promedio: number;
  estado: 'ACTIVO' | 'INACTIVO';
}

type StatusFilter = StudentCourse['estado'] | 'TODOS';

@Component({
  selector: 'app-student-cursos',
  imports: [],
  templateUrl: './cursos.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StudentCursos {
  readonly search = signal('');
  readonly statusFilter = signal<StatusFilter>('TODOS');

  readonly courses: readonly StudentCourse[] = [
    { id: 1, codigo: 'MAT-P3', nombre: 'Matemática III', docente: 'Miguel Campos Flores', nivel: 'Primaria', grado: '3° Primaria', seccion: 'B', promedio: 17.4, estado: 'ACTIVO' },
    { id: 2, codigo: 'COM-P3', nombre: 'Comunicación III', docente: 'Rosa Vargas Medina', nivel: 'Primaria', grado: '3° Primaria', seccion: 'B', promedio: 16.2, estado: 'ACTIVO' },
    { id: 3, codigo: 'ING-P3', nombre: 'Inglés III', docente: 'Patricia López Rivas', nivel: 'Primaria', grado: '3° Primaria', seccion: 'B', promedio: 15.8, estado: 'ACTIVO' },
  ];

  readonly filteredCourses = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.courses.filter((course) => {
      const searchable = `${course.codigo} ${course.nombre} ${course.docente}`.toLowerCase();
      return (status === 'TODOS' || course.estado === status) && searchable.includes(query);
    });
  });

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as StatusFilter);
  }
}
