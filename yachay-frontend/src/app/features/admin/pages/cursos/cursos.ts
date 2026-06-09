import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';

import { Course, CourseStatus } from '../../../../core/models/course.models';
import { ReportService } from '../../../../core/services/report';

type CourseStatusFilter = CourseStatus | 'TODOS';

@Component({
  selector: 'app-cursos',
  imports: [],
  templateUrl: './cursos.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Cursos {
  private readonly reportService = inject(ReportService);

  readonly search = signal('');
  readonly statusFilter = signal<CourseStatusFilter>('TODOS');

  readonly courses: readonly Course[] = [
    {
      id: 1,
      nombre: 'Matemática III',
      codigo: 'MAT-P3',
      nivel: 'Primaria',
      grado: '3° Primaria',
      area: 'Matemática',
      docenteAsignado: 'Miguel Campos Flores',
      estado: 'ACTIVO',
    },
    {
      id: 2,
      nombre: 'Comunicación I',
      codigo: 'COM-S1',
      nivel: 'Secundaria',
      grado: '1° Secundaria',
      area: 'Comunicación',
      docenteAsignado: 'Rosa Vargas Medina',
      estado: 'ACTIVO',
    },
    {
      id: 3,
      nombre: 'Arte Inicial',
      codigo: 'ART-I5',
      nivel: 'Inicial',
      grado: '5 años',
      area: 'Arte',
      docenteAsignado: 'Sin asignar',
      estado: 'INACTIVO',
    },
  ];

  readonly filteredCourses = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.courses.filter((course) => {
      const matchesStatus = status === 'TODOS' || course.estado === status;
      const searchable = `${course.nombre} ${course.codigo} ${course.area} ${course.docenteAsignado} ${course.nivel} ${course.grado}`.toLowerCase();

      return matchesStatus && searchable.includes(query);
    });
  });

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as CourseStatusFilter);
  }

  downloadExcel(): void {
    const filename = 'cursos.xlsx';

    this.reportService.downloadCursos().subscribe({
      next: (blob) => this.reportService.downloadFile(blob, filename),
      error: (error) => this.reportService.handleDownloadError(filename, error),
    });
  }

  viewCourse(course: Course): void {
    this.showAction(`Curso: ${course.nombre}`);
  }

  statusClass(status: CourseStatus): string {
    return status === 'ACTIVO'
      ? 'border-green-200 bg-green-50 text-green-700'
      : 'border-slate-200 bg-slate-50 text-slate-600';
  }

  private showAction(message: string): void {
    if (typeof window !== 'undefined') {
      window.alert(message);
    }
  }
}
