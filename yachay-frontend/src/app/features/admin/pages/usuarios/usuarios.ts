import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { AdminUser, AdminUserRole, AdminUserStatus } from '../../../../core/models/user.models';
import { UserService } from '../../../../core/services/user';

type UserStatusFilter = AdminUserStatus | 'TODOS';

@Component({
  selector: 'app-usuarios',
  imports: [ReactiveFormsModule],
  templateUrl: './usuarios.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Usuarios {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);

  readonly search = signal('');
  readonly statusFilter = signal<UserStatusFilter>('TODOS');
  readonly users = signal<AdminUser[]>([]);
  readonly loading = signal(false);
  readonly saving = signal(false);
  readonly modalOpen = signal(false);
  readonly editingId = signal<number | null>(null);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');

  readonly form = this.fb.nonNullable.group({
    nombres: ['', Validators.required],
    apellidos: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    passwordTemporal: ['Yachay123456', Validators.minLength(8)],
    rol: ['ALUMNO', Validators.required],
    activo: [true],
  });

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

  openCreateModal(): void {
    this.editingId.set(null);
    this.form.reset({
      nombres: '',
      apellidos: '',
      email: '',
      passwordTemporal: 'Yachay123456',
      rol: 'ALUMNO',
      activo: true,
    });
    this.errorMessage.set('');
    this.modalOpen.set(true);
  }

  editUser(user: AdminUser): void {
    this.editingId.set(user.id);
    this.form.reset({ nombres: user.nombres, apellidos: user.apellidos, email: user.email, passwordTemporal: '', rol: user.rol, activo: user.estado === 'ACTIVO' });
    this.errorMessage.set('');
    this.modalOpen.set(true);
  }

  closeCreateModal(): void {
    if (this.saving()) return;
    this.modalOpen.set(false);
  }

  createUser(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const raw = this.form.getRawValue();
    const request = this.editingId() ? this.userService.updateUser(this.editingId()!, {
      nombres: raw.nombres, apellidos: raw.apellidos, email: raw.email,
      passwordTemporal: raw.passwordTemporal || undefined,
      rol: raw.rol as AdminUserRole, activo: raw.activo,
      estado: raw.activo ? 'ACTIVO' : 'INACTIVO',
    }) : this.userService.createUser({
      nombres: raw.nombres,
      apellidos: raw.apellidos,
      email: raw.email,
      passwordTemporal: raw.passwordTemporal,
      rol: raw.rol as AdminUserRole,
      activo: raw.activo,
    });
    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.modalOpen.set(false);
        this.successMessage.set(this.editingId() ? 'Usuario actualizado correctamente.' : 'Registro creado correctamente.');
        this.loadUsers();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  toggleStatus(user: AdminUser): void {
    const estado: AdminUserStatus = user.estado === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';
    this.userService.updateStatus(user.id, { estado }).subscribe({
      next: () => {
        this.successMessage.set('Estado actualizado correctamente.');
        this.loadUsers();
      },
      error: (error) => this.handleSaveError(error),
    });
  }

  resetPassword(user: AdminUser): void {
    this.userService.resetPassword(user.id).subscribe({
      next: (response) => {
        this.successMessage.set(`Contraseña temporal generada: ${response.temporaryPassword}`);
      },
      error: (error) => this.handleSaveError(error),
    });
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

    this.userService.list().subscribe({
      next: (users) => {
        this.users.set(users);
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.handleLoadError(error, 'Error cargando usuarios');
      },
    });
  }

  private handleSaveError(error: unknown): void {
    this.saving.set(false);
    this.errorMessage.set('No se pudo guardar. Verifica los datos o la conexión con el servidor.');
    this.logHttpError('Error guardando usuario', error);
  }

  private handleLoadError(error: unknown, label: string): void {
    this.errorMessage.set(
      'No se pudo conectar con el servidor. Verifica que el backend esté activo en http://localhost:8080/api y que MySQL esté iniciado.',
    );
    this.logHttpError(label, error);
  }

  private logHttpError(label: string, error: unknown): void {
    if (error instanceof HttpErrorResponse) {
      console.error(label, {
        status: error.status,
        statusText: error.statusText,
        url: error.url,
        message: error.message,
        error: error.error,
      });
    }
  }
}
