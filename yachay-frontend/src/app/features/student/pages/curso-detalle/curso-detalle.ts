import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AppIcon } from '../../../../shared/components/app-icon/app-icon';

type CourseDetail = {
  id: number;
  nombre: string;
  docente: string;
  aula: string;
  progreso: string;
  proximaActividad: string;
  descripcion: string;
  horario: readonly string[];
  tareas: readonly string[];
  notas: readonly string[];
  comunicados: readonly string[];
};

const COURSES: readonly CourseDetail[] = [
  {
    id: 1,
    nombre: 'Matemática III',
    docente: 'Rosa Vargas',
    aula: '3 Primaria B',
    progreso: '74%',
    proximaActividad: 'Resolución de problemas',
    descripcion: 'Curso orientado a fortalecer razonamiento lógico, operaciones y resolución de problemas cotidianos.',
    horario: ['Lunes 08:00 - 09:00', 'Martes 09:00 - 10:00', 'Miércoles 09:00 - 10:00', 'Jueves 09:00 - 10:00', 'Viernes 08:00 - 09:00'],
    tareas: ['Resolución de problemas', 'Práctica dirigida'],
    notas: ['17 en evaluación semanal', '16 en práctica calificada'],
    comunicados: ['Reforzamiento de operaciones', 'Material adicional disponible'],
  },
  {
    id: 2,
    nombre: 'Comunicación I',
    docente: 'Luis Herrera',
    aula: '3 Primaria B',
    progreso: '68%',
    proximaActividad: 'Lectura evaluada',
    descripcion: 'Espacio para lectura, escritura, comprensión y expresión oral.',
    horario: ['Lunes 09:00 - 10:00', 'Martes 08:00 - 09:00', 'Miércoles 11:00 - 12:00', 'Jueves 10:00 - 11:00', 'Viernes 10:00 - 11:00'],
    tareas: ['Lectura guiada', 'Resumen de cuento'],
    notas: ['16 en comprensión lectora'],
    comunicados: ['Lectura complementaria disponible'],
  },
  {
    id: 3,
    nombre: 'Inglés III',
    docente: 'Patricia López',
    aula: '3 Primaria B',
    progreso: '72%',
    proximaActividad: 'Vocabulary quiz',
    descripcion: 'Curso de vocabulario, escucha y conversación básica.',
    horario: ['Lunes 11:00 - 12:00', 'Miércoles 10:00 - 11:00', 'Jueves 12:00 - 13:00'],
    tareas: ['Vocabulary quiz'],
    notas: ['15 en vocabulario semanal'],
    comunicados: ['Practicar pronunciación en casa'],
  },
];

@Component({
  selector: 'app-curso-detalle',
  imports: [RouterLink, AppIcon],
  templateUrl: './curso-detalle.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CursoDetalle {
  private readonly route = inject(ActivatedRoute);
  private readonly id = Number(this.route.snapshot.paramMap.get('id') ?? 1);

  readonly course = computed(
    () => COURSES.find((item) => item.id === this.id) ?? COURSES[0],
  );
}
