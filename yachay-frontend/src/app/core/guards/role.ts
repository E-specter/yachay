import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { UserRole } from '../models/auth.models';
import { AuthService } from '../services/auth';

const dashboardByRole: Record<UserRole, string> = {
  ADMINISTRADOR: '/admin/dashboard',
  DOCENTE: '/docente/dashboard',
  ALUMNO: '/alumno/dashboard',
};

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const allowedRoles = route.data['roles'] as readonly UserRole[] | undefined;

  if (!allowedRoles?.length) {
    return true;
  }

  const user = authService.user();

  if (!user) {
    return router.createUrlTree(['/login']);
  }

  if (allowedRoles.includes(user.role)) {
    return true;
  }

  return router.createUrlTree([dashboardByRole[user.role]]);
};
