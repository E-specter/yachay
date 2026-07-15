import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';

import { AcademicCalendarEvent } from '../../../../core/models/calendar.models';
import { CalendarService } from '../../../../core/services/calendar';
import { AppIcon } from '../../../../shared/components/app-icon/app-icon';

type EventGroup = {
  date: string;
  label: string;
  events: AcademicCalendarEvent[];
};

@Component({
  selector: 'app-student-calendario',
  imports: [AppIcon],
  templateUrl: './calendario.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StudentCalendario implements OnInit {
  private readonly calendarService = inject(CalendarService);

  readonly events = signal<AcademicCalendarEvent[]>([]);
  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly notice = signal('');
  readonly selectedType = signal('TODOS');
  readonly weekOffset = signal(0);

  readonly eventTypes = ['CLASE', 'EXAMEN', 'COMUNICADO', 'TAREA', 'FERIADO', 'OTRO'] as const;
  readonly timeSlots = ['08:00', '09:00', '10:00', '11:00', '12:00', '13:00'] as const;
  readonly weekDays = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes'] as const;

  readonly filteredEvents = computed(() => {
    const type = this.selectedType();
    if (type === 'TODOS') return this.events();
    return this.events().filter((event) => this.normalizeType(event.eventType) === type);
  });

  readonly groupedEvents = computed<EventGroup[]>(() => {
    const groups = new Map<string, AcademicCalendarEvent[]>();

    for (const event of this.filteredEvents()) {
      const dateKey = event.startDateTime.slice(0, 10);
      groups.set(dateKey, [...(groups.get(dateKey) ?? []), event]);
    }

    return Array.from(groups.entries())
      .sort(([left], [right]) => left.localeCompare(right))
      .map(([date, events]) => ({
        date,
        label: this.dateLabel(date),
        events: events.sort((left, right) =>
          left.startDateTime.localeCompare(right.startDateTime),
        ),
      }));
  });

  readonly weeklyEvents = computed(() =>
    this.filteredEvents()
      .filter((event) => {
        const start = this.weekStart(this.weekOffset());
        const end = new Date(start);
        end.setDate(end.getDate() + 7);
        const date = new Date(event.startDateTime);
        return date >= start && date < end;
      })
      .slice()
      .sort((left, right) => left.startDateTime.localeCompare(right.startDateTime))
      .slice(0, 12),
  );

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.notice.set('');

    this.calendarService.list('ALUMNO').subscribe({
      next: (events) => {
        this.events.set(events);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error cargando calendario alumno', error);
        this.errorMessage.set('No se pudo cargar el calendario académico.');
        this.loading.set(false);
      },
    });
  }

  goToday(): void {
    this.weekOffset.set(0);
    this.notice.set('Mostrando la semana actual.');
  }

  goPrevious(): void {
    this.weekOffset.update((value) => value - 1);
    this.notice.set('Mostrando la semana anterior.');
  }

  goNext(): void {
    this.weekOffset.update((value) => value + 1);
    this.notice.set('Mostrando la semana siguiente.');
  }

  setFilter(type: string): void {
    this.selectedType.set(type);
  }

  timeRange(event: AcademicCalendarEvent): string {
    return `${this.timeLabel(event.startDateTime)} - ${this.timeLabel(event.endDateTime)}`;
  }

  eventClass(type: string): string {
    switch (this.normalizeType(type)) {
      case 'TAREA':
        return 'border-green-soft bg-green-soft/25 text-ink-dark';
      case 'EXAMEN':
      case 'EVALUACION':
        return 'border-public-amber bg-public-amber/25 text-ink-dark';
      case 'COMUNICADO':
        return 'border-brown/20 bg-cloud text-ink-dark';
      default:
        return 'border-public-aqua bg-public-aqua/35 text-ink-dark';
    }
  }

  private normalizeType(type: string): string {
    return type.toUpperCase().replace('Ó', 'O').replace('Á', 'A');
  }

  private dateLabel(date: string): string {
    return new Intl.DateTimeFormat('es-PE', {
      weekday: 'long',
      day: '2-digit',
      month: 'long',
      year: 'numeric',
    }).format(new Date(`${date}T12:00:00`));
  }

  private timeLabel(value: string): string {
    return new Intl.DateTimeFormat('es-PE', {
      hour: '2-digit',
      minute: '2-digit',
    }).format(new Date(value));
  }

  private weekStart(offset: number): Date {
    const date = new Date();
    const day = date.getDay() || 7;
    date.setHours(0, 0, 0, 0);
    date.setDate(date.getDate() - day + 1 + offset * 7);
    return date;
  }
}
