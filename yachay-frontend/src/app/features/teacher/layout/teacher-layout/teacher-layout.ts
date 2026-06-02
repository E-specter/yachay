import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';

import { AuthService } from '../../../../core/services/auth';
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
export class TeacherLayout {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly sidebarCollapsed = signal(false);
  readonly mobileSidebarOpen = signal(false);
  readonly userMenuOpen = signal(false);

  readonly userName = 'Jesus Gabriel';
  readonly roleLabel = 'Docente';
  readonly sectionTitle = 'Panel docente';
  readonly profilePath = '/docente/perfil';
  readonly notificationCount = '9+';

  readonly navItems = [
    { label: 'Dashboard', path: '/docente/dashboard', exact: true, icon: 'dashboard' },
    { label: 'Mis cursos', path: '/docente/cursos', exact: false, icon: 'courses' },
    { label: 'Mis alumnos', path: '/docente/alumnos', exact: false, icon: 'profile' },
    { label: 'Tareas', path: '/docente/tareas', exact: false, icon: 'tasks' },
    { label: 'Notas', path: '/docente/notas', exact: false, icon: 'grades' },
    { label: 'Comunicados', path: '/docente/comunicados', exact: false, icon: 'announcements' },
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
  }

  closeUserMenu(): void {
    this.userMenuOpen.set(false);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}
