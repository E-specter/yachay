import { ChangeDetectionStrategy, Component } from '@angular/core';

import { TeacherDashboard } from '../../../../core/models/teacher-dashboard.models';
import { AppIcon } from '../../../../shared/components/app-icon/app-icon';
import { QuickActionCard } from '../../../../shared/components/quick-action-card/quick-action-card';
import { SectionCard } from '../../../../shared/components/section-card/section-card';
import { StatCard } from '../../../../shared/components/stat-card/stat-card';
import { StatusBadge } from '../../../../shared/components/status-badge/status-badge';

@Component({
  selector: 'app-teacher-dashboard',
  imports: [AppIcon, QuickActionCard, SectionCard, StatCard, StatusBadge],
  templateUrl: './dashboard.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TeacherDashboardPage {
  readonly dashboard: TeacherDashboard = {
    metrics: [
      { label: 'Cursos asignados', value: 4 },
      { label: 'Alumnos a cargo', value: 128 },
      { label: 'Tareas publicadas', value: 18 },
      { label: 'Notas registradas', value: 342 },
      { label: 'Comunicados publicados', value: 7 },
    ],
    reviewTasks: [
      { id: 1, titulo: 'Resolucion de problemas', curso: 'Matematica III', aula: '3 Primaria B', fechaEntrega: '2026-05-12', pendientes: 12 },
      { id: 2, titulo: 'Lectura guiada', curso: 'Comunicacion I', aula: '1 Secundaria C', fechaEntrega: '2026-05-14', pendientes: 24 },
      { id: 3, titulo: 'Informe de laboratorio', curso: 'Ciencia y Tecnologia', aula: '2 Secundaria A', fechaEntrega: '2026-05-15', pendientes: 9 },
    ],
    announcements: [
      { id: 1, titulo: 'Entrega de libretas', fechaPublicacion: '2026-05-10', estado: 'PUBLICADO' },
      { id: 2, titulo: 'Reunion de coordinacion', fechaPublicacion: '2026-05-07', estado: 'BORRADOR' },
    ],
    courses: [
      { id: 1, codigo: 'MAT-P3', nombre: 'Matematica III', aula: '3 Primaria B', cantidadAlumnos: 28 },
      { id: 2, codigo: 'COM-S1', nombre: 'Comunicacion I', aula: '1 Secundaria C', cantidadAlumnos: 31 },
      { id: 3, codigo: 'CTA-S2', nombre: 'Ciencia y Tecnologia', aula: '2 Secundaria A', cantidadAlumnos: 34 },
    ],
  };

  readonly stats = [
    { label: 'Cursos asignados', value: 4, caption: '3 aulas hoy', icon: 'courses', tone: 'blue' },
    { label: 'Alumnos a cargo', value: 128, caption: 'Seguimiento por curso', icon: 'profile', tone: 'sky' },
    { label: 'Tareas por revisar', value: 45, caption: 'Entregas abiertas', icon: 'tasks', tone: 'yellow' },
    { label: 'Notas registradas', value: 342, caption: 'Bimestre I', icon: 'grades', tone: 'red' },
  ] as const;

  readonly quickActions = [
    { title: 'Crear tarea', description: 'Publicar una nueva actividad academica.', link: '/docente/tareas', icon: 'tasks', tone: 'blue' },
    { title: 'Registrar nota', description: 'Actualizar calificaciones por bimestre.', link: '/docente/notas', icon: 'grades', tone: 'sky' },
    { title: 'Publicar comunicado', description: 'Enviar avisos a estudiantes y apoderados.', link: '/docente/comunicados', icon: 'announcements', tone: 'yellow' },
  ] as const;

  readonly lowPerformance = [
    { alumno: 'Diego Paredes', curso: 'Matematica III', promedio: 10.8, aula: '3 Primaria B' },
    { alumno: 'Camila Ruiz', curso: 'Comunicacion I', promedio: 11.2, aula: '1 Secundaria C' },
    { alumno: 'Adrian Flores', curso: 'Ciencia y Tecnologia', promedio: 10.5, aula: '2 Secundaria A' },
  ] as const;

  readonly todayCourses = [
    { hora: '08:00', curso: 'Matematica III', aula: '3 Primaria B', alumnos: 28 },
    { hora: '10:20', curso: 'Comunicacion I', aula: '1 Secundaria C', alumnos: 31 },
    { hora: '13:10', curso: 'Ciencia y Tecnologia', aula: '2 Secundaria A', alumnos: 34 },
  ] as const;

  readonly weeklyCalendar = [
    { day: 'Lun', event: 'Matematica III', detail: 'Practica dirigida', tone: 'bg-sky-soft/40 text-ink-dark' },
    { day: 'Mie', event: 'Comunicacion I', detail: 'Lectura evaluada', tone: 'bg-green-soft/30 text-ink-dark' },
    { day: 'Vie', event: 'Ciencia y Tecnologia', detail: 'Laboratorio', tone: 'bg-brown/10 text-brown' },
  ] as const;

  readonly recentActivity = [
    { title: 'Nueva entrega recibida', detail: 'Matematica III recibio 8 entregas nuevas.', icon: 'tasks' },
    { title: 'Nota registrada', detail: 'Se actualizo Comunicacion I del bimestre I.', icon: 'grades' },
    { title: 'Comunicado publicado', detail: 'Recordatorio enviado a 3 Primaria B.', icon: 'announcements' },
  ] as const;
}
