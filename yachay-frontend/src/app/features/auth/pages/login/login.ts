import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { AuthService } from '../../../../core/services/auth';
import { UserRole } from '../../../../core/models/auth.models';

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
      error: () => {
        this.loading.set(false);
        this.errorMessage.set(
          'Credenciales incorrectas. Verifica tu correo institucional y contraseña.',
        );
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
}
