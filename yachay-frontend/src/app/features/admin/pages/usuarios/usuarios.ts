import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';

import { AdminUser, AdminUserStatus } from '../../../../core/models/user.models';

type UserStatusFilter = AdminUserStatus | 'TODOS';

@Component({
  selector: 'app-usuarios',
  imports: [],
  templateUrl: './usuarios.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Usuarios {
  readonly search = signal('');
  readonly statusFilter = signal<UserStatusFilter>('TODOS');

  readonly users: readonly AdminUser[] = [
    {
      id: 1,
      nombres: 'Administrador',
      apellidos: 'Yachay',
      email: 'admin@mgp.edu.pe',
      rol: 'ADMINISTRADOR',
      estado: 'ACTIVO',
      fechaCreacion: '2026-04-30',
    },
    {
      id: 2,
      nombres: 'Rosa Elena',
      apellidos: 'Vargas Medina',
      email: 'rvargas@mgp.edu.pe',
      rol: 'DOCENTE',
      estado: 'ACTIVO',
      fechaCreacion: '2026-05-01',
    },
    {
      id: 3,
      nombres: 'Luis Alberto',
      apellidos: 'Torres Quispe',
      email: 'ltorres@mgp.edu.pe',
      rol: 'ALUMNO',
      estado: 'INACTIVO',
      fechaCreacion: '2026-05-02',
    },
  ];

  readonly filteredUsers = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();

    return this.users.filter((user) => {
      const matchesStatus = status === 'TODOS' || user.estado === status;
      const searchable = `${user.nombres} ${user.apellidos} ${user.email} ${user.rol}`.toLowerCase();

      return matchesStatus && searchable.includes(query);
    });
  });

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
}
