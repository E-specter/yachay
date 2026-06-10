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
  selector: 'app-admin-layout',
  imports: [RouterLink, RouterLinkActive, RouterOutlet, AppIcon],
  templateUrl: './admin-layout.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminLayout implements OnInit {
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
  readonly roleLabel = 'Administrador';
  readonly sectionTitle = 'Panel administrativo';
  readonly profilePath = '/admin/perfil';
  readonly notificationPreview = computed(() => this.notifications().slice(0, 5));
  readonly notificationCount = computed(() => {
    const unread = this.notifications().filter((notification) => !notification.read).length;
    if (unread === 0) return '0';
    return unread > 9 ? '9+' : String(unread);
  });

  readonly navItems = [
    { label: 'Dashboard', path: '/admin/dashboard', exact: true, icon: 'dashboard' },
    { label: 'Postulaciones', path: '/admin/postulaciones', exact: false, icon: 'plus' },
    { label: 'Usuarios', path: '/admin/usuarios', exact: false, icon: 'user' },
    { label: 'Alumnos', path: '/admin/alumnos', exact: false, icon: 'profile' },
    { label: 'Docentes', path: '/admin/docentes', exact: false, icon: 'user' },
    { label: 'Cursos', path: '/admin/cursos', exact: false, icon: 'courses' },
    { label: 'Secciones', path: '/admin/secciones', exact: false, icon: 'calendar' },
    { label: 'Tareas', path: '/admin/tareas', exact: false, icon: 'tasks' },
    { label: 'Notas', path: '/admin/notas', exact: false, icon: 'grades' },
    { label: 'Calendario', path: '/admin/calendario', exact: false, icon: 'calendar' },
    { label: 'Comunicados', path: '/admin/comunicados', exact: false, icon: 'announcements' },
    { label: 'Notificaciones', path: '/admin/notificaciones', exact: false, icon: 'notification' },
    { label: 'Configuración', path: '/admin/configuracion', exact: false, icon: 'settings' },
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
    this.notificationService.list('ADMINISTRADOR').subscribe({
      next: (notifications) => this.notifications.set(notifications),
      error: (error) => {
        console.error('Error cargando notificaciones administrador', error);
        this.notificationError.set('No se pudieron cargar las notificaciones.');
      },
    });
  }

  markNotificationAsRead(notification: UserNotification): void {
    if (notification.read) return;

    this.notificationService.markAsRead('ADMINISTRADOR', notification.id).subscribe({
      next: (updated) => {
        this.notifications.update((items) =>
          items.map((item) => (item.id === updated.id ? updated : item)),
        );
      },
      error: (error) => {
        console.error('Error actualizando notificacion administrador', error);
        this.notificationError.set('No se pudo actualizar la notificación.');
      },
    });
  }

  markAllNotificationsAsRead(): void {
    this.notificationService.markAllAsRead('ADMINISTRADOR').subscribe({
      next: (notifications) => this.notifications.set(notifications),
      error: (error) => {
        console.error('Error marcando notificaciones administrador', error);
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
