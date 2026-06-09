import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { StudentDashboard } from '../../../../core/models/student-dashboard.models';
import { AppIcon } from '../../../../shared/components/app-icon/app-icon';
import { QuickActionCard } from '../../../../shared/components/quick-action-card/quick-action-card';
import { SectionCard } from '../../../../shared/components/section-card/section-card';

@Component({
  selector: 'app-student-dashboard',
  imports: [RouterLink, AppIcon, QuickActionCard, SectionCard],
  templateUrl: './dashboard.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StudentDashboardPage {
  readonly dashboard: StudentDashboard = {
    metrics: [],
    upcomingTasks: [
      { id: 1, titulo: 'Resolucion de problemas', curso: 'Matematica III', fechaEntrega: '2026-05-12', estadoEntrega: 'PENDIENTE' },
      { id: 2, titulo: 'Lectura guiada', curso: 'Comunicacion I', fechaEntrega: '2026-05-14', estadoEntrega: 'PENDIENTE' },
    ],
    recentGrades: [
      { id: 1, curso: 'Matematica III', bimestre: 'I', nota: 17, fechaRegistro: '2026-05-04' },
      { id: 2, curso: 'Comunicacion I', bimestre: 'I', nota: 16, fechaRegistro: '2026-05-03' },
    ],
    announcements: [
      { id: 1, titulo: 'Entrega de libretas', remitente: 'Administracion', fechaPublicacion: '2026-05-10' },
      { id: 2, titulo: 'Material de refuerzo', remitente: 'Rosa Vargas', fechaPublicacion: '2026-05-09' },
    ],
  };

  readonly nextTask = {
    title: 'Resolucion de problemas',
    course: 'Matematica III',
    dueDate: '12 May',
  } as const;

  readonly academicWeek = [
    { title: 'Proxima tarea', value: 'Resolucion de problemas', detail: 'Matematica III - vence el 12 May', icon: 'tasks', tone: 'bg-blue/10 text-blue' },
    { title: 'Ultima nota registrada', value: '17 en Matematica III', detail: 'Bimestre I - registrado el 04 May', icon: 'grades', tone: 'bg-sky/10 text-blue' },
    { title: 'Comunicado reciente', value: 'Entrega de libretas', detail: 'Administracion - 10 May', icon: 'announcements', tone: 'bg-yellow/30 text-ink' },
    { title: 'Curso destacado', value: 'Ciencia y Tecnologia', detail: 'Informe de laboratorio pendiente', icon: 'book', tone: 'bg-red/10 text-red' },
  ] as const;

  readonly quickActions = [
    { title: 'Ver tareas', description: 'Revisar pendientes, entregas y calificaciones.', link: '/alumno/tareas', icon: 'tasks', tone: 'blue' },
    { title: 'Mis notas', description: 'Consultar el avance por curso y bimestre.', link: '/alumno/notas', icon: 'grades', tone: 'sky' },
    { title: 'Comunicados', description: 'Leer avisos del colegio y docentes.', link: '/alumno/comunicados', icon: 'announcements', tone: 'yellow' },
    { title: 'Mis cursos', description: 'Entrar a tus cursos activos del periodo.', link: '/alumno/cursos', icon: 'courses', tone: 'red' },
    { title: 'Abrir calendario', description: 'Ver horario semanal, tareas y evaluaciones.', link: '/alumno/calendario', icon: 'calendar', tone: 'sky' },
  ] as const;

  readonly academicCalendar = [
    { day: 'Lun', date: '05', events: [{ title: 'Practica dirigida', type: 'Tarea', tone: 'bg-sky-soft/40 text-ink-dark' }] },
    { day: 'Mar', date: '06', events: [{ title: 'Entrega de problemas', type: 'Tarea', tone: 'bg-green-soft/35 text-ink-dark' }] },
    { day: 'Mie', date: '07', events: [{ title: 'Lectura evaluada', type: 'Evaluacion', tone: 'bg-brown/10 text-brown' }] },
    { day: 'Jue', date: '08', events: [{ title: 'Comunicado de aula', type: 'Comunicado', tone: 'bg-sky-soft/35 text-ink-dark' }] },
    { day: 'Vie', date: '09', events: [{ title: 'Informe de laboratorio', type: 'Proyecto', tone: 'bg-green-soft/30 text-ink-dark' }] },
  ] as const;

  readonly courses = [
    { id: 1, name: 'Matematica III', teacher: 'Rosa Vargas', progress: '74%', next: 'Resolucion de problemas', tone: 'bg-sky-soft/40' },
    { id: 2, name: 'Comunicacion I', teacher: 'Luis Herrera', progress: '68%', next: 'Lectura evaluada', tone: 'bg-green-soft/35' },
    { id: 3, name: 'Ciencia y Tecnologia', teacher: 'Ana Medina', progress: '81%', next: 'Informe de laboratorio', tone: 'bg-brown/10' },
  ] as const;

  readonly activityRecent = [
    { title: 'Tarea marcada como pendiente', detail: 'Matematica III necesita entrega antes del 12 May.', icon: 'tasks' },
    { title: 'Nota disponible', detail: 'Comunicacion I registro una calificacion del bimestre I.', icon: 'grades' },
    { title: 'Comunicado leido', detail: 'Material de refuerzo fue revisado hoy.', icon: 'notification' },
  ] as const;

  readonly progress = [
    { label: 'Entregas al dia', value: '12 actividades completadas', icon: 'check' },
    { label: 'Seguimiento', value: '1 recordatorio activo', icon: 'alert' },
    { label: 'Calendario', value: '5 eventos esta semana', icon: 'calendar' },
  ] as const;
}
