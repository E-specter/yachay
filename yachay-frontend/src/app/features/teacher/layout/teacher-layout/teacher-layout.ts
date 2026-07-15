import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
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
  selector: 'app-teacher-layout',
  imports: [RouterLink, RouterLinkActive, RouterOutlet, AppIcon],
  templateUrl: './teacher-layout.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TeacherLayout implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  readonly sidebarCollapsed = signal(false);
  readonly mobileSidebarOpen = signal(false);
  readonly userMenuOpen = signal(false);
  readonly notificationMenuOpen = signal(false);
  readonly notifications = signal<UserNotification[]>([]);
  readonly notificationError = signal('');

  readonly userName = computed(() => this.authService.user()?.displayName || 'Docente');
  readonly roleLabel = 'Docente';
  readonly sectionTitle = 'Panel docente';
  readonly profilePath = '/docente/perfil';
  readonly notificationPreview = computed(() => this.notifications().slice(0, 5));
  readonly notificationCount = computed(() => {
    const unread = this.notifications().filter((notification) => !notification.read).length;
    if (unread === 0) return '0';
    return unread > 9 ? '9+' : String(unread);
  });

  readonly navItems = [
    { label: 'Dashboard', path: '/docente/dashboard', exact: true, icon: 'dashboard' },
    { label: 'Mis cursos', path: '/docente/cursos', exact: false, icon: 'courses' },
    { label: 'Mis alumnos', path: '/docente/alumnos', exact: false, icon: 'profile' },
    { label: 'Tareas', path: '/docente/tareas', exact: false, icon: 'tasks' },
    { label: 'Notas', path: '/docente/notas', exact: false, icon: 'grades' },
    { label: 'Calendario', path: '/docente/calendario', exact: false, icon: 'calendar' },
    { label: 'Comunicados', path: '/docente/comunicados', exact: false, icon: 'announcements' },
    { label: 'Notificaciones', path: '/docente/notificaciones', exact: false, icon: 'notification' },
    { label: 'Perfil', path: '/docente/perfil', exact: false, icon: 'user' },
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
    this.notificationService.changes$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((role) => {
        if (role === 'DOCENTE') this.loadNotifications();
      });
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
    this.notificationService.list('DOCENTE').subscribe({
      next: (notifications) => this.notifications.set(notifications),
      error: (error) => {
        console.error('Error cargando notificaciones docente', error);
        this.notificationError.set('No se pudieron cargar las notificaciones.');
      },
    });
  }

  markNotificationAsRead(notification: UserNotification): void {
    if (notification.read) return;

    this.notificationService.markAsRead('DOCENTE', notification.id).subscribe({
      next: (updated) => {
        this.notifications.update((items) =>
          items.map((item) => (item.id === updated.id ? updated : item)),
        );
      },
      error: (error) => {
        console.error('Error actualizando notificacion docente', error);
        this.notificationError.set('No se pudo actualizar la notificación.');
      },
    });
  }

  markAllNotificationsAsRead(): void {
    this.notificationService.markAllAsRead('DOCENTE').subscribe({
      next: (notifications) => this.notifications.set(notifications),
      error: (error) => {
        console.error('Error marcando notificaciones docente', error);
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
