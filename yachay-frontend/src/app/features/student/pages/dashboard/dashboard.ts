import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { StudentDashboard } from '../../../../core/models/student-dashboard.models';
import { StudentDashboardService } from '../../../../core/services/student-dashboard';
import { StudentPortalService } from '../../../../core/services/student-portal';
import { AppIcon } from '../../../../shared/components/app-icon/app-icon';
import { QuickActionCard } from '../../../../shared/components/quick-action-card/quick-action-card';
import { SectionCard } from '../../../../shared/components/section-card/section-card';

interface StudentCourse { id: number; nombre: string; docente: string; }
const EMPTY: StudentDashboard = { metrics: [], upcomingTasks: [], recentGrades: [], announcements: [] };

@Component({ selector: 'app-student-dashboard', imports: [RouterLink, AppIcon, QuickActionCard, SectionCard], templateUrl: './dashboard.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class StudentDashboardPage implements OnInit {
  private readonly dashboardService = inject(StudentDashboardService); private readonly portal = inject(StudentPortalService);
  readonly dashboard = signal<StudentDashboard>(EMPTY); readonly courseData = signal<StudentCourse[]>([]); readonly errorMessage = signal('');
  readonly nextTask = computed(() => { const item = this.dashboard().upcomingTasks[0]; return { title: item?.titulo ?? 'Sin tareas pendientes', course: item?.curso ?? '', dueDate: item?.fechaEntrega ?? '' }; });
  readonly academicWeek = computed(() => {
    const data = this.dashboard(); const items: { title: string; value: string; detail: string; icon: 'tasks' | 'grades' | 'announcements'; tone: string }[] = [];
    if (data.upcomingTasks[0]) items.push({ title: 'Próxima tarea', value: data.upcomingTasks[0].titulo, detail: `${data.upcomingTasks[0].curso} · ${data.upcomingTasks[0].fechaEntrega}`, icon: 'tasks', tone: 'bg-blue/10 text-blue' });
    if (data.recentGrades[0]) items.push({ title: 'Última nota', value: `${data.recentGrades[0].nota} en ${data.recentGrades[0].curso}`, detail: `Bimestre ${data.recentGrades[0].bimestre}`, icon: 'grades', tone: 'bg-sky/10 text-blue' });
    if (data.announcements[0]) items.push({ title: 'Comunicado reciente', value: data.announcements[0].titulo, detail: `${data.announcements[0].remitente} · ${data.announcements[0].fechaPublicacion}`, icon: 'announcements', tone: 'bg-yellow/30 text-ink' });
    return items;
  });
  readonly academicCalendar = computed(() => this.dashboard().upcomingTasks.slice(0, 5).map((item) => ({ day: new Date(item.fechaEntrega).toLocaleDateString('es-PE', { weekday: 'short' }), date: item.fechaEntrega.slice(8, 10), events: [{ title: item.titulo, type: 'Tarea', tone: 'bg-sky-soft/40 text-ink-dark' }] })));
  readonly courses = computed(() => this.courseData().map((course, index) => ({ id: course.id, name: course.nombre, teacher: course.docente, progress: 'Activo', next: this.dashboard().upcomingTasks.find((task) => task.curso === course.nombre)?.titulo ?? 'Sin tarea pendiente', tone: (['bg-sky-soft/40', 'bg-green-soft/35', 'bg-brown/10'] as const)[index % 3] })));
  readonly activityRecent = computed(() => [
    ...this.dashboard().recentGrades.map((item) => ({ title: 'Nota disponible', detail: `${item.curso}: ${item.nota}`, icon: 'grades' as const })),
    ...this.dashboard().announcements.map((item) => ({ title: item.titulo, detail: `${item.remitente} · ${item.fechaPublicacion}`, icon: 'notification' as const })),
  ].slice(0, 6));
  readonly progress = computed(() => this.dashboard().metrics.map((item, index) => ({ label: item.label, value: item.value, icon: (['courses', 'tasks', 'check', 'grades'] as const)[index] ?? 'check' })));
  readonly quickActions = [
    { title: 'Ver tareas', description: 'Revisar pendientes, entregas y calificaciones.', link: '/alumno/tareas', icon: 'tasks', tone: 'blue' },
    { title: 'Mis notas', description: 'Consultar el avance por curso y bimestre.', link: '/alumno/notas', icon: 'grades', tone: 'sky' },
    { title: 'Comunicados', description: 'Leer avisos del colegio y docentes.', link: '/alumno/comunicados', icon: 'announcements', tone: 'yellow' },
    { title: 'Mis cursos', description: 'Entrar a tus cursos activos del periodo.', link: '/alumno/cursos', icon: 'courses', tone: 'red' },
    { title: 'Abrir calendario', description: 'Ver horario semanal, tareas y evaluaciones.', link: '/alumno/calendario', icon: 'calendar', tone: 'sky' },
  ] as const;
  ngOnInit(): void { forkJoin({ dashboard: this.dashboardService.getDashboard(), courses: this.portal.getCourses<StudentCourse[]>() }).subscribe({ next: ({ dashboard, courses }) => { this.dashboard.set(dashboard); this.courseData.set(courses); }, error: () => this.errorMessage.set('No se pudo cargar el panel del alumno.') }); }
}
