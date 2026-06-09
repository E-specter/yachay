import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { AdminUser, AdminUserStatus } from '../../../../core/models/user.models';
import { UserService } from '../../../../core/services/user';

type UserStatusFilter = AdminUserStatus | 'TODOS';

@Component({
  selector: 'app-usuarios',
  imports: [],
  templateUrl: './usuarios.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Usuarios {
  private readonly userService = inject(UserService);

  readonly search = signal('');
  readonly statusFilter = signal<UserStatusFilter>('TODOS');
  readonly users = signal<AdminUser[]>([]);
  readonly loading = signal(false);
  readonly errorMessage = signal('');

  readonly filteredUsers = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.users().filter((user) => {
      const matchesStatus = status === 'TODOS' || user.estado === status;
      const searchable = `${user.nombres} ${user.apellidos} ${user.email} ${user.rol}`.toLowerCase();

      return matchesStatus && searchable.includes(query);
    });
  });

  constructor() {
    this.loadUsers();
  }

  updateSearch(event: Event): void {
    this.search.set((event.target as HTMLInputElement).value);
  }

  updateStatusFilter(event: Event): void {
    this.statusFilter.set((event.target as HTMLSelectElement).value as UserStatusFilter);
  }

  statusClass(status: AdminUserStatus): string {
    return status === 'ACTIVO'
      ? 'border-green-200 bg-green-50 text-green-700'
      : 'border-slate-200 bg-slate-50 text-slate-600';
  }

  nextStatusLabel(status: AdminUserStatus): string {
    return status === 'ACTIVO' ? 'Inactivar' : 'Activar';
  }

  private loadUsers(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.userService.getUsers().subscribe({
      next: (users) => {
        this.users.set(users);
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.errorMessage.set(
          'No se pudo conectar con el servidor. Verifica que el backend este activo en http://localhost:8080/api y que MySQL este iniciado.',
        );

        if (error instanceof HttpErrorResponse) {
          console.error('Error cargando usuarios', {
            status: error.status,
            statusText: error.statusText,
            url: error.url,
            message: error.message,
            error: error.error,
          });
        }
      },
    });
  }
}
