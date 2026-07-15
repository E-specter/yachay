import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { AdmissionApplication } from '../../../../core/models/admission.models';
import { Student } from '../../../../core/models/student.models';
import { AdmissionService } from '../../../../core/services/admission';
import { AnnouncementService } from '../../../../core/services/announcement';
import { CalendarService } from '../../../../core/services/calendar';
import { StudentService } from '../../../../core/services/student';
import { TeacherService } from '../../../../core/services/teacher';
import { UserService } from '../../../../core/services/user';
import { AppIcon } from '../../../../shared/components/app-icon/app-icon';
import { QuickActionCard } from '../../../../shared/components/quick-action-card/quick-action-card';
import { SectionCard } from '../../../../shared/components/section-card/section-card';
import { StatCard } from '../../../../shared/components/stat-card/stat-card';
import { StatusBadge } from '../../../../shared/components/status-badge/status-badge';

@Component({ selector: 'app-dashboard', imports: [AppIcon, QuickActionCard, SectionCard, StatCard, StatusBadge], templateUrl: './dashboard.html', styleUrl: './dashboard.css', changeDetection: ChangeDetectionStrategy.OnPush })
export class Dashboard implements OnInit {
  private readonly admissionsService = inject(AdmissionService); private readonly userService = inject(UserService); private readonly teacherService = inject(TeacherService); private readonly announcementService = inject(AnnouncementService); private readonly studentService = inject(StudentService); private readonly calendarService = inject(CalendarService);
  readonly admissionsData = signal<AdmissionApplication[]>([]); readonly studentsData = signal<Student[]>([]); readonly usersActive = signal(0); readonly teachersActive = signal(0); readonly announcementsPublished = signal(0); readonly calendarData = signal<any[]>([]); readonly errorMessage = signal('');
  readonly stats = computed(() => [
    { label: 'Postulaciones en revisión', value: this.admissionsData().filter((item) => item.status === 'PENDIENTE').length, caption: 'Pendientes reales', icon: 'plus' as const, tone: 'blue' as const },
    { label: 'Usuarios activos', value: this.usersActive(), caption: 'Administradores, docentes y alumnos', icon: 'user' as const, tone: 'sky' as const },
    { label: 'Docentes habilitados', value: this.teachersActive(), caption: 'Perfiles activos', icon: 'profile' as const, tone: 'yellow' as const },
    { label: 'Comunicados publicados', value: this.announcementsPublished(), caption: 'Registros publicados', icon: 'announcements' as const, tone: 'red' as const },
  ]);
  readonly admissions = computed(() => this.admissionsData().slice(0, 5).map((item) => ({ codigo: `ADM-${item.id}`, postulante: item.postulante, nivel: item.nivel, grado: item.grado, estado: item.status })));
  readonly recentStudents = computed(() => this.studentsData().slice(-5).reverse().map((item) => ({ codigo: item.codigo, nombre: `${item.nombres} ${item.apellidos}`, aula: `${item.grado} ${item.seccion}`, apoderado: item.apoderado || 'No registrado' })));
  readonly activity = computed(() => [`${this.admissionsData().length} postulaciones registradas.`, `${this.studentsData().length} alumnos disponibles.`, `${this.calendarData().length} eventos en calendario.`]);
  readonly adminCalendar = computed(() => this.calendarData().slice(0, 5).map((item) => ({ day: item.startDateTime.slice(0, 10), event: item.title, type: item.eventType, tone: 'bg-sky-soft/40 text-ink-dark' })));
  readonly quickActions = [
    { title: 'Revisar postulaciones', description: 'Gestionar solicitudes pendientes de admisión.', link: '/admin/postulaciones', icon: 'plus', tone: 'blue' },
    { title: 'Crear usuario', description: 'Gestionar accesos internos del campus.', link: '/admin/usuarios', icon: 'user', tone: 'sky' },
    { title: 'Ver cursos', description: 'Administrar oferta académica por nivel.', link: '/admin/cursos', icon: 'courses', tone: 'yellow' },
    { title: 'Comunicados', description: 'Publicar avisos institucionales.', link: '/admin/comunicados', icon: 'announcements', tone: 'red' },
  ] as const;
  ngOnInit(): void {
    forkJoin({ admissions: this.admissionsService.listApplications(), users: this.userService.list(), teachers: this.teacherService.list(), announcements: this.announcementService.list(), students: this.studentService.list(), calendar: this.calendarService.list('ADMINISTRADOR') }).subscribe({ next: (data) => { this.admissionsData.set(data.admissions); this.studentsData.set(data.students); this.usersActive.set(data.users.filter((item) => item.estado === 'ACTIVO').length); this.teachersActive.set(data.teachers.filter((item) => item.estado === 'ACTIVO').length); this.announcementsPublished.set(data.announcements.filter((item) => item.estado === 'PUBLICADO').length); this.calendarData.set(data.calendar); }, error: () => this.errorMessage.set('No se pudieron cargar todos los indicadores administrativos.') });
  }
}
