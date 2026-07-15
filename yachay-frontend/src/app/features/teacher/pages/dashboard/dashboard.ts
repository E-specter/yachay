import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { TeacherDashboard } from '../../../../core/models/teacher-dashboard.models';
import { TeacherDashboardService } from '../../../../core/services/teacher-dashboard';
import { AppIcon } from '../../../../shared/components/app-icon/app-icon';
import { QuickActionCard } from '../../../../shared/components/quick-action-card/quick-action-card';
import { SectionCard } from '../../../../shared/components/section-card/section-card';
import { StatCard } from '../../../../shared/components/stat-card/stat-card';
import { StatusBadge } from '../../../../shared/components/status-badge/status-badge';

const EMPTY: TeacherDashboard = { metrics: [], reviewTasks: [], announcements: [], courses: [] };

@Component({ selector: 'app-teacher-dashboard', imports: [AppIcon, QuickActionCard, SectionCard, StatCard, StatusBadge], templateUrl: './dashboard.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class TeacherDashboardPage implements OnInit {
  private readonly service = inject(TeacherDashboardService);
  readonly dashboard = signal<TeacherDashboard>(EMPTY); readonly errorMessage = signal('');
  readonly stats = computed(() => this.dashboard().metrics.slice(0, 4).map((item, index) => ({ ...item, caption: 'Datos actualizados', icon: (['courses', 'profile', 'tasks', 'grades'] as const)[index] ?? 'dashboard', tone: (['blue', 'sky', 'yellow', 'red'] as const)[index] ?? 'blue' })));
  readonly pendingReviews = computed(() => this.dashboard().reviewTasks.reduce((total, task) => total + task.pendientes, 0));
  readonly todayCourses = computed(() => this.dashboard().courses.map((course) => ({ hora: 'Horario', curso: course.nombre, aula: course.aula, alumnos: course.cantidadAlumnos })));
  readonly lowPerformance = computed<readonly { alumno: string; curso: string; promedio: number; aula: string }[]>(() => []);
  readonly weeklyCalendar = computed(() => this.dashboard().reviewTasks.map((task) => ({ day: task.fechaEntrega.slice(0, 10), event: task.curso, detail: task.titulo, tone: 'bg-sky-soft/40 text-ink-dark' })));
  readonly recentActivity = computed(() => this.dashboard().announcements.map((item) => ({ title: item.titulo, detail: `${item.estado} · ${item.fechaPublicacion}`, icon: 'announcements' as const })));
  readonly quickActions = [
    { title: 'Crear tarea', description: 'Publicar una nueva actividad académica.', link: '/docente/tareas', icon: 'tasks', tone: 'blue' },
    { title: 'Registrar nota', description: 'Actualizar calificaciones por bimestre.', link: '/docente/notas', icon: 'grades', tone: 'sky' },
    { title: 'Publicar comunicado', description: 'Enviar avisos a estudiantes y apoderados.', link: '/docente/comunicados', icon: 'announcements', tone: 'yellow' },
  ] as const;
  ngOnInit(): void { this.service.getDashboard().subscribe({ next: (data) => this.dashboard.set(data), error: () => this.errorMessage.set('No se pudo cargar el panel docente.') }); }
}
