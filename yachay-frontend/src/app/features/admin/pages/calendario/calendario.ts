import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import {
  AcademicCalendarEvent,
  CalendarAudience,
  CalendarEventType,
  CreateCalendarEventRequest,
} from '../../../../core/models/calendar.models';
import { CalendarService } from '../../../../core/services/calendar';
import { AppIcon } from '../../../../shared/components/app-icon/app-icon';

type EventGroup = {
  date: string;
  label: string;
  events: AcademicCalendarEvent[];
};

@Component({
  selector: 'app-admin-calendario',
  imports: [ReactiveFormsModule, AppIcon],
  templateUrl: './calendario.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminCalendario implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly calendarService = inject(CalendarService);

  readonly events = signal<AcademicCalendarEvent[]>([]);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');
  readonly formOpen = signal(false);
  readonly editingId = signal<number | null>(null);
  readonly selectedType = signal('TODOS');

  readonly eventTypes = [
    'CLASE',
    'EXAMEN',
    'REUNION',
    'FERIADO',
    'COMUNICADO',
    'TAREA',
    'OTRO',
  ] as const satisfies readonly CalendarEventType[];

  readonly audiences = [
    'TODOS',
    'ADMINISTRADOR',
    'DOCENTE',
    'ALUMNO',
    'APODERADO',
  ] as const satisfies readonly CalendarAudience[];

  readonly form = this.fb.nonNullable.group({
    titulo: ['', [Validators.required, Validators.minLength(3)]],
    descripcion: [''],
    fechaInicio: ['', [Validators.required]],
    fechaFin: ['', [Validators.required]],
    tipo: ['CLASE', [Validators.required]],
    cursoId: [''],
    seccion: [''],
    publicoObjetivo: ['TODOS', [Validators.required]],
  });

  readonly filteredEvents = computed(() => {
    const type = this.selectedType();
    if (type === 'TODOS') return this.events();
    return this.events().filter((event) => this.normalizeType(event.eventType) === type);
  });

  readonly groupedEvents = computed<EventGroup[]>(() => {
    const groups = new Map<string, AcademicCalendarEvent[]>();

    for (const event of this.filteredEvents()) {
      const dateKey = event.startDateTime.slice(0, 10);
      const current = groups.get(dateKey) ?? [];
      groups.set(dateKey, [...current, event]);
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

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.calendarService.list('ADMINISTRADOR').subscribe({
      next: (events) => {
        this.events.set(events);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error cargando calendario administrador', error);
        this.errorMessage.set('No se pudo cargar el calendario. Verifica la conexión con el servidor.');
        this.loading.set(false);
      },
    });
  }

  openForm(): void {
    this.editingId.set(null);
    this.successMessage.set('');
    this.errorMessage.set('');
    this.formOpen.set(true);
  }

  editEvent(event: AcademicCalendarEvent): void {
    this.editingId.set(event.id);
    this.form.reset({ titulo: event.title, descripcion: event.description ?? '', fechaInicio: event.startDateTime.slice(0, 16), fechaFin: event.endDateTime.slice(0, 16), tipo: event.eventType, cursoId: event.courseId ? String(event.courseId) : '', seccion: event.sectionName ?? '', publicoObjetivo: event.audience ?? 'TODOS' });
    this.errorMessage.set(''); this.formOpen.set(true);
  }

  closeForm(): void {
    this.formOpen.set(false);
    this.editingId.set(null);
    this.form.reset({
      titulo: '',
      descripcion: '',
      fechaInicio: '',
      fechaFin: '',
      tipo: 'CLASE',
      cursoId: '',
      seccion: '',
      publicoObjetivo: 'TODOS',
    });
  }

  createEvent(): void {
    this.successMessage.set('');
    this.errorMessage.set('');

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.errorMessage.set('Completa los campos obligatorios del evento.');
      return;
    }

    const raw = this.form.getRawValue();
    const payload: CreateCalendarEventRequest = {
      titulo: raw.titulo,
      descripcion: raw.descripcion,
      fechaInicio: raw.fechaInicio,
      fechaFin: raw.fechaFin,
      tipo: raw.tipo,
      cursoId: raw.cursoId ? Number(raw.cursoId) : null,
      seccion: raw.seccion || null,
      publicoObjetivo: raw.publicoObjetivo,
    };

    this.saving.set(true);
    const request = this.editingId() ? this.calendarService.updateAdminEvent(this.editingId()!, payload) : this.calendarService.createAdminEvent(payload);
    request.subscribe({
      next: (event) => {
        this.events.update((events) => [event, ...events]);
        this.successMessage.set(this.editingId() ? 'Evento actualizado correctamente.' : 'Evento creado correctamente.');
        this.saving.set(false);
        this.closeForm();
        this.loadEvents();
      },
      error: (error) => {
        console.error('Error creando evento de calendario', error);
        this.errorMessage.set('No se pudo guardar el evento. Verifica los datos o la conexión.');
        this.saving.set(false);
      },
    });
  }

  archiveEvent(event: AcademicCalendarEvent): void {
    this.calendarService.archiveAdminEvent(event.id).subscribe({ next: () => { this.successMessage.set(event.status === 'ARCHIVADO' ? 'Evento reactivado.' : 'Evento archivado.'); this.loadEvents(); }, error: () => this.errorMessage.set('No se pudo cambiar el estado del evento.') });
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
        return 'border-green-soft bg-green-soft/20 text-ink-dark';
      case 'EXAMEN':
      case 'EVALUACION':
        return 'border-public-amber bg-public-amber/20 text-ink-dark';
      case 'FERIADO':
        return 'border-public-orange bg-public-orange/15 text-ink-dark';
      case 'COMUNICADO':
        return 'border-brown/20 bg-cloud text-ink-dark';
      default:
        return 'border-public-aqua bg-public-aqua/25 text-ink-dark';
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
}
