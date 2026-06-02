import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

interface TeacherCourse {
  id: number;
  codigo: string;
  nombre: string;
  nivel: string;
  grado: string;
  seccion: string;
  cantidadAlumnos: number;
  horario: string;
  estado: 'ACTIVO' | 'INACTIVO';
}

type StatusFilter = TeacherCourse['estado'] | 'TODOS';

@Component({
  selector: 'app-teacher-cursos',
  imports: [],
  templateUrl: './cursos.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TeacherCursos {
  readonly search = signal('');
  readonly statusFilter = signal<StatusFilter>('TODOS');

  readonly courses: readonly TeacherCourse[] = [
    { id: 1, codigo: 'MAT-P3', nombre: 'Matemática III', nivel: 'Primaria', grado: '3° Primaria', seccion: 'B', cantidadAlumnos: 28, horario: 'Lun 08:00 - 09:30', estado: 'ACTIVO' },
    { id: 2, codigo: 'COM-S1', nombre: 'Comunicación I', nivel: 'Secundaria', grado: '1° Secundaria', seccion: 'C', cantidadAlumnos: 31, horario: 'Mar 10:00 - 11:30', estado: 'ACTIVO' },
    { id: 3, codigo: 'CTA-S2', nombre: 'Ciencia y Tecnología', nivel: 'Secundaria', grado: '2° Secundaria', seccion: 'A', cantidadAlumnos: 34, horario: 'Jue 09:30 - 11:00', estado: 'ACTIVO' },
  ];

  readonly filteredCourses = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.courses.filter((course) => {
      const searchable = `${course.codigo} ${course.nombre} ${course.nivel} ${course.grado} ${course.seccion}`.toLowerCase();
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
