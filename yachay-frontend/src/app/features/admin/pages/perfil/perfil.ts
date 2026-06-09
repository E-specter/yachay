import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../../../core/services/auth';
import { AppIcon } from '../../../../shared/components/app-icon/app-icon';

@Component({
  selector: 'app-admin-perfil',
  imports: [RouterLink, AppIcon],
  templateUrl: './perfil.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminPerfil {
  private readonly authService = inject(AuthService);

  readonly profile = computed(() => {
    const user = this.authService.user();

    return {
      nombres: user?.nombres ?? 'Administrador',
      apellidos: user?.apellidos ?? 'Yachay',
      email: user?.email ?? '',
      rol: user?.role ?? 'ADMINISTRADOR',
      estado: 'ACTIVO',
      fechaCreacion: '2026-05-12',
    };
  });
}
