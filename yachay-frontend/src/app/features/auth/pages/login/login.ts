import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { UserRole } from '../../../../core/models/auth.models';
import { AuthService } from '../../../../core/services/auth';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly loading = signal(false);
  readonly submitted = signal(false);
  readonly errorMessage = signal('');

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  login(): void {
    this.submitted.set(true);
    this.errorMessage.set('');
    this.form.markAllAsTouched();

    if (this.form.invalid) return;

    this.loading.set(true);

    this.authService.login(this.form.getRawValue()).subscribe({
      next: (response) => {
        this.loading.set(false);
        this.router.navigateByUrl(this.homeRouteByRole(response.user.role));
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.errorMessage.set(this.loginErrorMessage(error));
      },
    });
  }

  campoInvalido(controlName: 'email' | 'password'): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.touched || this.submitted());
  }

  private homeRouteByRole(role: UserRole): string {
    const routes: Record<UserRole, string> = {
      ADMINISTRADOR: '/admin/dashboard',
      DOCENTE: '/docente/dashboard',
      ALUMNO: '/alumno/dashboard',
    };

    return routes[role];
  }

  private loginErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      console.error('Error iniciando sesion', {
        status: error.status,
        statusText: error.statusText,
        url: error.url,
        message: error.message,
        error: error.error,
      });

      if (error.status === 401 || error.status === 403 || error.status === 400) {
        return 'Credenciales incorrectas.';
      }

      if (error.status === 0) {
        return 'No se pudo conectar con el servidor. Verifica que el backend este activo en http://localhost:8080/api y que MySQL este iniciado.';
      }
    }

    return 'No se pudo conectar con el servidor. Verifica que el backend este activo en http://localhost:8080/api y que MySQL este iniciado.';
  }
}
