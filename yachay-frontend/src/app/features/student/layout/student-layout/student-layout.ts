import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';

import { AuthService } from '../../../../core/services/auth';
import {
  NotificationService,
  UserNotification,
} from '../../../../core/services/notification';
import { AppIcon, type AppIconName } from '../../../../shared/components/app-icon/app-icon';

type NavItem = {
  label: string;
  path: string;
  exact: boolean;
  icon: AppIconName;
};

@Component({
  selector: 'app-student-layout',
  imports: [RouterLink, RouterLinkActive, RouterOutlet, AppIcon],
  templateUrl: './student-layout.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StudentLayout implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);

  readonly sidebarCollapsed = signal(false);
  readonly mobileSidebarOpen = signal(false);
  readonly userMenuOpen = signal(false);
  readonly notificationMenuOpen = signal(false);
  readonly notifications = signal<UserNotification[]>([]);
  readonly notificationError = signal('');

  readonly userName = 'Jesus Gabriel';
  readonly roleLabel = 'Estudiante';
  readonly sectionTitle = 'Panel alumno';
  readonly profilePath = '/alumno/perfil';
  readonly notificationPreview = computed(() => this.notifications().slice(0, 5));
  readonly notificationCount = computed(() => {
    const unread = this.notifications().filter((notification) => !notification.read).length;
    if (unread === 0) return '0';
    return unread > 9 ? '9+' : String(unread);
  });

  readonly navItems = [
    { label: 'Dashboard', path: '/alumno/dashboard', exact: true, icon: 'dashboard' },
    { label: 'Calendario', path: '/alumno/calendario', exact: false, icon: 'calendar' },
    { label: 'Mis cursos', path: '/alumno/cursos', exact: false, icon: 'courses' },
    { label: 'Tareas', path: '/alumno/tareas', exact: false, icon: 'tasks' },
    { label: 'Notas', path: '/alumno/notas', exact: false, icon: 'grades' },
    { label: 'Comunicados', path: '/alumno/comunicados', exact: false, icon: 'announcements' },
    { label: 'Notificaciones', path: '/alumno/notificaciones', exact: false, icon: 'notification' },
    { label: 'Perfil', path: '/alumno/perfil', exact: false, icon: 'profile' },
  ] as const satisfies readonly NavItem[];

  readonly sidebarClass = computed(() => [
    'fixed inset-y-0 left-0 z-50 flex w-72 flex-col border-r border-brown/10 bg-white shadow-xl transition-all duration-300 ease-in-out lg:translate-x-0 lg:shadow-none',
    this.mobileSidebarOpen() ? 'translate-x-0' : '-translate-x-full',
    this.sidebarCollapsed() ? 'lg:w-20' : 'lg:w-72',
  ].join(' '));

  readonly mainClass = computed(() => [
    'min-h-screen bg-cloud transition-all duration-300 ease-in-out',
    this.sidebarCollapsed() ? 'lg:pl-20' : 'lg:pl-72',
  ].join(' '));

  ngOnInit(): void {
    this.loadNotifications();
  }

  openMobileSidebar(): void {
    this.mobileSidebarOpen.set(true);
  }

  closeMobileSidebar(): void {
    this.mobileSidebarOpen.set(false);
  }

  toggleCollapsed(): void {
    this.sidebarCollapsed.update((value) => !value);
  }

  toggleUserMenu(): void {
    this.userMenuOpen.update((value) => !value);
    this.notificationMenuOpen.set(false);
  }

  closeUserMenu(): void {
    this.userMenuOpen.set(false);
  }

  toggleNotifications(): void {
    this.notificationMenuOpen.update((value) => !value);
    this.userMenuOpen.set(false);
    if (this.notificationMenuOpen()) {
      this.loadNotifications();
    }
  }

  loadNotifications(): void {
    this.notificationError.set('');
    this.notificationService.list('ALUMNO').subscribe({
      next: (notifications) => this.notifications.set(notifications),
      error: (error) => {
        console.error('Error cargando notificaciones alumno', error);
        this.notificationError.set('No se pudieron cargar las notificaciones.');
      },
    });
  }

  markNotificationAsRead(notification: UserNotification): void {
    if (notification.read) return;

    this.notificationService.markAsRead('ALUMNO', notification.id).subscribe({
      next: (updated) => {
        this.notifications.update((items) =>
          items.map((item) => (item.id === updated.id ? updated : item)),
        );
      },
      error: (error) => {
        console.error('Error actualizando notificacion alumno', error);
        this.notificationError.set('No se pudo actualizar la notificación.');
      },
    });
  }

  markAllNotificationsAsRead(): void {
    this.notificationService.markAllAsRead('ALUMNO').subscribe({
      next: (notifications) => this.notifications.set(notifications),
      error: (error) => {
        console.error('Error marcando notificaciones alumno', error);
        this.notificationError.set('No se pudieron actualizar las notificaciones.');
      },
    });
  }

  closeNotifications(): void {
    this.notificationMenuOpen.set(false);
  }

  formattedNotificationDate(value: string): string {
    return new Intl.DateTimeFormat('es-PE', {
      day: '2-digit',
      month: 'short',
      hour: '2-digit',
      minute: '2-digit',
    }).format(new Date(value));
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}
