import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

import { AppIcon } from '../../../../shared/components/app-icon/app-icon';

type EventType = 'CURSO' | 'TAREA' | 'EVALUACION' | 'COMUNICADO';
type WeekDay = 'LUN' | 'MAR' | 'MIE' | 'JUE' | 'VIE';

interface CalendarEvent {
  id: number;
  title: string;
  subtitle: string;
  day: WeekDay;
  startTime: string;
  endTime: string;
  type: EventType;
}

@Component({
  selector: 'app-student-calendario',
  imports: [AppIcon],
  templateUrl: './calendario.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StudentCalendario {
  readonly notice = signal('');
  readonly activeTypes = signal<readonly EventType[]>([
    'CURSO',
    'TAREA',
    'EVALUACION',
    'COMUNICADO',
  ]);

  readonly monthTitle = 'Mayo 2026';
  readonly weekTitle = 'Semana del 11 al 15 de mayo';
  readonly timeSlots = ['08:00', '09:00', '10:00', '11:00', '12:00', '13:00'] as const;
  readonly weekDays = [
    { key: 'LUN', label: 'Lunes', date: '11' },
    { key: 'MAR', label: 'Martes', date: '12' },
    { key: 'MIE', label: 'Miércoles', date: '13' },
    { key: 'JUE', label: 'Jueves', date: '14' },
    { key: 'VIE', label: 'Viernes', date: '15' },
  ] as const;

  readonly filters = [
    { type: 'CURSO', label: 'Cursos', color: 'bg-sky-soft' },
    { type: 'TAREA', label: 'Tareas', color: 'bg-green-soft' },
    { type: 'EVALUACION', label: 'Evaluaciones', color: 'bg-yellow' },
    { type: 'COMUNICADO', label: 'Comunicados', color: 'bg-cloud' },
  ] as const;

  readonly events: readonly CalendarEvent[] = [
    { id: 1, title: 'Matemática III', subtitle: 'Rosa Vargas', day: 'LUN', startTime: '08:00', endTime: '09:00', type: 'CURSO' },
    { id: 2, title: 'Comunicación I', subtitle: 'Luis Herrera', day: 'LUN', startTime: '09:00', endTime: '10:00', type: 'CURSO' },
    { id: 3, title: 'Ciencia y Tecnología', subtitle: 'Ana Medina', day: 'LUN', startTime: '10:00', endTime: '11:00', type: 'CURSO' },
    { id: 4, title: 'Inglés', subtitle: 'Patricia López', day: 'LUN', startTime: '11:00', endTime: '12:00', type: 'CURSO' },
    { id: 5, title: 'Educación Física', subtitle: 'Jorge Castillo', day: 'LUN', startTime: '12:00', endTime: '13:00', type: 'CURSO' },
    { id: 6, title: 'Tutoría', subtitle: 'Aula 3B', day: 'LUN', startTime: '13:00', endTime: '14:00', type: 'CURSO' },

    { id: 7, title: 'Comunicación I', subtitle: 'Luis Herrera', day: 'MAR', startTime: '08:00', endTime: '09:00', type: 'CURSO' },
    { id: 8, title: 'Matemática III', subtitle: 'Rosa Vargas', day: 'MAR', startTime: '09:00', endTime: '10:00', type: 'CURSO' },
    { id: 9, title: 'Arte', subtitle: 'Patricia Ramos', day: 'MAR', startTime: '10:00', endTime: '11:00', type: 'CURSO' },
    { id: 10, title: 'Resolución de problemas', subtitle: 'Entrega de tarea', day: 'MAR', startTime: '11:00', endTime: '12:00', type: 'TAREA' },
    { id: 11, title: 'Ciencias Sociales', subtitle: 'Ana Flores', day: 'MAR', startTime: '12:00', endTime: '13:00', type: 'CURSO' },
    { id: 12, title: 'Religión', subtitle: 'Aula 3B', day: 'MAR', startTime: '13:00', endTime: '14:00', type: 'CURSO' },

    { id: 13, title: 'Ciencia y Tecnología', subtitle: 'Ana Medina', day: 'MIE', startTime: '08:00', endTime: '09:00', type: 'CURSO' },
    { id: 14, title: 'Matemática III', subtitle: 'Rosa Vargas', day: 'MIE', startTime: '09:00', endTime: '10:00', type: 'CURSO' },
    { id: 15, title: 'Lectura evaluada', subtitle: 'Comunicación I', day: 'MIE', startTime: '10:00', endTime: '11:00', type: 'EVALUACION' },
    { id: 16, title: 'Comunicación I', subtitle: 'Luis Herrera', day: 'MIE', startTime: '11:00', endTime: '12:00', type: 'CURSO' },
    { id: 17, title: 'Educación Física', subtitle: 'Jorge Castillo', day: 'MIE', startTime: '12:00', endTime: '13:00', type: 'CURSO' },
    { id: 18, title: 'Taller de Lectura', subtitle: 'Biblioteca', day: 'MIE', startTime: '13:00', endTime: '14:00', type: 'CURSO' },

    { id: 19, title: 'Ciencias Sociales', subtitle: 'Ana Flores', day: 'JUE', startTime: '08:00', endTime: '09:00', type: 'CURSO' },
    { id: 20, title: 'Matemática III', subtitle: 'Rosa Vargas', day: 'JUE', startTime: '09:00', endTime: '10:00', type: 'CURSO' },
    { id: 21, title: 'Comunicación I', subtitle: 'Luis Herrera', day: 'JUE', startTime: '10:00', endTime: '11:00', type: 'CURSO' },
    { id: 22, title: 'Entrega de libretas', subtitle: 'Comunicado administrativo', day: 'JUE', startTime: '11:00', endTime: '12:00', type: 'COMUNICADO' },
    { id: 23, title: 'Inglés', subtitle: 'Patricia López', day: 'JUE', startTime: '12:00', endTime: '13:00', type: 'CURSO' },
    { id: 24, title: 'Computación', subtitle: 'Laboratorio', day: 'JUE', startTime: '13:00', endTime: '14:00', type: 'CURSO' },

    { id: 25, title: 'Matemática III', subtitle: 'Rosa Vargas', day: 'VIE', startTime: '08:00', endTime: '09:00', type: 'CURSO' },
    { id: 26, title: 'Informe de laboratorio', subtitle: 'Ciencia y Tecnología', day: 'VIE', startTime: '09:00', endTime: '10:00', type: 'TAREA' },
    { id: 27, title: 'Comunicación I', subtitle: 'Luis Herrera', day: 'VIE', startTime: '10:00', endTime: '11:00', type: 'CURSO' },
    { id: 28, title: 'Arte', subtitle: 'Patricia Ramos', day: 'VIE', startTime: '11:00', endTime: '12:00', type: 'CURSO' },
    { id: 29, title: 'Educación Física', subtitle: 'Jorge Castillo', day: 'VIE', startTime: '12:00', endTime: '13:00', type: 'CURSO' },
    { id: 30, title: 'Proyecto Escolar', subtitle: 'Trabajo grupal', day: 'VIE', startTime: '13:00', endTime: '14:00', type: 'CURSO' },
  ];

  readonly filteredEvents = computed(() =>
    this.events.filter((event) => this.activeTypes().includes(event.type)),
  );

  goToday(): void {
    this.notice.set('Mostrando la semana académica actual.');
  }

  goPrevious(): void {
    this.notice.set('Vista anterior preparada para integración con backend.');
  }

  goNext(): void {
    this.notice.set('Vista siguiente preparada para integración con backend.');
  }

  toggleType(type: EventType): void {
    this.activeTypes.update((types) =>
      types.includes(type)
        ? types.filter((item) => item !== type)
        : [...types, type],
    );
  }

  eventsFor(day: WeekDay, startTime: string): CalendarEvent[] {
    return this.filteredEvents().filter(
      (event) => event.day === day && event.startTime === startTime,
    );
  }

  eventClass(type: EventType): string {
    switch (type) {
      case 'TAREA':
        return 'border-green-soft bg-green-soft/25 text-ink-dark';
      case 'EVALUACION':
        return 'border-yellow bg-yellow/35 text-ink-dark';
      case 'COMUNICADO':
        return 'border-brown/20 bg-cloud text-ink-dark';
      default:
        return 'border-sky-soft bg-sky-soft/55 text-ink-dark';
    }
  }
}
